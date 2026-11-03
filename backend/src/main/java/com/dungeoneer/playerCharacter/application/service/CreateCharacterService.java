package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.ClassProgressionCommand;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.out.AbilityRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreValidator;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.CurrencyPouch;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.FeatChargeCalculator;
import com.dungeoneer.playerCharacter.domain.model.FeatPrerequisite;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.HpRoll;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.RulesEngine;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Srd2014RulesEngine;
import com.dungeoneer.playerCharacter.domain.model.Srd2024RulesEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class CreateCharacterService implements CreateCharacterUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final AbilityRollRepositoryPort abilityRollRepository;
    private final HpRollRepositoryPort hpRollRepository;

    public CreateCharacterService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            ReferenceDataRepository referenceDataRepository,
            AbilityRollRepositoryPort abilityRollRepository,
            HpRollRepositoryPort hpRollRepository
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.abilityRollRepository = abilityRollRepository;
        this.hpRollRepository = hpRollRepository;
    }

    @Override
    public CharacterSheetResult createCharacter(CreateCharacterCommand command) {
        if (command.userId() == null) {
            throw new CharacterValidationException("UserId is required.");
        }

        Archetype archetype = referenceDataRepository.findArchetypeById(command.archetypeId())
                .orElseThrow(() -> new CharacterValidationException("Archetype not found: " + command.archetypeId()));

        Lineage lineage = referenceDataRepository.findLineageById(command.lineageId())
                .orElseThrow(() -> new CharacterValidationException("Lineage not found: " + command.lineageId()));

        Background background = referenceDataRepository.findBackgroundById(command.backgroundId())
                .orElseThrow(() -> new CharacterValidationException("Background not found: " + command.backgroundId()));

        AbilityScores baseScores = new AbilityScores(
                command.strength(),
                command.dexterity(),
                command.constitution(),
                command.intelligence(),
                command.wisdom(),
                command.charisma()
        );

        AbilityRoll abilityRoll = null;
        if (command.abilityScoreMethod() == AbilityScoreMethod.ROLLED) {
            if (command.abilityRollId() == null) {
                throw new InvalidRollTokenException("AbilityRollId is required for ROLLED ability score method.");
            }
            abilityRoll = abilityRollRepository.findById(command.abilityRollId())
                    .orElseThrow(() -> new InvalidRollTokenException("Ability roll not found with id: " + command.abilityRollId()));

            if (!command.userId().equals(abilityRoll.getUserId())) {
                throw new InvalidRollTokenException("Ability roll does not belong to the user.");
            }
            if (abilityRoll.isUsed()) {
                throw new RollConflictException("Ability roll token has already been used: " + command.abilityRollId());
            }

            AbilityScoreValidator.validate(AbilityScoreMethod.ROLLED, baseScores, abilityRoll.getRolledScores());
        } else {
            AbilityScoreValidator.validate(command.abilityScoreMethod(), baseScores, null);
        }

        RulesEngine rulesEngine = (command.ruleset() == Ruleset.SRD_2024)
                ? new Srd2024RulesEngine()
                : new Srd2014RulesEngine();

        AbilityScores finalScores = rulesEngine.applyAbilityScoreIncreases(
                baseScores,
                lineage,
                background,
                command.abilityScoreBonuses()
        );

        HpCalculationMethod hpMethod = command.hpCalculationMethod() != null
                ? command.hpCalculationMethod()
                : HpCalculationMethod.MAX;

        // Collect Level 1 unlocked feats (archetype level 1 + subclass level 1 if applicable + originFeatId)
        UUID initialSubclassId = (archetype.getSubclassLevel() == 1) ? command.subclassId() : null;
        List<Feat> initialFeats = new ArrayList<>();
        List<Feat> level1ArchetypeFeats = referenceDataRepository.findProgressionFeats(archetype.getId(), 1);
        if (level1ArchetypeFeats != null) {
            initialFeats.addAll(level1ArchetypeFeats);
        }
        if (initialSubclassId != null) {
            List<Feat> level1SubclassFeats = referenceDataRepository.findSubclassProgressionFeats(initialSubclassId, 1);
            if (level1SubclassFeats != null) {
                initialFeats.addAll(level1SubclassFeats);
            }
        }
        List<Feat> level1LineageFeats = referenceDataRepository.findLineageFeats(lineage.getId(), 1);
        if (level1LineageFeats != null) {
            initialFeats.addAll(level1LineageFeats);
        }
        if (command.originFeatId() != null) {
            var featOpt = referenceDataRepository.findFeatById(command.originFeatId());
            if (featOpt.isPresent()) {
                Feat f = featOpt.get();
                validateFeatPrerequisites(f, finalScores, command.level() != null ? command.level() : 1, List.of(archetype.getId()), lineage.getId(), initialFeats);
                initialFeats.add(f);
            }
        }

        int initialHpPerLevelBonus = 0;
        int initialAcBonus = 0;
        int initialInitBonus = 0;
        int initialSpeedBonus = 0;
        Map<String, Integer> featAbilityBonuses = new LinkedHashMap<>();

        for (Feat feat : initialFeats) {
            MechanicalEffects effects = feat.getMechanicalEffects();
            if (effects != null) {
                initialHpPerLevelBonus += effects.hpPerLevelBonus();
                initialAcBonus += effects.acBonus();
                initialInitBonus += effects.initiativeBonus();
                initialSpeedBonus += effects.speedBonus();
                if (effects.abilityBonuses() != null) {
                    for (Map.Entry<String, Integer> entry : effects.abilityBonuses().entrySet()) {
                        featAbilityBonuses.merge(entry.getKey().toUpperCase(), entry.getValue(), Integer::sum);
                    }
                }
            }
        }

        if (!featAbilityBonuses.isEmpty()) {
            finalScores = applyAbilityMap(finalScores, featAbilityBonuses);
        }

        boolean hasExplicitMulticlassProgressions = command.classProgressions() != null && !command.classProgressions().isEmpty();
        boolean hasExplicitHpRolls = command.hpRollId() != null || (command.hpRollIds() != null && !command.hpRollIds().isEmpty());

        // New Level > 1 Creation Rule: when created from the wizard without explicit multiclass progressions or pre-rolled HP tokens,
        // initialize at Level 1 in the primary class and grant the target level's XP floor so levels 2..N are resolved via LevelUpWizard.
        boolean deferHigherLevelsToLevelUpWizard = command.level() > 1 && !hasExplicitMulticlassProgressions && !hasExplicitHpRolls;
        int effectiveInitialLevel = deferHigherLevelsToLevelUpWizard ? 1 : command.level();
        int initialExperiencePoints = deferHigherLevelsToLevelUpWizard ? rulesEngine.getXpForLevel(command.level()) : 0;

        List<ClassProgression> progressions;
        if (hasExplicitMulticlassProgressions) {
            int totalClassLevel = command.classProgressions().stream()
                    .mapToInt(cp -> cp.classLevel() != null ? cp.classLevel() : 0)
                    .sum();
            if (totalClassLevel != command.level()) {
                throw new CharacterValidationException("Sum of class progression levels (" + totalClassLevel + ") must equal character level (" + command.level() + ").");
            }
            progressions = command.classProgressions().stream()
                    .map(cp -> new ClassProgression(UUID.randomUUID(), null, cp.archetypeId(), cp.subclassId(), cp.classLevel()))
                    .toList();
        } else {
            progressions = List.of(ClassProgression.createInitial(
                    command.archetypeId(),
                    initialSubclassId != null ? initialSubclassId : command.subclassId(),
                    effectiveInitialLevel
            ));
        }

        List<HpRoll> usedHpRolls = new ArrayList<>();
        int hitPointsMax;

        boolean isMulticlass = progressions.size() > 1;

        if (isMulticlass) {
            int conMod = finalScores.getConstitutionModifier();
            int totalHp = Math.max(1, archetype.getHitDice().getDieValue() + conMod);

            List<UUID> rollIdCandidates = new ArrayList<>();
            if (command.hpRollIds() != null) {
                rollIdCandidates.addAll(command.hpRollIds());
            }
            if (command.hpRollId() != null && !rollIdCandidates.contains(command.hpRollId())) {
                rollIdCandidates.add(command.hpRollId());
            }

            Map<UUID, HpRoll> rollsByArchetype = new HashMap<>();
            if (hpMethod == HpCalculationMethod.ROLLED && effectiveInitialLevel > 1) {
                for (UUID rId : rollIdCandidates) {
                    HpRoll r = hpRollRepository.findById(rId)
                            .orElseThrow(() -> new InvalidRollTokenException("HP roll not found with id: " + rId));
                    if (!command.userId().equals(r.getUserId())) {
                        throw new InvalidRollTokenException("HP roll does not belong to the user.");
                    }
                    if (r.isUsed()) {
                        throw new RollConflictException("HP roll token has already been used: " + rId);
                    }
                    rollsByArchetype.put(r.getArchetypeId(), r);
                }
            }

            for (ClassProgressionCommand cp : command.classProgressions()) {
                boolean isPrimary = cp.archetypeId().equals(command.archetypeId());
                int additionalLevels = isPrimary ? (cp.classLevel() - 1) : cp.classLevel();
                if (additionalLevels <= 0) {
                    continue;
                }

                Archetype classArch = isPrimary
                        ? archetype
                        : referenceDataRepository.findArchetypeById(cp.archetypeId())
                        .orElseThrow(() -> new CharacterValidationException("Archetype not found: " + cp.archetypeId()));

                if (hpMethod == HpCalculationMethod.MAX) {
                    for (int l = 0; l < additionalLevels; l++) {
                        totalHp += Math.max(1, classArch.getHitDice().getDieValue() + conMod);
                    }
                } else if (hpMethod == HpCalculationMethod.AVERAGE) {
                    for (int l = 0; l < additionalLevels; l++) {
                        totalHp += Math.max(1, classArch.getHitDice().getAverageHp() + conMod);
                    }
                } else if (hpMethod == HpCalculationMethod.ROLLED) {
                    HpRoll roll = rollsByArchetype.get(cp.archetypeId());
                    if (roll == null) {
                        throw new InvalidRollTokenException("Missing HP roll for class: " + classArch.getName());
                    }
                    if (roll.getRolledHpPerLevel() == null || roll.getRolledHpPerLevel().size() != additionalLevels) {
                        throw new InvalidRollTokenException("HP roll for " + classArch.getName() + " requires " + additionalLevels + " rolls.");
                    }
                    for (int rolled : roll.getRolledHpPerLevel()) {
                        totalHp += Math.max(1, rolled + conMod);
                    }
                    if (!usedHpRolls.contains(roll)) {
                        usedHpRolls.add(roll);
                    }
                }
            }
            hitPointsMax = totalHp + (initialHpPerLevelBonus * effectiveInitialLevel);
        } else {
            if (effectiveInitialLevel > 1 && hpMethod == HpCalculationMethod.ROLLED) {
                UUID rollId = command.hpRollId() != null
                        ? command.hpRollId()
                        : (command.hpRollIds() != null && !command.hpRollIds().isEmpty() ? command.hpRollIds().get(0) : null);
                if (rollId == null) {
                    throw new InvalidRollTokenException("HpRollId is required for ROLLED HP calculation method at level > 1.");
                }
                HpRoll hpRoll = hpRollRepository.findById(rollId)
                        .orElseThrow(() -> new InvalidRollTokenException("HP roll not found with id: " + rollId));

                if (!command.userId().equals(hpRoll.getUserId())) {
                    throw new InvalidRollTokenException("HP roll does not belong to the user.");
                }
                if (hpRoll.isUsed()) {
                    throw new RollConflictException("HP roll token has already been used: " + rollId);
                }
                if (!hpRoll.getArchetypeId().equals(command.archetypeId())) {
                    throw new InvalidRollTokenException("HP roll does not match the selected archetype.");
                }
                if (hpRoll.getTargetLevel() != effectiveInitialLevel) {
                    throw new InvalidRollTokenException("HP roll target level (" + hpRoll.getTargetLevel() + ") does not match character level (" + effectiveInitialLevel + ").");
                }

                usedHpRolls.add(hpRoll);
                hitPointsMax = rulesEngine.calculateMaxHitPoints(
                        effectiveInitialLevel,
                        archetype.getHitDice(),
                        finalScores.getConstitutionModifier(),
                        hpMethod,
                        hpRoll.getRolledHpPerLevel()
                ) + (initialHpPerLevelBonus * effectiveInitialLevel);
            } else {
                hitPointsMax = rulesEngine.calculateMaxHitPoints(
                        effectiveInitialLevel,
                        archetype.getHitDice(),
                        finalScores.getConstitutionModifier(),
                        hpMethod,
                        null
                ) + (initialHpPerLevelBonus * effectiveInitialLevel);
            }
        }

        if (abilityRoll != null) {
            abilityRollRepository.markAsUsed(abilityRoll.getId());
        }
        for (HpRoll r : usedHpRolls) {
            hpRollRepository.markAsUsed(r.getId());
        }

        int armorClass = rulesEngine.calculateArmorClass(finalScores.getDexterityModifier()) + initialAcBonus;
        int initiativeBonus = rulesEngine.calculateInitiativeBonus(finalScores.getDexterityModifier()) + initialInitBonus;
        int speed = rulesEngine.calculateSpeed(lineage) + initialSpeedBonus;
        int proficiencyBonus = rulesEngine.calculateProficiencyBonus(effectiveInitialLevel);
        boolean hasPendingLevelChoices = command.level() > 1;

        UUID characterId = UUID.randomUUID();
        List<CharacterFeat> initialCharacterFeats = new ArrayList<>();
        Map<UUID, Feat> featMap = new LinkedHashMap<>();
        Set<UUID> addedFeatIds = new HashSet<>();
        for (Feat feat : initialFeats) {
            if (feat != null && feat.getId() != null && addedFeatIds.add(feat.getId())) {
                Integer uses = feat.getMechanicalEffects() != null
                        ? FeatChargeCalculator.calculateMaxCharges(
                                feat.getMechanicalEffects(),
                                finalScores,
                                proficiencyBonus,
                                effectiveInitialLevel
                        )
                        : null;
                String sourceName = feat.getFeatType() != null ? feat.getFeatType().name() : "CLASS";
                String choicesJson = String.format("{\"source\":\"%s\"}", sourceName);
                CharacterFeat cf = new CharacterFeat(
                        UUID.randomUUID(),
                        characterId,
                        feat.getId(),
                        1,
                        uses,
                        uses,
                        choicesJson
                );
                initialCharacterFeats.add(cf);
                featMap.put(feat.getId(), feat);
            }
        }

        Instant now = Instant.now();
        PlayerCharacter character = new PlayerCharacter(
                characterId,
                command.name(),
                effectiveInitialLevel,
                hitPointsMax,
                hitPointsMax,
                0,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                command.ruleset(),
                command.abilityScoreMethod(),
                command.lineageId(),
                command.backgroundId(),
                command.userId(),
                finalScores,
                command.notes(),
                hpMethod,
                hasPendingLevelChoices,
                progressions,
                command.skillProficiencies() != null ? command.skillProficiencies() : Collections.emptyList(),
                initialExperiencePoints,
                null,
                Collections.emptyList(),
                initialCharacterFeats,
                CurrencyPouch.empty(),
                command.avatarUrl(),
                now,
                now
        );

        PlayerCharacter saved = playerCharacterRepository.save(character);

        return CharacterSheetHydrator.toSheetResult(saved, archetype, lineage, background, Collections.emptyMap(), featMap);
    }

    private AbilityScores applyAbilityMap(AbilityScores scores, Map<String, Integer> bonuses) {
        int str = scores.getStrength();
        int dex = scores.getDexterity();
        int con = scores.getConstitution();
        int intel = scores.getIntelligence();
        int wis = scores.getWisdom();
        int cha = scores.getCharisma();

        for (Map.Entry<String, Integer> entry : bonuses.entrySet()) {
            String key = Srd2014RulesEngine.normalizeAbility(entry.getKey());
            int delta = entry.getValue() != null ? entry.getValue() : 0;
            switch (key) {
                case "STRENGTH" -> str = Math.min(20, str + delta);
                case "DEXTERITY" -> dex = Math.min(20, dex + delta);
                case "CONSTITUTION" -> con = Math.min(20, con + delta);
                case "INTELLIGENCE" -> intel = Math.min(20, intel + delta);
                case "WISDOM" -> wis = Math.min(20, wis + delta);
                case "CHARISMA" -> cha = Math.min(20, cha + delta);
                default -> {
                }
            }
        }
        return new AbilityScores(str, dex, con, intel, wis, cha);
    }

    private void validateFeatPrerequisites(
            Feat feat,
            AbilityScores scores,
            int level,
            List<UUID> archetypeIds,
            UUID lineageId,
            List<Feat> currentFeats
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
                case "MIN_LEVEL", "LEVEL" -> {
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
                                .anyMatch(f -> f.getId() != null && f.getId().toString().equalsIgnoreCase(rule.targetKey()));
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
