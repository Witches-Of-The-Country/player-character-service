package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AbilityScoresTest {

    @Test
    @DisplayName("Should create AbilityScores when all values are between 1 and 30")
    void shouldCreateAbilityScoresWhenValuesAreValid() {
        AbilityScores scores = new AbilityScores(15, 14, 13, 12, 10, 8);

        assertEquals(15, scores.getStrength());
        assertEquals(14, scores.getDexterity());
        assertEquals(13, scores.getConstitution());
        assertEquals(12, scores.getIntelligence());
        assertEquals(10, scores.getWisdom());
        assertEquals(8, scores.getCharisma());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 31, 100})
    @DisplayName("Should throw exception when Strength is outside 1..30")
    void shouldThrowExceptionWhenStrengthIsInvalid(int invalidScore) {
        assertThrows(CharacterValidationException.class, () ->
                new AbilityScores(invalidScore, 10, 10, 10, 10, 10)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, 31, 50})
    @DisplayName("Should throw exception when any ability score is outside 1..30")
    void shouldThrowExceptionWhenAnyAbilityScoreIsInvalid(int invalidScore) {
        assertThrows(CharacterValidationException.class, () ->
                new AbilityScores(10, invalidScore, 10, 10, 10, 10)
        );
        assertThrows(CharacterValidationException.class, () ->
                new AbilityScores(10, 10, invalidScore, 10, 10, 10)
        );
        assertThrows(CharacterValidationException.class, () ->
                new AbilityScores(10, 10, 10, invalidScore, 10, 10)
        );
        assertThrows(CharacterValidationException.class, () ->
                new AbilityScores(10, 10, 10, 10, invalidScore, 10)
        );
        assertThrows(CharacterValidationException.class, () ->
                new AbilityScores(10, 10, 10, 10, 10, invalidScore)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "1, -5",
            "8, -1",
            "9, -1",
            "10, 0",
            "11, 0",
            "12, 1",
            "13, 1",
            "14, 2",
            "15, 2",
            "18, 4",
            "20, 5",
            "30, 10"
    })
    @DisplayName("Should calculate modifier correctly for D&D 5e standard formula")
    void shouldCalculateModifiersCorrectly(int score, int expectedModifier) {
        assertEquals(expectedModifier, AbilityScores.calculateModifier(score));
    }

    @Test
    @DisplayName("Should return correct modifiers from instance methods")
    void shouldReturnCorrectModifiersFromInstance() {
        AbilityScores scores = new AbilityScores(16, 14, 12, 10, 8, 20);

        assertEquals(3, scores.getStrengthModifier());
        assertEquals(2, scores.getDexterityModifier());
        assertEquals(1, scores.getConstitutionModifier());
        assertEquals(0, scores.getIntelligenceModifier());
        assertEquals(-1, scores.getWisdomModifier());
        assertEquals(5, scores.getCharismaModifier());
    }
}
