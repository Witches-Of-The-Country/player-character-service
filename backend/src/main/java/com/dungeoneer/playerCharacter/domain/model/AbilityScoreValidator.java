package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class AbilityScoreValidator {

    public static final List<Integer> STANDARD_ARRAY = List.of(15, 14, 13, 12, 10, 8);
    public static final int POINT_BUY_TOTAL_BUDGET = 27;

    private static final Map<Integer, Integer> POINT_BUY_COSTS = Map.of(
            8, 0,
            9, 1,
            10, 2,
            11, 3,
            12, 4,
            13, 5,
            14, 7,
            15, 9
    );

    private AbilityScoreValidator() {
    }

    public static void validate(
            AbilityScoreMethod method,
            AbilityScores baseScores,
            List<Integer> rolledScores
    ) {
        if (method == null) {
            throw new CharacterValidationException("AbilityScoreMethod cannot be null.");
        }
        if (baseScores == null) {
            throw new CharacterValidationException("Base ability scores cannot be null.");
        }

        List<Integer> scoresList = extractScores(baseScores);

        switch (method) {
            case STANDARD_ARRAY -> validateStandardArray(scoresList);
            case POINT_BUY -> validatePointBuy(scoresList);
            case ROLLED -> validateRolled(scoresList, rolledScores);
        }
    }

    private static void validateStandardArray(List<Integer> scores) {
        List<Integer> sortedActual = new ArrayList<>(scores);
        Collections.sort(sortedActual);

        List<Integer> sortedExpected = new ArrayList<>(STANDARD_ARRAY);
        Collections.sort(sortedExpected);

        if (!sortedActual.equals(sortedExpected)) {
            throw new CharacterValidationException(
                    "Standard Array scores must be an exact permutation of " + STANDARD_ARRAY + ". Provided: " + scores
            );
        }
    }

    private static void validatePointBuy(List<Integer> scores) {
        int totalCost = 0;
        for (int score : scores) {
            if (!POINT_BUY_COSTS.containsKey(score)) {
                throw new CharacterValidationException(
                        "Point Buy scores must be between 8 and 15. Invalid score: " + score
                );
            }
            totalCost += POINT_BUY_COSTS.get(score);
        }

        if (totalCost != POINT_BUY_TOTAL_BUDGET) {
            throw new CharacterValidationException(
                    "Point Buy total cost must be exactly " + POINT_BUY_TOTAL_BUDGET + " points. Current total cost: " + totalCost
            );
        }
    }

    private static void validateRolled(List<Integer> scores, List<Integer> rolledScores) {
        if (rolledScores == null || rolledScores.size() != 6) {
            throw new CharacterValidationException(
                    "Rolled scores must contain exactly 6 integers. Provided: " + (rolledScores == null ? "null" : rolledScores.size())
            );
        }

        List<Integer> sortedActual = new ArrayList<>(scores);
        Collections.sort(sortedActual);

        List<Integer> sortedRolled = new ArrayList<>(rolledScores);
        Collections.sort(sortedRolled);

        if (!sortedActual.equals(sortedRolled)) {
            throw new CharacterValidationException(
                    "Base ability scores do not match the assigned rolled scores. Expected permutation: " + sortedRolled + ", provided: " + sortedActual
            );
        }
    }

    public static List<Integer> extractScores(AbilityScores scores) {
        return List.of(
                scores.getStrength(),
                scores.getDexterity(),
                scores.getConstitution(),
                scores.getIntelligence(),
                scores.getWisdom(),
                scores.getCharisma()
        );
    }
}
