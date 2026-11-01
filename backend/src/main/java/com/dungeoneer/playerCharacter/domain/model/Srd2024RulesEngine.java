package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Srd2024RulesEngine implements RulesEngine {

    private final Srd2014RulesEngine baseEngine = new Srd2014RulesEngine();

    @Override
    public Ruleset getRuleset() {
        return Ruleset.SRD_2024;
    }

    @Override
    public AbilityScores applyAbilityScoreIncreases(
            AbilityScores baseScores,
            Lineage lineage,
            Background background,
            Map<String, Integer> chosenBonuses
    ) {
        if (baseScores == null) {
            throw new CharacterValidationException("Base ability scores cannot be null.");
        }
        if (background == null) {
            throw new CharacterValidationException("Background cannot be null for SRD 2024 ruleset.");
        }

        validateChosenBonuses(background, chosenBonuses);

        Map<String, Integer> normalizedBonuses = new HashMap<>();
        if (chosenBonuses != null) {
            chosenBonuses.forEach((key, val) -> {
                normalizedBonuses.put(Srd2014RulesEngine.normalizeAbility(key), val);
            });
        }

        int str = Math.min(20, baseScores.getStrength() + normalizedBonuses.getOrDefault("STRENGTH", 0));
        int dex = Math.min(20, baseScores.getDexterity() + normalizedBonuses.getOrDefault("DEXTERITY", 0));
        int con = Math.min(20, baseScores.getConstitution() + normalizedBonuses.getOrDefault("CONSTITUTION", 0));
        int intel = Math.min(20, baseScores.getIntelligence() + normalizedBonuses.getOrDefault("INTELLIGENCE", 0));
        int wis = Math.min(20, baseScores.getWisdom() + normalizedBonuses.getOrDefault("WISDOM", 0));
        int cha = Math.min(20, baseScores.getCharisma() + normalizedBonuses.getOrDefault("CHARISMA", 0));

        return new AbilityScores(str, dex, con, intel, wis, cha);
    }

    private void validateChosenBonuses(Background background, Map<String, Integer> chosenBonuses) {
        if (chosenBonuses == null || chosenBonuses.isEmpty()) {
            throw new CharacterValidationException("SRD 2024 requires selecting ability score bonuses from the background.");
        }

        List<String> allowed = background.getAllowedAbilityBonuses();
        Set<String> normalizedAllowed = new HashSet<>();
        if (allowed != null) {
            for (String a : allowed) {
                normalizedAllowed.add(Srd2014RulesEngine.normalizeAbility(a));
            }
        }

        List<Integer> bonusValues = new ArrayList<>();
        Set<String> selectedAbilities = new HashSet<>();

        for (Map.Entry<String, Integer> entry : chosenBonuses.entrySet()) {
            String ability = Srd2014RulesEngine.normalizeAbility(entry.getKey());
            if (!normalizedAllowed.contains(ability)) {
                throw new CharacterValidationException(
                        "Ability '" + entry.getKey() + "' is not permitted for background '" + background.getName() + "'. Allowed: " + allowed
                );
            }
            if (!selectedAbilities.add(ability)) {
                throw new CharacterValidationException("Duplicate ability score bonus specified for: " + entry.getKey());
            }
            bonusValues.add(entry.getValue());
        }

        Collections.sort(bonusValues);

        boolean isTwoPlusOne = bonusValues.equals(List.of(1, 2));
        boolean isThreeOnes = bonusValues.equals(List.of(1, 1, 1));

        if (!isTwoPlusOne && !isThreeOnes) {
            throw new CharacterValidationException(
                    "Invalid bonus distribution. SRD 2024 allows either (+2/+1) to two distinct abilities or (+1/+1/+1) to three distinct abilities. Provided: " + bonusValues
            );
        }
    }

    @Override
    public int calculateMaxHitPoints(
            int level,
            HitDiceEnum hitDice,
            int constitutionModifier,
            HpCalculationMethod hpCalculationMethod,
            List<Integer> rolledHpPerLevel
    ) {
        return baseEngine.calculateMaxHitPoints(
                level,
                hitDice,
                constitutionModifier,
                hpCalculationMethod,
                rolledHpPerLevel
        );
    }

    @Override
    public int calculateArmorClass(int dexterityModifier) {
        return 10 + dexterityModifier;
    }

    @Override
    public int calculateArmorClass(
            int dexterityModifier,
            ArmorMechanics armor,
            ArmorMechanics shield,
            int extraBonus,
            boolean hasBarbarianUnarmoredDefense,
            int constitutionModifier,
            boolean hasMonkUnarmoredDefense,
            int wisdomModifier
    ) {
        return baseEngine.calculateArmorClass(
                dexterityModifier,
                armor,
                shield,
                extraBonus,
                hasBarbarianUnarmoredDefense,
                constitutionModifier,
                hasMonkUnarmoredDefense,
                wisdomModifier
        );
    }

    @Override
    public int calculateInitiativeBonus(int dexterityModifier) {
        return dexterityModifier;
    }

    @Override
    public int calculateProficiencyBonus(int level) {
        return (level - 1) / 4 + 2;
    }

    @Override
    public int calculateSpeed(Lineage lineage) {
        return (lineage != null && lineage.getSpeed() > 0) ? lineage.getSpeed() : 30;
    }

    @Override
    public int getXpForLevel(int level) {
        return baseEngine.getXpForLevel(level);
    }

    @Override
    public int getLevelForXp(int experiencePoints) {
        return baseEngine.getLevelForXp(experiencePoints);
    }
}
