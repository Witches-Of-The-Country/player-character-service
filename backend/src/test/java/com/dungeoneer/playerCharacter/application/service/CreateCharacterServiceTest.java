package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.out.AbilityRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCharacterServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort playerCharacterRepository;
    @Mock
    private ReferenceDataRepository referenceDataRepository;
    @Mock
    private AbilityRollRepositoryPort abilityRollRepository;
    @Mock
    private HpRollRepositoryPort hpRollRepository;

    private CreateCharacterService service;

    private UUID userId;
    private UUID archetypeId;
    private UUID lineageId;
    private UUID backgroundId;

    private Archetype archetype;
    private Lineage lineage;
    private Background background;

    @BeforeEach
    void setUp() {
        service = new CreateCharacterService(
                playerCharacterRepository,
                referenceDataRepository,
                abilityRollRepository,
                hpRollRepository
        );

        userId = UUID.randomUUID();
        archetypeId = UUID.randomUUID();
        lineageId = UUID.randomUUID();
        backgroundId = UUID.randomUUID();

        archetype = new Archetype(
                archetypeId,
                "Fighter",
                HitDiceEnum.D10,
                "Strength",
                List.of("Strength", "Constitution"),
                List.of("All Armor"),
                List.of("Martial Weapons"),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        lineage = new Lineage(
                lineageId,
                "Human",
                "Versatile",
                30,
                "MEDIUM",
                Map.of(),
                List.of(),
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        background = new Background(
                backgroundId,
                "Soldier",
                "Military training",
                List.of("Athletics", "Intimidation"),
                List.of("Vehicles"),
                List.of("Common"),
                List.of("STRENGTH", "CONSTITUTION", "DEXTERITY"),
                "Savage Attacker",
                "Extra damage",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.of(lineage));
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.of(background));
    }

    @Test
    @DisplayName("TP-01: Creates character with STANDARD_ARRAY successfully")
    void shouldCreateCharacterWithStandardArray() {
        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Sir Galahad",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                "Noble knight"
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Sir Galahad");
        // Base 15 STR + 2 bonus = 17
        assertThat(result.abilityScores().getStrength()).isEqualTo(17);
        // Base 13 CON + 1 bonus = 14 (+2 mod)
        assertThat(result.abilityScores().getConstitution()).isEqualTo(14);
        // Level 1 Fighter with CON mod (+2): 10 + 2 = 12 HP
        assertThat(result.hitPointsMax()).isEqualTo(12);
        assertThat(result.classProgressions()).hasSize(1);
        assertThat(result.classProgressions().get(0).getArchetypeId()).isEqualTo(archetypeId);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("TP-01: Creates character with ROLLED scores and consumes token atomically")
    void shouldCreateCharacterWithRolledScoresAndConsumeToken() {
        UUID abilityRollId = UUID.randomUUID();
        AbilityRoll roll = new AbilityRoll(abilityRollId, userId, List.of(16, 14, 13, 12, 10, 9), "[]", false, Instant.now());
        when(abilityRollRepository.findById(abilityRollId)).thenReturn(Optional.of(roll));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Rolled Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.ROLLED,
                16, 14, 13, 12, 10, 9,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                abilityRollId,
                HpCalculationMethod.MAX,
                null,
                "Rolled notes"
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result).isNotNull();
        verify(abilityRollRepository).markAsUsed(abilityRollId);
    }

    @Test
    @DisplayName("TE-01: Reused ability roll token throws RollConflictException")
    void shouldRejectReusedAbilityRollToken() {
        UUID abilityRollId = UUID.randomUUID();
        // already used = true
        AbilityRoll roll = new AbilityRoll(abilityRollId, userId, List.of(16, 14, 13, 12, 10, 9), "[]", true, Instant.now());
        when(abilityRollRepository.findById(abilityRollId)).thenReturn(Optional.of(roll));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Reused Roll Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.ROLLED,
                16, 14, 13, 12, 10, 9,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                abilityRollId,
                HpCalculationMethod.MAX,
                null,
                null
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(RollConflictException.class);
    }

    @Test
    @DisplayName("TE-01: Tampered score permutation with ROLLED method throws CharacterValidationException")
    void shouldRejectTamperedScoresWithRolledMethod() {
        UUID abilityRollId = UUID.randomUUID();
        AbilityRoll roll = new AbilityRoll(abilityRollId, userId, List.of(16, 14, 13, 12, 10, 9), "[]", false, Instant.now());
        when(abilityRollRepository.findById(abilityRollId)).thenReturn(Optional.of(roll));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Tampered Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.ROLLED,
                18, 14, 13, 12, 10, 9, // 18 was not rolled!
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                abilityRollId,
                HpCalculationMethod.MAX,
                null,
                null
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(CharacterValidationException.class);
    }
}
