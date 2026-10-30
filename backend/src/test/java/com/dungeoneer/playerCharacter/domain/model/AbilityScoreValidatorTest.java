package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbilityScoreValidatorTest {

    @Test
    @DisplayName("TP-01: Valid Standard Array permutation succeeds")
    void shouldAcceptValidStandardArrayPermutation() {
        AbilityScores validScores = new AbilityScores(15, 14, 13, 12, 10, 8);
        assertDoesNotThrow(() ->
                AbilityScoreValidator.validate(AbilityScoreMethod.STANDARD_ARRAY, validScores, null)
        );

        AbilityScores alternativePermutation = new AbilityScores(8, 10, 12, 13, 14, 15);
        assertDoesNotThrow(() ->
                AbilityScoreValidator.validate(AbilityScoreMethod.STANDARD_ARRAY, alternativePermutation, null)
        );
    }

    @Test
    @DisplayName("TE-01: Invalid Standard Array values throw CharacterValidationException")
    void shouldRejectInvalidStandardArrayScores() {
        AbilityScores invalidScores = new AbilityScores(16, 14, 13, 12, 10, 8);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.STANDARD_ARRAY, invalidScores, null)
        );

        AbilityScores duplicateScores = new AbilityScores(15, 15, 13, 12, 10, 8);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.STANDARD_ARRAY, duplicateScores, null)
        );
    }

    @Test
    @DisplayName("TP-01: Valid Point Buy with exactly 27 points succeeds")
    void shouldAcceptValidPointBuyScores() {
        // 15 (9 pts), 15 (9 pts), 15 (9 pts), 8 (0), 8 (0), 8 (0) = 27 pts
        AbilityScores three15s = new AbilityScores(15, 15, 15, 8, 8, 8);
        assertDoesNotThrow(() ->
                AbilityScoreValidator.validate(AbilityScoreMethod.POINT_BUY, three15s, null)
        );

        // 13 (5), 13 (5), 13 (5), 12 (4), 12 (4), 12 (4) = 27 pts
        AbilityScores balanced = new AbilityScores(13, 13, 13, 12, 12, 12);
        assertDoesNotThrow(() ->
                AbilityScoreValidator.validate(AbilityScoreMethod.POINT_BUY, balanced, null)
        );
    }

    @Test
    @DisplayName("TE-01: Point Buy exceeding or falling short of 27 points throws exception")
    void shouldRejectPointBuyNotEqual27Points() {
        // All 8s = 0 pts
        AbilityScores all8s = new AbilityScores(8, 8, 8, 8, 8, 8);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.POINT_BUY, all8s, null)
        );

        // 15, 15, 15, 15, 8, 8 = 36 pts
        AbilityScores overBudget = new AbilityScores(15, 15, 15, 15, 8, 8);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.POINT_BUY, overBudget, null)
        );
    }

    @Test
    @DisplayName("TE-01: Point Buy scores outside 8..15 range throw exception")
    void shouldRejectPointBuyScoresOutsideRange() {
        AbilityScores scoreWith7 = new AbilityScores(7, 14, 13, 12, 10, 8);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.POINT_BUY, scoreWith7, null)
        );

        AbilityScores scoreWith16 = new AbilityScores(16, 14, 13, 12, 10, 8);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.POINT_BUY, scoreWith16, null)
        );
    }

    @Test
    @DisplayName("TP-01: Valid ROLLED permutation matching rolledScores succeeds")
    void shouldAcceptMatchingRolledScores() {
        List<Integer> rolled = List.of(16, 14, 13, 11, 10, 9);
        AbilityScores assigned = new AbilityScores(16, 14, 13, 11, 10, 9);
        assertDoesNotThrow(() ->
                AbilityScoreValidator.validate(AbilityScoreMethod.ROLLED, assigned, rolled)
        );

        AbilityScores permuted = new AbilityScores(9, 10, 11, 13, 14, 16);
        assertDoesNotThrow(() ->
                AbilityScoreValidator.validate(AbilityScoreMethod.ROLLED, permuted, rolled)
        );
    }

    @Test
    @DisplayName("TE-01: Tampered or mismatched ROLLED scores throw exception")
    void shouldRejectMismatchedRolledScores() {
        List<Integer> rolled = List.of(16, 14, 13, 11, 10, 9);
        AbilityScores tampered = new AbilityScores(18, 14, 13, 11, 10, 9);
        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.ROLLED, tampered, rolled)
        );

        assertThrows(CharacterValidationException.class, () ->
                AbilityScoreValidator.validate(AbilityScoreMethod.ROLLED, tampered, List.of(16, 14, 13, 11, 10))
        );
    }
}
