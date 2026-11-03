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

    @Test
    @DisplayName("Should create mechanical effects with spellChoice and abilityBonusChoice")
    void shouldCreateMechanicalEffectsWithSpellChoice() {
        SpellChoice spellChoice = new SpellChoice(2, 1, 1, List.of("CLERIC", "DRUID", "WIZARD"), false);
        AbilityBonusChoice bonusChoice = new AbilityBonusChoice(1, List.of("STR", "DEX"), 1);
        FeatPrerequisite prereq = new FeatPrerequisite("LEVEL", null, 4, "Nível 4+");

        MechanicalEffects effects = new MechanicalEffects(
                Map.of(),
                0,
                0,
                0,
                0,
                List.of(),
                null,
                null,
                null,
                null,
                RestType.NONE,
                bonusChoice,
                false,
                null,
                List.of(prereq),
                spellChoice
        );

        assertNotNull(effects.spellChoice());
        assertEquals(2, effects.spellChoice().cantripsCount());
        assertEquals(1, effects.spellChoice().spellsCount());
        assertEquals(1, effects.spellChoice().spellLevel());
        assertFalse(effects.spellChoice().isRitualOnly());
        assertEquals(List.of("CLERIC", "DRUID", "WIZARD"), effects.spellChoice().allowedClasses());

        assertNotNull(effects.abilityBonusChoice());
        assertEquals(1, effects.abilityBonusChoice().chooseCount());
        assertEquals(List.of("STR", "DEX"), effects.abilityBonusChoice().allowedAbilities());

        assertEquals(1, effects.prerequisiteRules().size());
        assertEquals("LEVEL", effects.prerequisiteRules().get(0).type());
    }

    @Test
    @DisplayName("Should create mechanical effects with structured charge configuration")
    void shouldCreateMechanicalEffectsWithChargeConfiguration() {
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.ABILITY_MODIFIER, "cha ", null,
                ChargeRecoveryType.FIXED, 1,
                ChargeRecoveryType.ALL, null
        );

        assertEquals(ChargeType.ABILITY_MODIFIER, effects.maxChargesType());
        assertEquals("CHA", effects.chargeAbility());
        assertEquals(ChargeRecoveryType.FIXED, effects.shortRestRecoveryType());
        assertEquals(1, effects.shortRestRecoveryFixed());
        assertEquals(ChargeRecoveryType.ALL, effects.longRestRecoveryType());
        assertNull(effects.longRestRecoveryFixed());
    }

    @Test
    @DisplayName("Should throw exception on negative fixed charges or negative recovery")
    void shouldThrowExceptionOnNegativeFixedChargesOrRecovery() {
        assertThrows(CharacterValidationException.class, () ->
                new MechanicalEffects(
                        Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                        null, false, null, List.of(), null,
                        ChargeType.FIXED, null, -1,
                        null, null, null, null
                )
        );

        assertThrows(CharacterValidationException.class, () ->
                new MechanicalEffects(
                        Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                        null, false, null, List.of(), null,
                        ChargeType.FIXED, null, 1,
                        ChargeRecoveryType.FIXED, -2,
                        null, null
                )
        );

        assertThrows(CharacterValidationException.class, () ->
                new MechanicalEffects(
                        Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                        null, false, null, List.of(), null,
                        ChargeType.FIXED, null, 1,
                        null, null,
                        ChargeRecoveryType.FIXED, -3
                )
        );
    }
}
