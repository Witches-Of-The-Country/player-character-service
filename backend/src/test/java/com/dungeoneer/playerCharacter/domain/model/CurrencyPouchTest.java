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
