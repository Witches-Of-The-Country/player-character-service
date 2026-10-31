package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.port.in.RollAbilityScoresUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollHitPointsUseCase;
import com.dungeoneer.playerCharacter.application.port.out.AbilityRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.HpRoll;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class RollService implements RollAbilityScoresUseCase, RollHitPointsUseCase {

    private final AbilityRollRepositoryPort abilityRollRepository;
    private final HpRollRepositoryPort hpRollRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final Random random = new Random();

    public RollService(
            AbilityRollRepositoryPort abilityRollRepository,
            HpRollRepositoryPort hpRollRepository,
            ReferenceDataRepository referenceDataRepository
    ) {
        this.abilityRollRepository = abilityRollRepository;
        this.hpRollRepository = hpRollRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    @Override
    public RollAbilityScoresResult rollAbilityScores(UUID userId) {
        if (userId == null) {
            throw new CharacterValidationException("UserId is required.");
        }

        Optional<AbilityRoll> existing = abilityRollRepository.findUnusedByUserId(userId);
        if (existing.isPresent()) {
            AbilityRoll roll = existing.get();
            return new RollAbilityScoresResult(
                    roll.getId(),
                    roll.getUserId(),
                    roll.getRolledScores(),
                    roll.getDiceBreakdownJson(),
                    false,
                    roll.getCreatedAt()
            );
        }

        List<Integer> finalScores = new ArrayList<>();
        StringBuilder breakdownJson = new StringBuilder("[");

        for (int i = 0; i < 6; i++) {
            List<Integer> dice = new ArrayList<>();
            for (int d = 0; d < 4; d++) {
                dice.add(random.nextInt(6) + 1);
            }
            Collections.sort(dice);
            // 4d6kh3: drop the lowest (dice.get(0)), sum top 3 (dice.get(1), dice.get(2), dice.get(3))
            int sum = dice.get(1) + dice.get(2) + dice.get(3);
            finalScores.add(sum);

            breakdownJson.append("[")
                    .append(dice.get(0)).append(",")
                    .append(dice.get(1)).append(",")
                    .append(dice.get(2)).append(",")
                    .append(dice.get(3))
                    .append("]");
            if (i < 5) {
                breakdownJson.append(",");
            }
        }
        breakdownJson.append("]");

        AbilityRoll newRoll = AbilityRoll.createNew(userId, finalScores, breakdownJson.toString());
        AbilityRoll saved = abilityRollRepository.save(newRoll);

        return new RollAbilityScoresResult(
                saved.getId(),
                saved.getUserId(),
                saved.getRolledScores(),
                saved.getDiceBreakdownJson(),
                true,
                saved.getCreatedAt()
        );
    }

    @Override
    public RollHitPointsResult rollHitPoints(RollHitPointsCommand command) {
        if (command.userId() == null) {
            throw new CharacterValidationException("UserId is required.");
        }
        if (command.archetypeId() == null) {
            throw new CharacterValidationException("ArchetypeId is required.");
        }
        if (command.targetLevel() < 2 || command.targetLevel() > 20) {
            throw new CharacterValidationException("Target level must be between 2 and 20. Provided: " + command.targetLevel());
        }

        Optional<HpRoll> existing = hpRollRepository.findUnusedByUserIdAndArchetypeIdAndTargetLevel(
                command.userId(),
                command.archetypeId(),
                command.targetLevel()
        );
        if (existing.isPresent()) {
            HpRoll roll = existing.get();
            return new RollHitPointsResult(
                    roll.getId(),
                    roll.getUserId(),
                    roll.getArchetypeId(),
                    roll.getTargetLevel(),
                    roll.getRolledHpPerLevel(),
                    false,
                    roll.getCreatedAt()
            );
        }

        Archetype archetype = referenceDataRepository.findArchetypeById(command.archetypeId())
                .orElseThrow(() -> new CharacterValidationException("Archetype not found: " + command.archetypeId()));

        int dieSides = archetype.getHitDice().getDieValue();
        int rollsNeeded = command.targetLevel() - 1;
        List<Integer> rolledHp = new ArrayList<>();

        for (int i = 0; i < rollsNeeded; i++) {
            rolledHp.add(random.nextInt(dieSides) + 1);
        }

        HpRoll newRoll = HpRoll.createNew(command.userId(), command.archetypeId(), command.targetLevel(), rolledHp);
        HpRoll saved = hpRollRepository.save(newRoll);

        return new RollHitPointsResult(
                saved.getId(),
                saved.getUserId(),
                saved.getArchetypeId(),
                saved.getTargetLevel(),
                saved.getRolledHpPerLevel(),
                true,
                saved.getCreatedAt()
        );
    }
}
