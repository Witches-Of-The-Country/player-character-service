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
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.HpRoll;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
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
import java.util.List;
import java.util.Map;
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

        List<ClassProgression> progressions;
        if (command.classProgressions() != null && !command.classProgressions().isEmpty()) {
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
                    command.subclassId(),
                    command.level()
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
            if (hpMethod == HpCalculationMethod.ROLLED && command.level() > 1) {
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
            hitPointsMax = totalHp;
        } else {
            // Mono-class logic
            if (command.level() > 1 && hpMethod == HpCalculationMethod.ROLLED) {
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
                if (hpRoll.getTargetLevel() != command.level()) {
                    throw new InvalidRollTokenException("HP roll target level (" + hpRoll.getTargetLevel() + ") does not match character level (" + command.level() + ").");
                }

                usedHpRolls.add(hpRoll);
                hitPointsMax = rulesEngine.calculateMaxHitPoints(
                        command.level(),
                        archetype.getHitDice(),
                        finalScores.getConstitutionModifier(),
                        hpMethod,
                        hpRoll.getRolledHpPerLevel()
                );
            } else {
                hitPointsMax = rulesEngine.calculateMaxHitPoints(
                        command.level(),
                        archetype.getHitDice(),
                        finalScores.getConstitutionModifier(),
                        hpMethod,
                        null
                );
            }
        }

        if (abilityRoll != null) {
            abilityRollRepository.markAsUsed(abilityRoll.getId());
        }
        for (HpRoll r : usedHpRolls) {
            hpRollRepository.markAsUsed(r.getId());
        }

        int armorClass = rulesEngine.calculateArmorClass(finalScores.getDexterityModifier());
        int initiativeBonus = rulesEngine.calculateInitiativeBonus(finalScores.getDexterityModifier());
        int speed = rulesEngine.calculateSpeed(lineage);
        int proficiencyBonus = rulesEngine.calculateProficiencyBonus(command.level());
        boolean hasPendingLevelChoices = command.level() > 1;

        Instant now = Instant.now();
        PlayerCharacter character = new PlayerCharacter(
                UUID.randomUUID(),
                command.name(),
                command.level(),
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
                now,
                now
        );

        PlayerCharacter saved = playerCharacterRepository.save(character);

        return CharacterSheetHydrator.toSheetResult(saved, archetype, lineage, background);
    }
}
