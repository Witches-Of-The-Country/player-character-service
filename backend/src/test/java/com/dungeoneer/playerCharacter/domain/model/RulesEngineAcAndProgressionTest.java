package com.dungeoneer.playerCharacter.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RulesEngineAcAndProgressionTest {

    private final Srd2014RulesEngine engine2014 = new Srd2014RulesEngine();
    private final Srd2024RulesEngine engine2024 = new Srd2024RulesEngine();

    @Test
    @DisplayName("Should correctly compute AC for HEAVY, MEDIUM, LIGHT, ROBE, and SHIELD")
    void shouldComputeAcAcrossAllArmorCategories() {
        int dexMod = 3; // +3 DEX

        // Heavy: Base 18 (Plate), ignores DEX
        ArmorMechanics heavyPlate = new ArmorMechanics(ArmorCategory.HEAVY, 18, 0, 15, true);
        assertEquals(18, engine2014.calculateArmorClass(dexMod, heavyPlate, null, 0, false, 0, false, 0));

        // Medium: Base 14 (Breastplate), max +2 DEX = 16
        ArmorMechanics medBreastplate = new ArmorMechanics(ArmorCategory.MEDIUM, 14, 0, 0, false);
        assertEquals(16, engine2014.calculateArmorClass(dexMod, medBreastplate, null, 0, false, 0, false, 0));

        // Light: Base 12 (Studded Leather) + full DEX (+3) = 15
        ArmorMechanics lightLeather = new ArmorMechanics(ArmorCategory.LIGHT, 12, 0, 0, false);
        assertEquals(15, engine2014.calculateArmorClass(dexMod, lightLeather, null, 0, false, 0, false, 0));

        // Robe + Magic bonus: Base 10 + full DEX (+3) + 1 magic = 14
        ArmorMechanics robe = new ArmorMechanics(ArmorCategory.ROBE, 10, 1, 0, false);
        assertEquals(14, engine2014.calculateArmorClass(dexMod, robe, null, 0, false, 0, false, 0));

        // Robe with Barbarian Unarmored Defense (+3 DEX, +2 CON) + 1 magic bonus = 10 + 3 + 2 + 1 = 16
        assertEquals(16, engine2014.calculateArmorClass(dexMod, robe, null, 0, true, 2, false, 0));

        // Add Shield (+2 base, +1 magic = +3) to Heavy Plate (18) = 21
        ArmorMechanics shield = new ArmorMechanics(ArmorCategory.SHIELD, 2, 1, 0, false);
        assertEquals(21, engine2014.calculateArmorClass(dexMod, heavyPlate, shield, 0, false, 0, false, 0));

        // Extra bonus (e.g. Defense feat +1)
        assertEquals(22, engine2014.calculateArmorClass(dexMod, heavyPlate, shield, 1, false, 0, false, 0));
    }

    @Test
    @DisplayName("Should lookup XP for levels 1-20 correctly")
    void shouldLookupXpForLevels() {
        assertEquals(0, engine2014.getXpForLevel(1));
        assertEquals(300, engine2014.getXpForLevel(2));
        assertEquals(900, engine2014.getXpForLevel(3));
        assertEquals(2700, engine2014.getXpForLevel(4));
        assertEquals(355000, engine2014.getXpForLevel(20));

        assertEquals(1, engine2014.getLevelForXp(0));
        assertEquals(1, engine2014.getLevelForXp(299));
        assertEquals(2, engine2014.getLevelForXp(300));
        assertEquals(3, engine2014.getLevelForXp(900));
        assertEquals(20, engine2014.getLevelForXp(400000));
    }
}
