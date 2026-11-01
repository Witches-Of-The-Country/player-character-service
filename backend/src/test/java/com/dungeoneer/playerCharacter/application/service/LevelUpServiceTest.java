package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.AddExperienceCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.LevelUpCommand;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
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

    private LevelUpService levelUpService;

    private UUID characterId;
    private UUID userId;
    private UUID archetypeId;
    private PlayerCharacter character;
    private Archetype archetype;

    @BeforeEach
    void setUp() {
        levelUpService = new LevelUpService(characterRepository, referenceDataRepository, sheetHydrator);

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
                HpCalculationMethod.MAX,
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
    @DisplayName("Should level up same class from level 1 to level 2 with HP increase")
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
                6, // d10 avg = 6
                false,
                null,
                null
        );

        CharacterSheetResult result = levelUpService.levelUp(characterId, userId, command);

        assertThat(result).isNotNull();
        // Base 12 HP + 6 (roll) + 2 (CON 14 mod) = 20 HP
        verify(characterRepository).save(argThat(c -> c.getLevel() == 2 && c.getHitPointsMax() == 20));
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
}