package com.dungeoneer.playerCharacter.domain.model;

public final class FeatChargeCalculator {

    private FeatChargeCalculator() {
    }

    public static Integer calculateMaxCharges(
            MechanicalEffects effects,
            AbilityScores abilityScores,
            int proficiencyBonus,
            int characterLevel
    ) {
        if (effects == null) {
            return null;
        }

        ChargeType chargeType = effects.maxChargesType();
        if (chargeType == null) {
            return effects.usesPerRest();
        }

        return switch (chargeType) {
            case FIXED -> {
                Integer fixed = effects.fixedMaxCharges() != null ? effects.fixedMaxCharges() : effects.usesPerRest();
                yield fixed != null ? fixed : 0;
            }
            case ABILITY_MODIFIER -> {
                String ability = effects.chargeAbility() != null ? effects.chargeAbility() : "STR";
                int score = abilityScores != null ? abilityScores.getScore(ability) : 10;
                int mod = AbilityScores.calculateModifier(score);
                yield Math.max(1, mod);
            }
            case PROFICIENCY -> Math.max(1, proficiencyBonus);
            case LEVEL -> Math.max(1, characterLevel);
        };
    }

    public static int calculateRecoveryAmount(
            RestType restType,
            MechanicalEffects effects,
            int currentCharges,
            int maxCharges,
            AbilityScores abilityScores,
            int proficiencyBonus,
            int characterLevel
    ) {
        if (restType == null || restType == RestType.NONE || maxCharges <= 0) {
            return 0;
        }

        ChargeRecoveryType recoveryType = resolveRecoveryType(restType, effects);
        int rawAmount = switch (recoveryType) {
            case NONE -> 0;
            case ALL -> maxCharges;
            case FIXED -> {
                Integer fixed = (restType == RestType.SHORT)
                        ? (effects != null ? effects.shortRestRecoveryFixed() : null)
                        : (effects != null ? effects.longRestRecoveryFixed() : null);
                yield fixed != null ? Math.max(0, fixed) : 0;
            }
            case MODIFIER -> {
                String ability = effects != null && effects.chargeAbility() != null ? effects.chargeAbility() : "STR";
                int score = abilityScores != null ? abilityScores.getScore(ability) : 10;
                int mod = AbilityScores.calculateModifier(score);
                yield Math.max(0, mod);
            }
            case PROFICIENCY -> Math.max(0, proficiencyBonus);
            case LEVEL -> Math.max(0, characterLevel);
        };

        int missing = Math.max(0, maxCharges - currentCharges);
        return Math.min(missing, rawAmount);
    }

    private static ChargeRecoveryType resolveRecoveryType(RestType restType, MechanicalEffects effects) {
        if (restType == RestType.SHORT) {
            if (effects != null && effects.shortRestRecoveryType() != null) {
                return effects.shortRestRecoveryType();
            }
            if (effects != null && effects.restType() == RestType.SHORT
                    && (effects.usesPerRest() != null || effects.maxChargesType() != null)) {
                return ChargeRecoveryType.ALL;
            }
            return ChargeRecoveryType.NONE;
        }

        if (restType == RestType.LONG) {
            if (effects != null && effects.longRestRecoveryType() != null) {
                return effects.longRestRecoveryType();
            }
            return ChargeRecoveryType.ALL;
        }

        return ChargeRecoveryType.NONE;
    }
}
