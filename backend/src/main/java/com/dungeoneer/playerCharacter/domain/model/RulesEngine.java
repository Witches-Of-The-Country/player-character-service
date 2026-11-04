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

    int calculateArmorClass(
            int dexterityModifier,
            ArmorMechanics armor,
            ArmorMechanics shield,
            int extraBonus,
            boolean hasBarbarianUnarmoredDefense,
            int constitutionModifier,
            boolean hasMonkUnarmoredDefense,
            int wisdomModifier
    );

    int calculateInitiativeBonus(int dexterityModifier);

    int calculateProficiencyBonus(int level);

    int calculateSpeed(Lineage lineage);

    int getXpForLevel(int level);

    int getLevelForXp(int experiencePoints);

    int calculateHitDieHealing(int dieRoll, int constitutionModifier);

    int calculateHitDiceRecovery(int totalCharacterLevel);

    int calculateExhaustionRecovery(int currentExhaustion);

    static RulesEngine forRuleset(Ruleset ruleset) {
        if (ruleset == Ruleset.SRD_2024) {
            return new Srd2024RulesEngine();
        }
        return new Srd2014RulesEngine();
    }
}
