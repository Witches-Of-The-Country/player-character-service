package com.dungeoneer.playerCharacter.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeatChargeCalculatorTest {

    private final AbilityScores standardScores = new AbilityScores(16, 14, 12, 10, 8, 18);
    // STR 16 (+3), DEX 14 (+2), CON 12 (+1), INT 10 (0), WIS 8 (-1), CHA 18 (+4)

    @Test
    @DisplayName("TP-01: Should calculate max charges for all formula types (FIXED, ABILITY_MODIFIER, PROFICIENCY, LEVEL)")
    void shouldCalculateMaxChargesForAllFormulas() {
        // FIXED
        MechanicalEffects fixedEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.FIXED, null, 4, null, null, null, null
        );
        assertEquals(4, FeatChargeCalculator.calculateMaxCharges(fixedEffects, standardScores, 2, 3));

        // ABILITY_MODIFIER (CHA +4)
        MechanicalEffects chaEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.ABILITY_MODIFIER, "CHA", null, null, null, null, null
        );
        assertEquals(4, FeatChargeCalculator.calculateMaxCharges(chaEffects, standardScores, 2, 3));

        // PROFICIENCY (pb = 3)
        MechanicalEffects profEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.PROFICIENCY, null, null, null, null, null, null
        );
        assertEquals(3, FeatChargeCalculator.calculateMaxCharges(profEffects, standardScores, 3, 5));

        // LEVEL (level = 7)
        MechanicalEffects levelEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.LEVEL, null, null, null, null, null, null
        );
        assertEquals(7, FeatChargeCalculator.calculateMaxCharges(levelEffects, standardScores, 3, 7));
    }

    @Test
    @DisplayName("TP-01 Fallback: Should fallback to legacy usesPerRest if maxChargesType is null")
    void shouldFallbackToLegacyUsesPerRest() {
        MechanicalEffects legacyEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, 2, RestType.SHORT
        );
        assertEquals(2, FeatChargeCalculator.calculateMaxCharges(legacyEffects, standardScores, 2, 1));

        MechanicalEffects emptyEffects = MechanicalEffects.empty();
        assertNull(FeatChargeCalculator.calculateMaxCharges(emptyEffects, standardScores, 2, 1));
        assertNull(FeatChargeCalculator.calculateMaxCharges(null, standardScores, 2, 1));
    }

    @Test
    @DisplayName("TE-01: ABILITY_MODIFIER with non-positive modifier clamps max charges to minimum 1")
    void shouldClampMaxChargesToMinimumOneWhenModifierIsNonPositive() {
        // WIS 8 -> modifier -1 -> must evaluate to 1
        MechanicalEffects wisEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.ABILITY_MODIFIER, "WIS", null, null, null, null, null
        );
        assertEquals(1, FeatChargeCalculator.calculateMaxCharges(wisEffects, standardScores, 2, 3));

        // INT 10 -> modifier 0 -> must evaluate to 1
        MechanicalEffects intEffects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.ABILITY_MODIFIER, "INT", null, null, null, null, null
        );
        assertEquals(1, FeatChargeCalculator.calculateMaxCharges(intEffects, standardScores, 2, 3));
    }

    @Test
    @DisplayName("TP-02: Should calculate recovery amounts per rest type independently")
    void shouldCalculateRecoveryAmountsPerRestType() {
        // Short rest: FIXED 2, Long rest: ALL
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.FIXED, null, 5,
                ChargeRecoveryType.FIXED, 2,
                ChargeRecoveryType.ALL, null
        );

        // Short rest when current is 1/5 -> recovers 2
        int shortRecovered = FeatChargeCalculator.calculateRecoveryAmount(
                RestType.SHORT, effects, 1, 5, standardScores, 2, 3
        );
        assertEquals(2, shortRecovered);

        // Long rest when current is 1/5 -> recovers 4 (to reach 5)
        int longRecovered = FeatChargeCalculator.calculateRecoveryAmount(
                RestType.LONG, effects, 1, 5, standardScores, 2, 3
        );
        assertEquals(4, longRecovered);
    }

    @Test
    @DisplayName("TP-02 Defaults: Long rest defaults to ALL and Short rest defaults to NONE")
    void shouldApplyDefaultRecoveryRulesWhenUnspecified() {
        MechanicalEffects effectsWithoutRecovery = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.FIXED, null, 4,
                null, null,
                null, null
        );

        // Short rest defaults to NONE -> 0
        assertEquals(0, FeatChargeCalculator.calculateRecoveryAmount(
                RestType.SHORT, effectsWithoutRecovery, 1, 4, standardScores, 2, 3
        ));

        // Long rest defaults to ALL -> 3
        assertEquals(3, FeatChargeCalculator.calculateRecoveryAmount(
                RestType.LONG, effectsWithoutRecovery, 1, 4, standardScores, 2, 3
        ));
    }

    @Test
    @DisplayName("TP-02 Legacy: Legacy feat with RestType.SHORT recovers ALL on short and long rest")
    void shouldRecoverLegacyShortRestFeat() {
        MechanicalEffects legacyShort = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, 3, RestType.SHORT
        );

        assertEquals(2, FeatChargeCalculator.calculateRecoveryAmount(
                RestType.SHORT, legacyShort, 1, 3, standardScores, 2, 1
        ));
        assertEquals(2, FeatChargeCalculator.calculateRecoveryAmount(
                RestType.LONG, legacyShort, 1, 3, standardScores, 2, 1
        ));
    }

    @Test
    @DisplayName("TE-02: Recovery attempting to exceed max charges is clamped to max")
    void shouldClampRecoveryToNotExceedMaxCharges() {
        // Short rest recovery fixed 3, but current is 4 and max is 5 (missing 1)
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.FIXED, null, 5,
                ChargeRecoveryType.FIXED, 3,
                ChargeRecoveryType.ALL, null
        );

        int recovered = FeatChargeCalculator.calculateRecoveryAmount(
                RestType.SHORT, effects, 4, 5, standardScores, 2, 3
        );
        assertEquals(1, recovered); // Only 1 charge needed to reach 5
    }

    @Test
    @DisplayName("TE-02 Edge: Negative modifier recovery does not drain charges (yields 0)")
    void shouldNotDrainChargesOnNegativeModifierRecovery() {
        MechanicalEffects wisRecovery = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.FIXED, "WIS", 4,
                ChargeRecoveryType.MODIFIER, null,
                ChargeRecoveryType.ALL, null
        );

        // WIS mod is -1 -> should recover 0, not -1
        int recovered = FeatChargeCalculator.calculateRecoveryAmount(
                RestType.SHORT, wisRecovery, 2, 4, standardScores, 2, 3
        );
        assertEquals(0, recovered);
    }
}
