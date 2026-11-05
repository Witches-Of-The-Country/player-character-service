package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyPouchTest {

    @Test
    @DisplayName("should calculate total copper correctly across all denominations")
    void shouldCalculateTotalCopper() {
        CurrencyPouch pouch = new CurrencyPouch(5, 4, 3, 2, 1);
        // 5*1 + 4*10 + 3*50 + 2*100 + 1*1000 = 5 + 40 + 150 + 200 + 1000 = 1395 CP
        assertThat(pouch.toTotalCopper()).isEqualTo(1395L);
    }

    @Test
    @DisplayName("should deduct with automatic coin conversion and change calculation")
    void shouldDeductWithConversionAndChange() {
        // Player has 1 Gold Piece (100 CP) and buys an item costing 5 Silver Pieces (50 CP)
        CurrencyPouch pouch = new CurrencyPouch(0, 0, 0, 1, 0);
        CurrencyPouch cost = new CurrencyPouch(0, 5, 0, 0, 0);

        assertThat(pouch.canAfford(cost)).isTrue();

        CurrencyPouch remaining = pouch.deductWithConversion(cost);
        // Remaining should be 50 CP = 5 Silver Pieces (or 5 SP, 0 GP, 0 CP)
        assertThat(remaining.toTotalCopper()).isEqualTo(50L);
        assertThat(remaining.sp()).isEqualTo(5);
        assertThat(remaining.gp()).isEqualTo(0);
        assertThat(remaining.cp()).isEqualTo(0);
    }

    @Test
    @DisplayName("should preserve unaffected denominations without auto-consolidation on exact deduction")
    void shouldPreserveUnaffectedDenominationsOnExactDeduction() {
        // Acceptance Criterion 1: 100 CP, 20 SP, 10 EP, 10 GP, 2 PP buying 5 GP (500 CP)
        CurrencyPouch pouch = new CurrencyPouch(100, 20, 10, 10, 2);
        CurrencyPouch cost = new CurrencyPouch(0, 0, 0, 5, 0);

        CurrencyPouch remaining = pouch.deductWithConversion(cost);

        assertThat(remaining.cp()).isEqualTo(100);
        assertThat(remaining.sp()).isEqualTo(20);
        assertThat(remaining.ep()).isEqualTo(10);
        assertThat(remaining.gp()).isEqualTo(5);
        assertThat(remaining.pp()).isEqualTo(2);
        assertThat(remaining.toTotalCopper()).isEqualTo(pouch.toTotalCopper() - 500L);
    }

    @Test
    @DisplayName("should break single smallest higher coin and return change without consolidating untouched coins")
    void shouldBreakSingleSmallestHigherCoinAndReturnChange() {
        // Acceptance Criterion 2: 0 SP, 5 GP buying 5 SP (50 CP)
        CurrencyPouch pouch = new CurrencyPouch(0, 0, 0, 5, 0);
        CurrencyPouch cost = new CurrencyPouch(0, 5, 0, 0, 0);

        CurrencyPouch remaining = pouch.deductWithConversion(cost);

        assertThat(remaining.cp()).isEqualTo(0);
        assertThat(remaining.sp()).isEqualTo(5);
        assertThat(remaining.ep()).isEqualTo(0);
        assertThat(remaining.gp()).isEqualTo(4);
        assertThat(remaining.pp()).isEqualTo(0);
    }

    @Test
    @DisplayName("should break higher coin preserving electrum when higher coin covers cost")
    void shouldPreserveElectrumWhenBreakingHigherCoin() {
        // Player has 10 EP (500 CP) and 1 GP (100 CP), buys item costing 60 CP (6 SP)
        // 1 GP (100 CP) covers the 60 CP cost by itself; 10 EP must remain untouched
        CurrencyPouch pouch = new CurrencyPouch(0, 0, 10, 1, 0);

        CurrencyPouch remaining = pouch.deductWithConversion(60L);

        assertThat(remaining.ep()).isEqualTo(10);
        assertThat(remaining.gp()).isEqualTo(0);
        assertThat(remaining.sp()).isEqualTo(4);
        assertThat(remaining.cp()).isEqualTo(0);
        assertThat(remaining.toTotalCopper()).isEqualTo(600L - 60L);
    }

    @Test
    @DisplayName("should break platinum coin and return standard change in gold and silver")
    void shouldBreakPlatinumCoinAndReturnStandardChange() {
        // Player has 1 PP (1000 CP), buys item costing 50 CP (5 SP)
        CurrencyPouch pouch = new CurrencyPouch(0, 0, 0, 0, 1);

        CurrencyPouch remaining = pouch.deductWithConversion(50L);

        assertThat(remaining.pp()).isEqualTo(0);
        assertThat(remaining.gp()).isEqualTo(9);
        assertThat(remaining.sp()).isEqualTo(5);
        assertThat(remaining.cp()).isEqualTo(0);
        assertThat(remaining.toTotalCopper()).isEqualTo(950L);
    }

    @Test
    @DisplayName("should pay composite cost across multiple coins when no single coin suffices")
    void shouldPayCompositeCostAcrossMultipleCoins() {
        // Player has 2 GP (200 CP), buys item costing 150 CP (1.5 GP)
        // 1 GP paid whole (100 CP), remaining 50 CP breaks the second 1 GP -> leaves 5 SP
        CurrencyPouch pouch = new CurrencyPouch(0, 0, 0, 2, 0);

        CurrencyPouch remaining = pouch.deductWithConversion(150L);

        assertThat(remaining.gp()).isEqualTo(0);
        assertThat(remaining.sp()).isEqualTo(5);
        assertThat(remaining.cp()).isEqualTo(0);
        assertThat(remaining.toTotalCopper()).isEqualTo(50L);
    }

    @Test
    @DisplayName("should throw exception when funds are insufficient")
    void shouldThrowWhenInsufficientFunds() {
        // 4 Silver Pieces (40 CP) vs 1 Gold Piece (100 CP)
        CurrencyPouch pouch = new CurrencyPouch(0, 4, 0, 0, 0);
        CurrencyPouch cost = new CurrencyPouch(0, 0, 0, 1, 0);

        assertThat(pouch.canAfford(cost)).isFalse();
        assertThatThrownBy(() -> pouch.deductWithConversion(cost))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Fundos insuficientes");
    }

    @Test
    @DisplayName("should reject negative coin amounts")
    void shouldRejectNegativeCoins() {
        assertThatThrownBy(() -> new CurrencyPouch(-1, 0, 0, 0, 0))
                .isInstanceOf(CharacterValidationException.class);
    }

    @Test
    @DisplayName("should parse legacy and localized cost strings correctly")
    void shouldParseCostStrings() {
        CurrencyPouch p1 = CurrencyPouch.parseCostString("50 po");
        assertThat(p1.gp()).isEqualTo(50);

        CurrencyPouch p2 = CurrencyPouch.parseCostString("15 gp");
        assertThat(p2.gp()).isEqualTo(15);

        CurrencyPouch p3 = CurrencyPouch.parseCostString("2 pp, 5 pc");
        assertThat(p3.sp()).isEqualTo(2); // pp = prata / silver
        assertThat(p3.cp()).isEqualTo(5);

        CurrencyPouch p4 = CurrencyPouch.parseCostString("1 pl");
        assertThat(p4.pp()).isEqualTo(1); // pl = platina
    }
}
