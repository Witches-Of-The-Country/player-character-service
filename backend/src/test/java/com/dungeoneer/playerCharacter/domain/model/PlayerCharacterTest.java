package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCharacterTest {

    private AbilityScores createValidAbilityScores() {
        return new AbilityScores(15, 14, 13, 12, 10, 8);
    }

    @Test
    @DisplayName("Should create PlayerCharacter successfully with valid attributes")
    void shouldCreatePlayerCharacterSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AbilityScores abilityScores = createValidAbilityScores();
        Instant now = Instant.now();

        PlayerCharacter character = new PlayerCharacter(
                id,
                "Thorin Stonebeard",
                3,
                24,
                24,
                0,
                16,
                2,
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                now,
                now
        );

        assertEquals(id, character.getId());
        assertEquals("Thorin Stonebeard", character.getName());
        assertEquals(3, character.getLevel());
        assertEquals(24, character.getHitPointsCurrent());
        assertEquals(24, character.getHitPointsMax());
        assertEquals(0, character.getHitPointsTemp());
        assertEquals(16, character.getArmorClass());
        assertEquals(2, character.getInitiativeBonus());
        assertEquals(30, character.getSpeed());
        assertEquals(2, character.getProficiencyBonus());
        assertEquals(Ruleset.SRD_2024, character.getRuleset());
        assertEquals(AbilityScoreMethod.STANDARD_ARRAY, character.getAbilityScoreMethod());
        assertEquals(lineageId, character.getLineageId());
        assertEquals(backgroundId, character.getBackgroundId());
        assertEquals(userId, character.getUserId());
        assertEquals(abilityScores, character.getAbilityScores());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "4, 2",
            "5, 3",
            "8, 3",
            "9, 4",
            "12, 4",
            "13, 5",
            "16, 5",
            "17, 6",
            "20, 6"
    })
    @DisplayName("Should calculate correct proficiency bonus for each level (1-20)")
    void shouldCalculateCorrectProficiencyBonus(int level, int expectedProficiencyBonus) {
        assertEquals(expectedProficiencyBonus, PlayerCharacter.calculateProficiencyBonus(level));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 21, 25, 100})
    @DisplayName("Should throw exception when level is outside 1..20")
    void shouldThrowExceptionWhenLevelIsInvalid(int invalidLevel) {
        assertThrows(CharacterValidationException.class, () ->
                PlayerCharacter.calculateProficiencyBonus(invalidLevel)
        );

        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(
                        UUID.randomUUID(),
                        "Invalid Hero",
                        invalidLevel,
                        10,
                        10,
                        0,
                        10,
                        0,
                        30,
                        2,
                        Ruleset.SRD_2024,
                        AbilityScoreMethod.STANDARD_ARRAY,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        createValidAbilityScores(),
                        Instant.now(),
                        Instant.now()
                )
        );
    }

    @Test
    @DisplayName("Should throw exception when name is null or blank")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(
                        UUID.randomUUID(),
                        null,
                        1,
                        10,
                        10,
                        0,
                        10,
                        0,
                        30,
                        2,
                        Ruleset.SRD_2024,
                        AbilityScoreMethod.STANDARD_ARRAY,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        createValidAbilityScores(),
                        Instant.now(),
                        Instant.now()
                )
        );

        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(
                        UUID.randomUUID(),
                        "   ",
                        1,
                        10,
                        10,
                        0,
                        10,
                        0,
                        30,
                        2,
                        Ruleset.SRD_2024,
                        AbilityScoreMethod.STANDARD_ARRAY,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        createValidAbilityScores(),
                        Instant.now(),
                        Instant.now()
                )
        );
    }

    @Test
    @DisplayName("Should throw exception when mandatory fields are null")
    void shouldThrowExceptionWhenMandatoryFieldsAreNull() {
        UUID id = UUID.randomUUID();
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = createValidAbilityScores();
        Instant now = Instant.now();

        // Null Ruleset
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        null, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, null, scores, now, now)
        );

        // Null AbilityScoreMethod
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, null, lineageId, backgroundId, null, scores, now, now)
        );

        // Null LineageId
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, null, backgroundId, null, scores, now, now)
        );

        // Null BackgroundId
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, null, null, scores, now, now)
        );

        // Null AbilityScores
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, null, null, now, now)
        );
    }

    @Test
    @DisplayName("Should create character with builder/factory defaulting calculation methods correctly")
    void shouldCreateNewCharacterWithAutoCalculatedFields() {
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = new AbilityScores(14, 16, 12, 10, 8, 13); // DEX mod = +3

        PlayerCharacter character = PlayerCharacter.createNew(
                "Aramil",
                5, // level 5 -> prof bonus +3
                35, // max HP
                Ruleset.SRD_2024,
                AbilityScoreMethod.POINT_BUY,
                lineageId,
                backgroundId,
                null,
                scores
        );

        assertNotNull(character.getId());
        assertEquals("Aramil", character.getName());
        assertEquals(5, character.getLevel());
        assertEquals(3, character.getProficiencyBonus());
        assertEquals(35, character.getHitPointsMax());
        assertEquals(35, character.getHitPointsCurrent());
        assertEquals(0, character.getHitPointsTemp());
        assertEquals(13, character.getArmorClass()); // 10 + 3 DEX
        assertEquals(3, character.getInitiativeBonus()); // +3 DEX
        assertEquals(30, character.getSpeed()); // default 30
        assertNotNull(character.getCreatedAt());
        assertNotNull(character.getUpdatedAt());
    }
}
