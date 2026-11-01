package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Srd2014RulesEngine implements RulesEngine {

    public static final int[] XP_THRESHOLDS = {
            0,       // Level 1
            300,     // Level 2
            900,     // Level 3
            2700,    // Level 4
            6500,    // Level 5
            14000,   // Level 6
            23000,   // Level 7
            34000,   // Level 8
            48000,   // Level 9
            64000,   // Level 10
            85000,   // Level 11
            100000,  // Level 12
            120000,  // Level 13
            140000,  // Level 14
            165000,  // Level 15
            195000,  // Level 16
            225000,  // Level 17
            265000,  // Level 18
            305000,  // Level 19
            355000   // Level 20
    };

    @Override
    public Ruleset getRuleset() {
        return Ruleset.SRD_2014;
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

        Map<String, Integer> totalBonuses = new HashMap<>();

        if (lineage != null && lineage.getAbilityBonuses() != null) {
            lineage.getAbilityBonuses().forEach((key, val) -> {
                if (key != null && val != null) {
                    totalBonuses.merge(normalizeAbility(key), val, Integer::sum);
                }
            });
        }

        if (chosenBonuses != null) {
            chosenBonuses.forEach((key, val) -> {
                if (key != null && val != null) {
                    totalBonuses.merge(normalizeAbility(key), val, Integer::sum);
                }
            });
        }

        int str = Math.min(20, baseScores.getStrength() + totalBonuses.getOrDefault("STRENGTH", 0));
        int dex = Math.min(20, baseScores.getDexterity() + totalBonuses.getOrDefault("DEXTERITY", 0));
        int con = Math.min(20, baseScores.getConstitution() + totalBonuses.getOrDefault("CONSTITUTION", 0));
        int intel = Math.min(20, baseScores.getIntelligence() + totalBonuses.getOrDefault("INTELLIGENCE", 0));
        int wis = Math.min(20, baseScores.getWisdom() + totalBonuses.getOrDefault("WISDOM", 0));
        int cha = Math.min(20, baseScores.getCharisma() + totalBonuses.getOrDefault("CHARISMA", 0));

        return new AbilityScores(str, dex, con, intel, wis, cha);
    }

    @Override
    public int calculateMaxHitPoints(
            int level,
            HitDiceEnum hitDice,
            int constitutionModifier,
            HpCalculationMethod hpCalculationMethod,
            List<Integer> rolledHpPerLevel
    ) {
        if (level < 1 || level > 20) {
            throw new CharacterValidationException("Level must be between 1 and 20. Provided: " + level);
        }
        if (hitDice == null) {
            throw new CharacterValidationException("Hit dice cannot be null.");
        }

        HpCalculationMethod calcMethod = (hpCalculationMethod != null) ? hpCalculationMethod : HpCalculationMethod.MAX;

        // Level 1 HP
        int totalHp = Math.max(1, hitDice.getDieValue() + constitutionModifier);

        if (level == 1) {
            return totalHp;
        }

        if (calcMethod == HpCalculationMethod.ROLLED) {
            int neededRolls = level - 1;
            if (rolledHpPerLevel == null || rolledHpPerLevel.size() != neededRolls) {
                throw new CharacterValidationException(
                        "Rolled HP requires exactly " + neededRolls + " rolls for level " + level + ". Provided: " +
                                (rolledHpPerLevel == null ? "null" : rolledHpPerLevel.size())
                );
            }
            for (int rolled : rolledHpPerLevel) {
                if (rolled < 1 || rolled > hitDice.getDieValue()) {
                    throw new CharacterValidationException(
                            "Rolled HP value " + rolled + " is outside valid range for " + hitDice + " (1-" + hitDice.getDieValue() + ")"
                    );
                }
                totalHp += Math.max(1, rolled + constitutionModifier);
            }
        } else if (calcMethod == HpCalculationMethod.AVERAGE) {
            int avgPerLevel = hitDice.getAverageHp();
            for (int lvl = 2; lvl <= level; lvl++) {
                totalHp += Math.max(1, avgPerLevel + constitutionModifier);
            }
        } else {
            // MAX
            int maxPerLevel = hitDice.getDieValue();
            for (int lvl = 2; lvl <= level; lvl++) {
                totalHp += Math.max(1, maxPerLevel + constitutionModifier);
            }
        }

        return totalHp;
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
        int baseAc;
        if (armor == null) {
            // Unarmored
            if (hasBarbarianUnarmoredDefense) {
                baseAc = 10 + dexterityModifier + constitutionModifier;
            } else if (hasMonkUnarmoredDefense) {
                baseAc = 10 + dexterityModifier + wisdomModifier;
            } else {
                baseAc = 10 + dexterityModifier;
            }
        } else {
            baseAc = switch (armor.armorCategory()) {
                case HEAVY -> armor.baseAc() + armor.magicBonus();
                case MEDIUM -> armor.baseAc() + armor.magicBonus() + Math.min(dexterityModifier, 2);
                case LIGHT -> armor.baseAc() + armor.magicBonus() + dexterityModifier;
                case ROBE -> {
                    int unarmoredBase = 10 + dexterityModifier;
                    if (hasBarbarianUnarmoredDefense) {
                        unarmoredBase = 10 + dexterityModifier + constitutionModifier;
                    } else if (hasMonkUnarmoredDefense) {
                        unarmoredBase = 10 + dexterityModifier + wisdomModifier;
                    }
                    int robeBase = Math.max(armor.baseAc() + dexterityModifier, unarmoredBase);
                    yield robeBase + armor.magicBonus();
                }
                case SHIELD -> 10 + dexterityModifier;
            };
        }

        int shieldBonus = 0;
        if (shield != null && shield.armorCategory() == ArmorCategory.SHIELD) {
            shieldBonus = shield.baseAc() + shield.magicBonus();
        }

        return baseAc + shieldBonus + extraBonus;
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
        if (level < 1 || level > 20) {
            throw new CharacterValidationException("Level must be between 1 and 20. Provided: " + level);
        }
        return XP_THRESHOLDS[level - 1];
    }

    @Override
    public int getLevelForXp(int experiencePoints) {
        if (experiencePoints < 0) {
            throw new CharacterValidationException("Experience points cannot be negative.");
        }
        for (int i = XP_THRESHOLDS.length - 1; i >= 0; i--) {
            if (experiencePoints >= XP_THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    public static String normalizeAbility(String name) {
        if (name == null) return "";
        String s = name.trim().toUpperCase();
        return switch (s) {
            case "STR", "STRENGTH" -> "STRENGTH";
            case "DEX", "DEXTERITY" -> "DEXTERITY";
            case "CON", "CONSTITUTION" -> "CONSTITUTION";
            case "INT", "INTELLIGENCE" -> "INTELLIGENCE";
            case "WIS", "WISDOM" -> "WISDOM";
            case "CHA", "CHARISMA" -> "CHARISMA";
            default -> s;
        };
    }
}
