package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.AddExperienceCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.LevelUpCommand;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LevelUpServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort characterRepository;

    @Mock
    private ReferenceDataRepository referenceDataRepository;

    @Mock
    private CharacterSheetHydrator sheetHydrator;

    @Mock
    private HpRollRepositoryPort hpRollRepository;

    private LevelUpService levelUpService;

    private UUID characterId;
    private UUID userId;
    private UUID archetypeId;
    private PlayerCharacter character;
    private Archetype archetype;

    @BeforeEach
    void setUp() {
        levelUpService = new LevelUpService(characterRepository, referenceDataRepository, sheetHydrator, hpRollRepository);

        characterId = UUID.randomUUID();
        userId = UUID.randomUUID();
        archetypeId = UUID.randomUUID();

        archetype = new Archetype(
                archetypeId,
                "Fighter",
                HitDiceEnum.D10,
                "Strength",
                List.of("Strength", "Constitution"),
                List.of("All armor"),
                List.of("Martial weapons"),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null,
                3
        );

        ClassProgression progression = new ClassProgression(UUID.randomUUID(), characterId, archetypeId, null, 1);
        AbilityScores scores = new AbilityScores(16, 12, 14, 10, 12, 8);

        character = new PlayerCharacter(
                characterId,
                "Conan",
                1,
                12,
                12,
                0,
                16,
                1,
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                userId,
                scores,
                "",
                HpCalculationMethod.AVERAGE,
                false,
                List.of(progression),
                List.of(),
                0,
                null,
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("Should add experience to character successfully")
    void shouldAddExperience() {
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.save(any(PlayerCharacter.class))).thenAnswer(i -> i.getArgument(0));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        CharacterSheetResult result = levelUpService.addExperience(characterId, userId, new AddExperienceCommand(300));

        assertThat(result).isNotNull();
        verify(characterRepository).save(argThat(c -> c.getExperiencePoints() == 300));
    }

    @Test
    @DisplayName("Should level up same class from level 1 to level 2 with server-calculated AVERAGE HP increase")
    void shouldLevelUpClass() {
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(characterRepository.save(any(PlayerCharacter.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceDataRepository.findProgressionFeats(archetypeId, 2)).thenReturn(List.of());
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        LevelUpCommand command = new LevelUpCommand(
                archetypeId,
                null,
                HpCalculationMethod.AVERAGE,
                null,
                999, // Client-sent hpIncrease is ignored in favor of server-locked AVERAGE (6)
                false,
                null,
                null
        );

        CharacterSheetResult result = levelUpService.levelUp(characterId, userId, command);

        assertThat(result).isNotNull();
        // Base 12 HP + 6 (d10 avg) + 2 (CON 14 mod) = 20 HP
        verify(characterRepository).save(argThat(c -> c.getLevel() == 2 && c.getHitPointsMax() == 20));
    }

    @Test
    @DisplayName("Should validate and consume HpRoll token when character hpCalculationMethod is ROLLED")
    void shouldValidateAndConsumeHpRollTokenWhenRolled() {
        ClassProgression progression = new ClassProgression(UUID.randomUUID(), characterId, archetypeId, null, 1);
        PlayerCharacter rolledChar = new PlayerCharacter(
                characterId, "Conan", 1, 12, 12, 0, 16, 1, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(), UUID.randomUUID(), userId,
                new AbilityScores(16, 12, 14, 10, 12, 8), "",
                HpCalculationMethod.ROLLED, true, List.of(progression), List.of(),
                6500, null, List.of(), List.of(), Instant.now(), Instant.now()
        );

        UUID hpRollId = UUID.randomUUID();
        HpRoll hpRoll = new HpRoll(hpRollId, userId, archetypeId, 2, List.of(9), false, Instant.now());

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(rolledChar));
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(hpRollRepository.findById(hpRollId)).thenReturn(Optional.of(hpRoll));
        when(characterRepository.save(any(PlayerCharacter.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceDataRepository.findProgressionFeats(archetypeId, 2)).thenReturn(List.of());
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        LevelUpCommand command = new LevelUpCommand(
                archetypeId, null, HpCalculationMethod.ROLLED, hpRollId, 0, false, null, null
        );

        levelUpService.levelUp(characterId, userId, command);

        verify(hpRollRepository).markAsUsed(hpRollId);
        // 12 + 9 (rolled) + 2 (CON mod) = 23 HP; 6500 XP >= level 3 threshold (900) -> hasPendingLevelChoices remains true
        verify(characterRepository).save(argThat(c ->
                c.getLevel() == 2 && c.getHitPointsMax() == 23 && c.isHasPendingLevelChoices()
        ));
    }

    @Test
    @DisplayName("Should throw InvalidRollTokenException when ROLLED character submits level-up without hpRollId")
    void shouldRejectRolledLevelUpWithoutToken() {
        ClassProgression progression = new ClassProgression(UUID.randomUUID(), characterId, archetypeId, null, 1);
        PlayerCharacter rolledChar = new PlayerCharacter(
                characterId, "Conan", 1, 12, 12, 0, 16, 1, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(), UUID.randomUUID(), userId,
                new AbilityScores(16, 12, 14, 10, 12, 8), "",
                HpCalculationMethod.ROLLED, false, List.of(progression), List.of(),
                300, null, List.of(), List.of(), Instant.now(), Instant.now()
        );

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(rolledChar));
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));

        LevelUpCommand command = new LevelUpCommand(
                archetypeId, null, HpCalculationMethod.ROLLED, null, 10, false, null, null
        );

        assertThatThrownBy(() -> levelUpService.levelUp(characterId, userId, command))
                .isInstanceOf(InvalidRollTokenException.class);
    }

    @Test
    @DisplayName("Should apply feat abilityBonuses and retroactive hpPerLevelBonus when selectedFeatId is chosen")
    void shouldApplySelectedFeatMechanicalEffects() {
        UUID toughFeatId = UUID.randomUUID();
        MechanicalEffects toughEffects = new MechanicalEffects(
                Map.of("STR", 1),
                0, 0, 0,
                2, // +2 HP per level
                List.of(),
                null, null, null, null, RestType.NONE
        );
        Feat toughFeat = new Feat(
                toughFeatId, Ruleset.SRD_2024, "tough", "Tough", "Vigoroso", null,
                "+2 PV por nível", "SRD", null, FeatType.ORIGIN, toughEffects
        );

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findFeatById(toughFeatId)).thenReturn(Optional.of(toughFeat));
        when(characterRepository.save(any(PlayerCharacter.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceDataRepository.findProgressionFeats(archetypeId, 2)).thenReturn(List.of());
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        LevelUpCommand command = new LevelUpCommand(
                archetypeId, null, HpCalculationMethod.AVERAGE, null, 6, true, null, toughFeatId
        );

        levelUpService.levelUp(characterId, userId, command);

        // Base 12 + 6 (avg) + 2 (CON) + (2 * 2 retroactive Tough HP) = 24; STR 16 + 1 = 17
        verify(characterRepository).save(argThat(c ->
                c.getLevel() == 2
                        && c.getHitPointsMax() == 24
                        && c.getAbilityScores().getStrength() == 17
                        && c.getExperiencePoints() == 300
        ));
    }

    @Test
    @DisplayName("Should fail level up when maximum level (20) reached")
    void shouldFailLevelUpAtMaxLevel() {
        PlayerCharacter maxLevelChar = mock(PlayerCharacter.class);
        when(maxLevelChar.getLevel()).thenReturn(20);
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(maxLevelChar));

        LevelUpCommand command = new LevelUpCommand(
                archetypeId,
                null,
                HpCalculationMethod.AVERAGE,
                null,
                6,
                false,
                null,
                null
        );

        assertThatThrownBy(() -> levelUpService.levelUp(characterId, userId, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximum level (20)");
    }

    @Test
    @DisplayName("Should reject feat selection when hard prerequisites are not met")
    void shouldRejectFeatWhenPrerequisitesNotMet() {
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));

        UUID featId = UUID.randomUUID();
        Feat featWithPrereq = new Feat(
                featId,
                Ruleset.SRD_2024,
                "great-weapon-master",
                "Great Weapon Master",
                "Mestre das Armas Grandes",
                "Força 18+",
                "Dano massivo com armas pesadas",
                "SRD_2024",
                null,
                FeatType.GENERAL,
                new MechanicalEffects(
                        Map.of(),
                        0, 0, 0, 0,
                        List.of(), null, null, null, null,
                        RestType.NONE,
                        null,
                        false,
                        null,
                        List.of(new FeatPrerequisite("ABILITY_SCORE", "STRENGTH", 18, "Força 18+"))
                )
        );

        when(referenceDataRepository.findFeatById(featId)).thenReturn(Optional.of(featWithPrereq));

        LevelUpCommand command = new LevelUpCommand(
                archetypeId,
                null,
                HpCalculationMethod.AVERAGE,
                null,
                6,
                false,
                Map.of(),
                featId
        );

        assertThatThrownBy(() -> levelUpService.levelUp(characterId, userId, command))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Pré-requisito do talento não atendido: Força 18+");
    }
}
