package com.dungeoneer.playerCharacter.domain.model;

import java.util.List;
import java.util.Map;

public interface RulesEngine {

    Ruleset getRuleset();

    AbilityScores applyAbilityScoreIncreases(
            AbilityScores baseScores,
            Lineage lineage,
            Background background,
            Map<String, Integer> chosenBonuses
    );

    int calculateMaxHitPoints(
            int level,
            HitDiceEnum hitDice,
            int constitutionModifier,
            HpCalculationMethod hpCalculationMethod,
            List<Integer> rolledHpPerLevel
    );

    int calculateArmorClass(int dexterityModifier);

    int calculateInitiativeBonus(int dexterityModifier);

    int calculateProficiencyBonus(int level);

    int calculateSpeed(Lineage lineage);
}
