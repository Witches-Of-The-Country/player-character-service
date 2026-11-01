package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MechanicalEffectsTest {

    @Test
    @DisplayName("Should create valid mechanical effects with action and rest charges")
    void shouldCreateValidMechanicalEffects() {
        MechanicalEffects effects = new MechanicalEffects(
                Map.of("STR", 1),
                1,
                2,
                10,
                2,
                List.of("Heavy Armor"),
                "Retomar o Fôlego",
                "BONUS_ACTION",
                "1d10",
                1,
                RestType.SHORT
        );

        assertEquals(1, effects.abilityBonuses().get("STR"));
        assertEquals(1, effects.acBonus());
        assertEquals(2, effects.initiativeBonus());
        assertEquals(10, effects.speedBonus());
        assertEquals(2, effects.hpPerLevelBonus());
        assertTrue(effects.grantedProficiencies().contains("Heavy Armor"));
        assertEquals("Retomar o Fôlego", effects.actionName());
        assertEquals(RestType.SHORT, effects.restType());
    }

    @Test
    @DisplayName("Should throw exception on invalid dice formula or negative charges")
    void shouldThrowExceptionOnInvalidDiceOrNegativeCharges() {
        assertThrows(CharacterValidationException.class, () ->
                new MechanicalEffects(
                        Map.of(),
                        0,
                        0,
                        0,
                        0,
                        List.of(),
                        "Test",
                        "ACTION",
                        "invalid_dice",
                        1,
                        RestType.SHORT
                )
        );

        assertThrows(CharacterValidationException.class, () ->
                new MechanicalEffects(
                        Map.of(),
                        0,
                        0,
                        0,
                        0,
                        List.of(),
                        "Test",
                        "ACTION",
                        "1d6",
                        -1,
                        RestType.SHORT
                )
        );
    }
}
