package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.port.out.AbilityRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.HpRoll;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RollServiceTest {

    @Mock
    private AbilityRollRepositoryPort abilityRollRepository;
    @Mock
    private HpRollRepositoryPort hpRollRepository;
    @Mock
    private ReferenceDataRepository referenceDataRepository;

    private RollService service;
    private UUID userId;
    private UUID archetypeId;

    @BeforeEach
    void setUp() {
        service = new RollService(abilityRollRepository, hpRollRepository, referenceDataRepository);
        userId = UUID.randomUUID();
        archetypeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("TP-02: Generates new 4d6kh3 ability roll when no unused roll exists")
    void shouldGenerateNewAbilityRollWhenNoneExists() {
        when(abilityRollRepository.findUnusedByUserId(userId)).thenReturn(Optional.empty());
        when(abilityRollRepository.save(any(AbilityRoll.class))).thenAnswer(inv -> inv.getArgument(0));

        RollAbilityScoresResult result = service.rollAbilityScores(userId);

        assertThat(result.isNew()).isTrue();
        assertThat(result.rolledScores()).hasSize(6);
        for (int score : result.rolledScores()) {
            assertThat(score).isBetween(3, 18);
        }
        verify(abilityRollRepository).save(any(AbilityRoll.class));
    }

    @Test
    @DisplayName("TP-02: Idempotency: Returns existing unused ability roll without re-rolling")
    void shouldReturnExistingUnusedAbilityRoll() {
        UUID existingId = UUID.randomUUID();
        AbilityRoll existing = new AbilityRoll(existingId, userId, List.of(15, 14, 13, 12, 10, 8), "[]", false, Instant.now());
        when(abilityRollRepository.findUnusedByUserId(userId)).thenReturn(Optional.of(existing));

        RollAbilityScoresResult result = service.rollAbilityScores(userId);

        assertThat(result.isNew()).isFalse();
        assertThat(result.rollId()).isEqualTo(existingId);
        assertThat(result.rolledScores()).containsExactly(15, 14, 13, 12, 10, 8);
        verify(abilityRollRepository, never()).save(any(AbilityRoll.class));
    }

    @Test
    @DisplayName("TP-02: Generates new HP roll for target level when none exists")
    void shouldGenerateNewHpRollWhenNoneExists() {
        Archetype archetype = new Archetype(
                archetypeId, "Fighter", HitDiceEnum.D10, "STR",
                List.of(), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null
        );
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(hpRollRepository.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, archetypeId, 3))
                .thenReturn(Optional.empty());
        when(hpRollRepository.save(any(HpRoll.class))).thenAnswer(inv -> inv.getArgument(0));

        RollHitPointsResult result = service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 3));

        assertThat(result.isNew()).isTrue();
        // Target level 3 requires (3 - 1) = 2 rolls
        assertThat(result.rolledHpPerLevel()).hasSize(2);
        for (int hp : result.rolledHpPerLevel()) {
            assertThat(hp).isBetween(1, 10);
        }
        verify(hpRollRepository).save(any(HpRoll.class));
    }

    @Test
    @DisplayName("TP-02: Idempotency: Returns existing unused HP roll without re-rolling")
    void shouldReturnExistingUnusedHpRoll() {
        UUID existingId = UUID.randomUUID();
        HpRoll existing = new HpRoll(existingId, userId, archetypeId, 3, List.of(8, 7), false, Instant.now());
        when(hpRollRepository.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, archetypeId, 3))
                .thenReturn(Optional.of(existing));

        RollHitPointsResult result = service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 3));

        assertThat(result.isNew()).isFalse();
        assertThat(result.rollId()).isEqualTo(existingId);
        assertThat(result.rolledHpPerLevel()).containsExactly(8, 7);
        verify(hpRollRepository, never()).save(any(HpRoll.class));
    }

    @Test
    @DisplayName("Bug 1: Throws CharacterValidationException when archetypeId is null")
    void shouldThrowExceptionWhenArchetypeIdIsNullOnHpRoll() {
        org.junit.jupiter.api.Assertions.assertThrows(
                com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException.class,
                () -> service.rollHitPoints(new RollHitPointsCommand(userId, null, 2))
        );
    }

    @Test
    @DisplayName("Bug 1: Throws CharacterValidationException when userId is null")
    void shouldThrowExceptionWhenUserIdIsNullOnHpRoll() {
        org.junit.jupiter.api.Assertions.assertThrows(
                com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException.class,
                () -> service.rollHitPoints(new RollHitPointsCommand(null, archetypeId, 2))
        );
    }

    @Test
    @DisplayName("Bug 1: Throws CharacterValidationException when targetLevel is out of range")
    void shouldThrowExceptionWhenTargetLevelIsOutOfRangeOnHpRoll() {
        org.junit.jupiter.api.Assertions.assertThrows(
                com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException.class,
                () -> service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 1))
        );
        org.junit.jupiter.api.Assertions.assertThrows(
                com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException.class,
                () -> service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 21))
        );
    }

    @Test
    @DisplayName("Bug 1: Throws CharacterValidationException when archetype is not found")
    void shouldThrowExceptionWhenArchetypeNotFoundOnHpRoll() {
        when(hpRollRepository.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, archetypeId, 2))
                .thenReturn(Optional.empty());
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException.class,
                () -> service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 2))
        );
    }

    @Test
    @DisplayName("Multiclass HP: Rolls all N hit dice for secondary class (level 2 -> 2 dice, level 1 -> 1 die)")
    void shouldRollAllHitDiceForSecondaryClass() {
        Archetype rogue = new Archetype(
                archetypeId, "Rogue", HitDiceEnum.D8, "DEX",
                List.of(), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null
        );
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(rogue));
        when(hpRollRepository.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, archetypeId, 2))
                .thenReturn(Optional.empty());
        when(hpRollRepository.save(any(HpRoll.class))).thenAnswer(inv -> inv.getArgument(0));

        RollHitPointsResult result = service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 2, true));

        assertThat(result.isNew()).isTrue();
        // Secondary class at level 2 rolls exactly 2 dice (not 2 - 1 = 1)
        assertThat(result.rolledHpPerLevel()).hasSize(2);
        for (int hp : result.rolledHpPerLevel()) {
            assertThat(hp).isBetween(1, 8);
        }
    }

    @Test
    @DisplayName("Multiclass HP: Allows target level 1 for secondary class and rolls 1 hit die")
    void shouldRollHitDiceForSecondaryClassLevel1() {
        Archetype warlock = new Archetype(
                archetypeId, "Warlock", HitDiceEnum.D8, "CHA",
                List.of(), List.of(), List.of(), SpellcastingType.PACT, null, Ruleset.SRD_2024, "SRD", null
        );
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(warlock));
        when(hpRollRepository.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, archetypeId, 1))
                .thenReturn(Optional.empty());
        when(hpRollRepository.save(any(HpRoll.class))).thenAnswer(inv -> inv.getArgument(0));

        RollHitPointsResult result = service.rollHitPoints(new RollHitPointsCommand(userId, archetypeId, 1, true));

        assertThat(result.isNew()).isTrue();
        assertThat(result.rolledHpPerLevel()).hasSize(1);
        assertThat(result.rolledHpPerLevel().getFirst()).isBetween(1, 8);
        verify(hpRollRepository).save(any(HpRoll.class));
    }
}
