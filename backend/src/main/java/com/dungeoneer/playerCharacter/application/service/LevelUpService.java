package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.AddExperienceCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.LevelUpCommand;
import com.dungeoneer.playerCharacter.application.port.in.AddExperienceUseCase;
import com.dungeoneer.playerCharacter.application.port.in.LevelUpUseCase;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import com.dungeoneer.playerCharacter.domain.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LevelUpService implements AddExperienceUseCase, LevelUpUseCase {

    private final PlayerCharacterRepositoryPort characterRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final CharacterSheetHydrator sheetHydrator;
    private final HpRollRepositoryPort hpRollRepository;

    @Autowired
    public LevelUpService(
            PlayerCharacterRepositoryPort characterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator sheetHydrator,
            HpRollRepositoryPort hpRollRepository
    ) {
        this.characterRepository = characterRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.sheetHydrator = sheetHydrator;
        this.hpRollRepository = hpRollRepository;
    }

    public LevelUpService(
            PlayerCharacterRepositoryPort characterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator sheetHydrator
    ) {
        this(characterRepository, referenceDataRepository, sheetHydrator, null);
    }

    @Override
    public CharacterSheetResult addExperience(UUID characterId, UUID userId, AddExperienceCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);
        PlayerCharacter updated = character.addExperience(command.amount());
        PlayerCharacter saved = characterRepository.save(updated);
        return sheetHydrator.hydrate(saved);
    }

    @Override
    public CharacterSheetResult levelUp(UUID characterId, UUID userId, LevelUpCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);
        int currentTotalLevel = character.getLevel();
        if (currentTotalLevel >= 20) {
            throw new IllegalArgumentException("Character has already reached maximum level (20)");
        }

        // Verify archetype
        Archetype archetype = referenceDataRepository.findArchetypeById(command.archetypeId())
                .orElseThrow(() -> new IllegalArgumentException("Archetype not found: " + command.archetypeId()));

        // Resolve class progression
        List<ClassProgression> progressions = new ArrayList<>(character.getClassProgressions());
        int targetClassIndex = -1;
        for (int i = 0; i < progressions.size(); i++) {
            if (progressions.get(i).getArchetypeId().equals(command.archetypeId())) {
                targetClassIndex = i;
                break;
            }
        }

        int newClassLevel;
        UUID activeSubclassId = command.subclassId();
        if (targetClassIndex >= 0) {
            ClassProgression existing = progressions.get(targetClassIndex);
            newClassLevel = existing.getClassLevel() + 1;
            if (activeSubclassId == null) {
                activeSubclassId = existing.getSubclassId();
            }
            progressions.set(targetClassIndex, existing.withClassLevel(newClassLevel).withSubclassId(activeSubclassId));
        } else {
            // Unrestricted Multiclassing: add 1st level in new class
            newClassLevel = 1;
            progressions.add(new ClassProgression(
                    UUID.randomUUID(),
                    characterId,
                    archetype.getId(),
                    activeSubclassId,
                    newClassLevel
            ));
        }

        // Validate subclass requirement if archetype requires subclass at this level
        if (newClassLevel >= archetype.getSubclassLevel() && activeSubclassId == null) {
            List<Subclass> availableSubclasses = referenceDataRepository.findSubclassesByArchetypeId(archetype.getId());
            if (!availableSubclasses.isEmpty()) {
                throw new IllegalArgumentException("Subclass selection is required for archetype " + archetype.getName() + " at level " + newClassLevel);
            }
        }

        int newTotalLevel = currentTotalLevel + 1;

        // Apply Ability Score Improvement (ASI) if provided
        AbilityScores newAbilityScores = character.getAbilityScores();
        if (command.abilityScoreIncreases() != null && !command.abilityScoreIncreases().isEmpty()) {
            newAbilityScores = applyAbilityScoreIncreases(newAbilityScores, command.abilityScoreIncreases());
        }

        // Resolve selected Feat (if chosen at ASI level) and apply its MechanicalEffects
        Feat selectedFeat = null;
        int retroactiveFeatHpBonus = 0;
        if (command.selectedFeatId() != null) {
            selectedFeat = referenceDataRepository.findFeatById(command.selectedFeatId())
                    .orElseThrow(() -> new IllegalArgumentException("Selected feat not found: " + command.selectedFeatId()));
            MechanicalEffects featEffects = selectedFeat.getMechanicalEffects();
            if (featEffects != null) {
                if (featEffects.abilityBonuses() != null && !featEffects.abilityBonuses().isEmpty()) {
                    newAbilityScores = applyAbilityScoreIncreases(newAbilityScores, featEffects.abilityBonuses());
                }
                if (featEffects.hpPerLevelBonus() > 0) {
                    retroactiveFeatHpBonus += featEffects.hpPerLevelBonus() * newTotalLevel;
                }
            }
        }

        // Sum hpPerLevelBonus from existing character feats
        int existingHpPerLevelBonus = 0;
        for (CharacterFeat cf : character.getFeats()) {
            Feat existingFeat = referenceDataRepository.findFeatById(cf.getFeatId()).orElse(null);
            if (existingFeat != null && existingFeat.getMechanicalEffects() != null) {
                existingHpPerLevelBonus += existingFeat.getMechanicalEffects().hpPerLevelBonus();
            }
        }

        // Server-Authority HP Gain locked to character.getHpCalculationMethod()
        HpCalculationMethod effectiveHpMethod = character.getHpCalculationMethod() != null
                ? character.getHpCalculationMethod()
                : (command.hpCalculationMethod() != null ? command.hpCalculationMethod() : HpCalculationMethod.AVERAGE);

        int baseHpGain;
        if (effectiveHpMethod == HpCalculationMethod.MAX) {
            baseHpGain = archetype.getHitDice().getDieValue();
        } else if (effectiveHpMethod == HpCalculationMethod.ROLLED) {
            if (command.hpRollId() == null) {
                throw new InvalidRollTokenException("HpRollId is required when character HP calculation method is ROLLED.");
            }
            if (hpRollRepository != null) {
                HpRoll hpRoll = hpRollRepository.findById(command.hpRollId())
                        .orElseThrow(() -> new InvalidRollTokenException("HP roll token not found: " + command.hpRollId()));
                if (!userId.equals(hpRoll.getUserId())) {
                    throw new InvalidRollTokenException("HP roll token does not belong to the user.");
                }
                if (hpRoll.isUsed()) {
                    throw new RollConflictException("HP roll token has already been used: " + command.hpRollId());
                }
                if (!hpRoll.getArchetypeId().equals(archetype.getId())) {
                    throw new InvalidRollTokenException("HP roll token does not match the selected archetype.");
                }
                hpRollRepository.markAsUsed(hpRoll.getId());
                List<Integer> rolledValues = hpRoll.getRolledHpPerLevel();
                baseHpGain = (rolledValues != null && !rolledValues.isEmpty())
                        ? rolledValues.get(rolledValues.size() - 1)
                        : ((archetype.getHitDice().getDieValue() / 2) + 1);
            } else {
                baseHpGain = command.hpIncrease() > 0
                        ? command.hpIncrease()
                        : ((archetype.getHitDice().getDieValue() / 2) + 1);
            }
        } else {
            // AVERAGE
            baseHpGain = (archetype.getHitDice().getDieValue() / 2) + 1;
        }

        int oldConMod = character.getAbilityScores().getConstitutionModifier();
        int newConMod = newAbilityScores.getConstitutionModifier();
        int retroactiveConHpBonus = Math.max(0, newConMod - oldConMod) * currentTotalLevel;

        int totalHpGain = Math.max(1, baseHpGain + newConMod)
                + existingHpPerLevelBonus
                + retroactiveFeatHpBonus
                + retroactiveConHpBonus;
        int newMaxHp = character.getHitPointsMax() + totalHpGain;

        RulesEngine rulesEngine = character.getRuleset() == Ruleset.SRD_2024
                ? new Srd2024RulesEngine()
                : new Srd2014RulesEngine();
        int requiredXpForNewLevel = rulesEngine.getXpForLevel(newTotalLevel);
        int updatedXp = command.milestone()
                ? Math.max(character.getExperiencePoints(), requiredXpForNewLevel)
                : character.getExperiencePoints();
        boolean hasPendingLevelChoices = newTotalLevel < 20
                && updatedXp >= rulesEngine.getXpForLevel(newTotalLevel + 1);

        // Create updated character domain entity
        PlayerCharacter updated = character.withLevelUp(
                newTotalLevel,
                newMaxHp,
                newAbilityScores,
                progressions,
                character.getFeats(),
                updatedXp,
                character.getArmorClass(),
                hasPendingLevelChoices
        );
        PlayerCharacter saved = characterRepository.save(updated);

        // Unlock progression feats for the archetype at newClassLevel
        List<Feat> archetypeFeats = referenceDataRepository.findProgressionFeats(archetype.getId(), newClassLevel);
        if (archetypeFeats != null) {
            for (Feat feat : archetypeFeats) {
                if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(feat.getId()))) {
                    String source = feat.getSource() != null ? feat.getSource() : "CLASS";
                    characterRepository.saveCharacterFeat(new CharacterFeat(
                            UUID.randomUUID(),
                            characterId,
                            feat.getId(),
                            newTotalLevel,
                            feat.getMechanicalEffects().usesPerRest(),
                            feat.getMechanicalEffects().usesPerRest(),
                            String.format("{\"source\":\"%s\"}", source)
                    ));
                }
            }
        }

        // Unlock subclass feats if activeSubclassId is present
        if (activeSubclassId != null) {
            List<Feat> subclassFeats = referenceDataRepository.findSubclassProgressionFeats(activeSubclassId, newClassLevel);
            if (subclassFeats != null) {
                for (Feat feat : subclassFeats) {
                    if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(feat.getId()))) {
                        characterRepository.saveCharacterFeat(new CharacterFeat(
                                UUID.randomUUID(),
                                characterId,
                                feat.getId(),
                                newTotalLevel,
                                feat.getMechanicalEffects().usesPerRest(),
                                feat.getMechanicalEffects().usesPerRest(),
                                "{\"source\":\"SUBCLASS\"}"
                        ));
                    }
                }
            }
        }

        // Unlock lineage feats for lineage at newTotalLevel
        List<Feat> lineageFeats = referenceDataRepository.findLineageFeats(character.getLineageId(), newTotalLevel);
        if (lineageFeats != null) {
            for (Feat feat : lineageFeats) {
                if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(feat.getId()))) {
                    characterRepository.saveCharacterFeat(new CharacterFeat(
                            UUID.randomUUID(),
                            characterId,
                            feat.getId(),
                            newTotalLevel,
                            feat.getMechanicalEffects().usesPerRest(),
                            feat.getMechanicalEffects().usesPerRest(),
                            "{\"source\":\"LINEAGE\"}"
                    ));
                }
            }
        }

        // Save selected bonus feat if chosen at ASI level
        if (selectedFeat != null) {
            List<UUID> currentArchIds = progressions.stream().map(ClassProgression::getArchetypeId).toList();
            validateFeatPrerequisites(selectedFeat, newAbilityScores, newTotalLevel, currentArchIds, character.getLineageId(), character.getFeats());
            final Feat featToSave = selectedFeat;
            if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(featToSave.getId()))) {
                characterRepository.saveCharacterFeat(new CharacterFeat(
                        UUID.randomUUID(),
                        characterId,
                        featToSave.getId(),
                        newTotalLevel,
                        featToSave.getMechanicalEffects().usesPerRest(),
                        featToSave.getMechanicalEffects().usesPerRest(),
                        "{\"source\":\"FEAT\"}"
                ));
            }
        }

        // Reload updated character with fresh feats and items
        PlayerCharacter fresh = characterRepository.findByIdAndUserId(characterId, userId)
                .orElse(saved);

        return sheetHydrator.hydrate(fresh);
    }

    private AbilityScores applyAbilityScoreIncreases(AbilityScores baseScores, Map<String, Integer> increases) {
        Map<String, Integer> currentScores = new LinkedHashMap<>();
        currentScores.put("STRENGTH", baseScores.getStrength());
        currentScores.put("DEXTERITY", baseScores.getDexterity());
        currentScores.put("CONSTITUTION", baseScores.getConstitution());
        currentScores.put("INTELLIGENCE", baseScores.getIntelligence());
        currentScores.put("WISDOM", baseScores.getWisdom());
        currentScores.put("CHARISMA", baseScores.getCharisma());

        for (Map.Entry<String, Integer> entry : increases.entrySet()) {
            String key = Srd2014RulesEngine.normalizeAbility(entry.getKey());
            int delta = entry.getValue() != null ? entry.getValue() : 0;
            int currentVal = currentScores.getOrDefault(key, 10);
            currentScores.put(key, Math.min(20, currentVal + delta));
        }
        return new AbilityScores(
                currentScores.get("STRENGTH"),
                currentScores.get("DEXTERITY"),
                currentScores.get("CONSTITUTION"),
                currentScores.get("INTELLIGENCE"),
                currentScores.get("WISDOM"),
                currentScores.get("CHARISMA")
        );
    }

    private PlayerCharacter getCharacter(UUID characterId, UUID userId) {
        return characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + characterId));
    }

    private void validateFeatPrerequisites(
            Feat feat,
            AbilityScores scores,
            int level,
            List<UUID> archetypeIds,
            UUID lineageId,
            List<CharacterFeat> currentFeats
    ) {
        if (feat.getMechanicalEffects() == null || feat.getMechanicalEffects().prerequisiteRules() == null) {
            return;
        }
        for (FeatPrerequisite rule : feat.getMechanicalEffects().prerequisiteRules()) {
            if (rule == null || rule.type() == null) continue;
            switch (rule.type().toUpperCase()) {
                case "ABILITY_SCORE" -> {
                    if (rule.targetKey() != null && rule.minValue() != null && scores != null) {
                        int score = scores.getScore(rule.targetKey());
                        if (score < rule.minValue()) {
                            throw new CharacterValidationException(
                                    "Pré-requisito do talento não atendido: " + (rule.displayLabel() != null ? rule.displayLabel() : (rule.targetKey() + " >= " + rule.minValue()))
                            );
                        }
                    }
                }
                case "MIN_LEVEL" -> {
                    if (rule.minValue() != null && level < rule.minValue()) {
                        throw new CharacterValidationException(
                                "Pré-requisito do talento não atendido: " + (rule.displayLabel() != null ? rule.displayLabel() : ("Nível mínimo " + rule.minValue()))
                        );
                    }
                }
                case "ARCHETYPE" -> {
                    if (rule.targetKey() != null && archetypeIds != null && !archetypeIds.isEmpty()) {
                        boolean matches = false;
                        for (UUID archId : archetypeIds) {
                            if (archId != null && archId.toString().equalsIgnoreCase(rule.targetKey())) {
                                matches = true;
                                break;
                            }
                        }
                        if (!matches) {
                            throw new CharacterValidationException(
                                    "Pré-requisito do talento não atendido: " + (rule.displayLabel() != null ? rule.displayLabel() : "Classe necessária")
                            );
                        }
                    }
                }
                case "LINEAGE" -> {
                    if (rule.targetKey() != null && lineageId != null) {
                        if (!lineageId.toString().equalsIgnoreCase(rule.targetKey())) {
                            throw new CharacterValidationException(
                                    "Pré-requisito do talento não atendido: " + (rule.displayLabel() != null ? rule.displayLabel() : "Linhagem necessária")
                            );
                        }
                    }
                }
                case "FEAT" -> {
                    if (rule.targetKey() != null) {
                        boolean hasFeat = currentFeats.stream()
                                .anyMatch(f -> f.getFeatId() != null && f.getFeatId().toString().equalsIgnoreCase(rule.targetKey()));
                        if (!hasFeat) {
                            throw new CharacterValidationException(
                                    "Pré-requisito do talento não atendido: " + (rule.displayLabel() != null ? rule.displayLabel() : "Talento pré-requisito necessário")
                            );
                        }
                    }
                }
            }
        }
    }
}
