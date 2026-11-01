package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.AddExperienceCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.LevelUpCommand;
import com.dungeoneer.playerCharacter.application.port.in.AddExperienceUseCase;
import com.dungeoneer.playerCharacter.application.port.in.LevelUpUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LevelUpService implements AddExperienceUseCase, LevelUpUseCase {

    private final PlayerCharacterRepositoryPort characterRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final CharacterSheetHydrator sheetHydrator;

    public LevelUpService(
            PlayerCharacterRepositoryPort characterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator sheetHydrator
    ) {
        this.characterRepository = characterRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.sheetHydrator = sheetHydrator;
    }

    @Override
    public CharacterSheetResult addExperience(UUID characterId, UUID userId, AddExperienceCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);
        int newXp = character.getExperiencePoints() + command.amount();
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

        // Calculate HP increase
        int conModifier = character.getAbilityScores().getConstitutionModifier();
        int baseHpGain = command.hpIncrease();
        if (baseHpGain < 1) {
            baseHpGain = (archetype.getHitDice().getDieValue() / 2) + 1;
        }

        int totalHpGain = Math.max(1, baseHpGain + conModifier);
        int newMaxHp = character.getHitPointsMax() + totalHpGain;
        int newTotalLevel = currentTotalLevel + 1;

        // Apply Ability Score Improvement (ASI) if provided
        AbilityScores newAbilityScores = character.getAbilityScores();
        if (command.abilityScoreIncreases() != null && !command.abilityScoreIncreases().isEmpty()) {
            Map<String, Integer> currentScores = new LinkedHashMap<>();
            currentScores.put("STRENGTH", newAbilityScores.getStrength());
            currentScores.put("DEXTERITY", newAbilityScores.getDexterity());
            currentScores.put("CONSTITUTION", newAbilityScores.getConstitution());
            currentScores.put("INTELLIGENCE", newAbilityScores.getIntelligence());
            currentScores.put("WISDOM", newAbilityScores.getWisdom());
            currentScores.put("CHARISMA", newAbilityScores.getCharisma());

            for (Map.Entry<String, Integer> entry : command.abilityScoreIncreases().entrySet()) {
                String key = entry.getKey().toUpperCase();
                int currentVal = currentScores.getOrDefault(key, 10);
                currentScores.put(key, Math.min(20, currentVal + entry.getValue()));
            }
            newAbilityScores = new AbilityScores(
                    currentScores.get("STRENGTH"),
                    currentScores.get("DEXTERITY"),
                    currentScores.get("CONSTITUTION"),
                    currentScores.get("INTELLIGENCE"),
                    currentScores.get("WISDOM"),
                    currentScores.get("CHARISMA")
            );
        }

        // Create updated character domain entity
        PlayerCharacter updated = character.withLevelUp(
                newTotalLevel,
                newMaxHp,
                newAbilityScores,
                progressions,
                character.getFeats(),
                character.getExperiencePoints(),
                character.getArmorClass()
        );
        PlayerCharacter saved = characterRepository.save(updated);

        // Unlock progression feats for the archetype at newClassLevel
        List<Feat> archetypeFeats = referenceDataRepository.findProgressionFeats(archetype.getId(), newClassLevel);
        for (Feat feat : archetypeFeats) {
            if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(feat.getId()))) {
                characterRepository.saveCharacterFeat(new CharacterFeat(
                        UUID.randomUUID(),
                        characterId,
                        feat.getId(),
                        newTotalLevel,
                        feat.getMechanicalEffects().usesPerRest(),
                        feat.getMechanicalEffects().usesPerRest(),
                        feat.getSource()
                ));
            }
        }

        // Unlock subclass feats if activeSubclassId is present
        if (activeSubclassId != null) {
            List<Feat> subclassFeats = referenceDataRepository.findSubclassProgressionFeats(activeSubclassId, newClassLevel);
            for (Feat feat : subclassFeats) {
                if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(feat.getId()))) {
                    characterRepository.saveCharacterFeat(new CharacterFeat(
                            UUID.randomUUID(),
                            characterId,
                            feat.getId(),
                            newTotalLevel,
                            feat.getMechanicalEffects().usesPerRest(),
                            feat.getMechanicalEffects().usesPerRest(),
                            "SUBCLASS"
                    ));
                }
            }
        }

        // If command selected a bonus feat (e.g. at ASI level instead of stats)
        if (command.selectedFeatId() != null) {
            referenceDataRepository.findFeatById(command.selectedFeatId()).ifPresent(feat -> {
                if (!characterRepository.findFeatsByCharacterId(characterId).stream().anyMatch(cf -> cf.getFeatId().equals(feat.getId()))) {
                    characterRepository.saveCharacterFeat(new CharacterFeat(
                            UUID.randomUUID(),
                            characterId,
                            feat.getId(),
                            newTotalLevel,
                            feat.getMechanicalEffects().usesPerRest(),
                            feat.getMechanicalEffects().usesPerRest(),
                            "FEAT"
                    ));
                }
            });
        }

        // Reload updated character with fresh feats and items
        PlayerCharacter fresh = characterRepository.findByIdAndUserId(characterId, userId)
                .orElse(saved);

        return sheetHydrator.hydrate(fresh);
    }

    private PlayerCharacter getCharacter(UUID characterId, UUID userId) {
        return characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + characterId));
    }
}