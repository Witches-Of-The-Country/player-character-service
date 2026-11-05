package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMechanicsTest {

    @Test
    @DisplayName("Should validate valid weapon mechanics")
    void shouldValidateValidWeaponMechanics() {
        DamageRoll primary = new DamageRoll("1d8", DamageType.SLASHING);
        DamageRoll secondary = new DamageRoll("1d6", DamageType.FIRE);

        WeaponMechanics weapon = new WeaponMechanics(
                List.of(primary, secondary),
                "1d10",
                1,
                List.of(WeaponProperty.VERSATILE, WeaponProperty.FINESSE)
        );

        assertEquals(2, weapon.damageRolls().size());
        assertEquals("1d10", weapon.versatileDice());
        assertEquals(1, weapon.magicBonus());
        assertTrue(weapon.properties().contains(WeaponProperty.VERSATILE));
    }

    @Test
    @DisplayName("Should throw exception when weapon has no damage rolls or invalid dice formula")
    void shouldThrowExceptionWhenWeaponHasNoDamageRollsOrInvalidFormula() {
        assertThrows(CharacterValidationException.class, () ->
                new WeaponMechanics(List.of(), null, 0, List.of())
        );

        assertThrows(CharacterValidationException.class, () ->
                new DamageRoll("invalid", DamageType.SLASHING)
        );

        assertThrows(CharacterValidationException.class, () ->
                new WeaponMechanics(
                        List.of(new DamageRoll("1d8", DamageType.SLASHING)),
                        "invalid_versatile",
                        0,
                        List.of()
                )
        );
    }

    @Test
    @DisplayName("Should validate armor mechanics across categories")
    void shouldValidateArmorMechanics() {
        ArmorMechanics plate = new ArmorMechanics(
                ArmorCategory.HEAVY,
                18,
                0,
                15,
                true
        );

        assertEquals(ArmorCategory.HEAVY, plate.armorCategory());
        assertEquals(18, plate.baseAc());
        assertEquals(15, plate.strengthRequirement());
        assertTrue(plate.stealthDisadvantage());

        ArmorMechanics shield = new ArmorMechanics(
                ArmorCategory.SHIELD,
                2,
                1,
                0,
                false
        );
        assertEquals(ArmorCategory.SHIELD, shield.armorCategory());
        assertEquals(2, shield.baseAc());
        assertEquals(1, shield.magicBonus());
    }

    @Test
    @DisplayName("Should validate consumable mechanics")
    void shouldValidateConsumableMechanics() {
        ConsumableMechanics potion = new ConsumableMechanics(
                ConsumableEffectType.HEAL_HP,
                "2d4+2",
                "Cura instantânea"
        );

        assertEquals(ConsumableEffectType.HEAL_HP, potion.effectType());
        assertEquals("2d4+2", potion.diceFormula());

        assertThrows(CharacterValidationException.class, () ->
                new ConsumableMechanics(null, "1d4", "")
        );
    }

    @Test
    @DisplayName("Should validate wondrous mechanics with overrides, bonuses, AC and save bonuses")
    void shouldValidateWondrousMechanics() {
        WondrousMechanics gauntlets = new WondrousMechanics(
                java.util.Map.of("CON", 2),
                java.util.Map.of("STR", 19),
                1,
                1
        );

        assertEquals(2, gauntlets.abilityBonuses().get("CON"));
        assertEquals(19, gauntlets.abilityOverrides().get("STR"));
        assertEquals(1, gauntlets.acBonus());
        assertEquals(1, gauntlets.savingThrowBonus());

        assertThrows(CharacterValidationException.class, () ->
                new WondrousMechanics(java.util.Map.of(), java.util.Map.of("STR", 35), 0, 0)
        );
    }
}
