package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterActionDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CharacterSheetHydratorTest {

    @Test
    @DisplayName("Weapon action adds Strength modifier and magic bonus to primary formula, preserves conditional rolls")
    void weaponActionAddsStrengthAndMagicBonus() {
        // STR 16 (+3), DEX 10 (+0), Level 1 (Proficiency +2)
        AbilityScores abilityScores = new AbilityScores(16, 10, 14, 10, 10, 10);
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // Lingue de fogo: Longsword +2 magic bonus, 1d8 slashing, +2d6 fire conditional, versatile 1d10
        DamageRoll primaryDamage = new DamageRoll("1d8", DamageType.SLASHING, false, null);
        DamageRoll fireDamage = new DamageRoll("2d6", DamageType.FIRE, true, "Flame activation");
        WeaponMechanics weaponMechanics = new WeaponMechanics(
                List.of(primaryDamage, fireDamage),
                "1d10",
                2,
                List.of(WeaponProperty.VERSATILE)
        );

        Item flameTongue = new Item(
                itemId,
                Ruleset.SRD_2024,
                "flame-tongue",
                "Lingue de fogo",
                "Lingue de fogo",
                "Weapon",
                "Martial Melee",
                "Rare",
                true,
                "1000 gp",
                "3 lb",
                "A flaming sword",
                "SRD",
                null,
                weaponMechanics
        );

        CharacterItem equippedSword = new CharacterItem(
                UUID.randomUUID(),
                characterId,
                itemId,
                1,
                true,
                false
        );

        PlayerCharacter character = new PlayerCharacter(
                characterId,
                "Valeros",
                1,
                12,
                12,
                0,
                10,
                0,
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                "",
                HpCalculationMethod.MAX,
                false,
                List.of(),
                List.of(),
                0,
                null,
                List.of(equippedSword),
                List.of(),
                CurrencyPouch.empty(),
                null,
                Instant.now(),
                Instant.now()
        );

        CharacterSheetResult response = CharacterSheetHydrator.toSheetResult(
                character,
                null,
                null,
                null,
                Map.of(itemId, flameTongue),
                Collections.emptyMap()
        );

        assertThat(response.actions()).isNotNull();
        CharacterActionDto weaponAction = response.actions().stream()
                .filter(a -> a.name().equals("Lingue de fogo"))
                .findFirst()
                .orElse(null);

        assertThat(weaponAction).isNotNull();
        // Attack bonus = STR (+3) + Prof (+2) + Magic (+2) = 7
        assertThat(weaponAction.attackBonus()).isEqualTo(7);

        // Damage rolls
        List<DamageRoll> rolls = weaponAction.damageRolls();
        assertThat(rolls).hasSize(2);

        // Primary: 1d8 + STR (+3) + Magic (+2) = 1d8+5
        assertThat(rolls.get(0).diceFormula()).isEqualTo("1d8+5");
        assertThat(rolls.get(0).damageType()).isEqualTo(DamageType.SLASHING);
        assertThat(rolls.get(0).conditional()).isFalse();

        // Secondary: 2d6 FIRE (conditional, does not add STR)
        assertThat(rolls.get(1).diceFormula()).isEqualTo("2d6");
        assertThat(rolls.get(1).damageType()).isEqualTo(DamageType.FIRE);
        assertThat(rolls.get(1).conditional()).isTrue();

        // Versatile: 1d10 + STR (+3) + Magic (+2) = 1d10+5
        assertThat(weaponAction.versatileDice()).isEqualTo("1d10+5");
    }

    @Test
    @DisplayName("Should dynamically recalculate feat charges and clamp current charges on hydration")
    void shouldDynamicallyRecalculateFeatChargesOnHydration() {
        // CHA 18 (+4), Level 3, Prof +2
        AbilityScores abilityScores = new AbilityScores(10, 10, 10, 10, 10, 18);
        UUID characterId = UUID.randomUUID();
        UUID featId = UUID.randomUUID();

        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), "Inspiração Mística", "BONUS_ACTION",
                "1d8", null, null, null, false, null, List.of(), null,
                ChargeType.ABILITY_MODIFIER, "CHA", null,
                ChargeRecoveryType.NONE, null,
                ChargeRecoveryType.ALL, null
        );

        Feat feat = new Feat(
                featId, Ruleset.HOMEBREW, "inspiracao-mistica", "Mystic Inspiration", "Inspiração Mística",
                null, "Concede bônus", "HOMEBREW", null, FeatType.GENERAL, effects
        );

        // CharacterFeat was stored with stale currentCharges = 10, maxCharges = null
        CharacterFeat charFeat = new CharacterFeat(
                UUID.randomUUID(), characterId, featId, 1, 10, null, "{}"
        );

        PlayerCharacter character = new PlayerCharacter(
                characterId, "Bard", 3, 20, 20, 0, 10, 0, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                abilityScores, "", HpCalculationMethod.MAX, false,
                List.of(), List.of(), 0, null, List.of(), List.of(charFeat),
                CurrencyPouch.empty(), null, Instant.now(), Instant.now()
        );

        CharacterSheetResult response = CharacterSheetHydrator.toSheetResult(
                character, null, null, null, Collections.emptyMap(), Map.of(featId, feat)
        );

        assertThat(response.feats()).hasSize(1);
        var hydratedFeat = response.feats().get(0);
        // CHA modifier is +4, so dynamic maxCharges must be 4
        assertThat(hydratedFeat.maxCharges()).isEqualTo(4);
        // currentCharges was 10, must be clamped to 4
        assertThat(hydratedFeat.currentCharges()).isEqualTo(4);

        assertThat(response.actions()).isNotNull();
        var action = response.actions().stream().filter(a -> a.name().equals("Inspiração Mística")).findFirst().orElse(null);
        assertThat(action).isNotNull();
        assertThat(action.maxCharges()).isEqualTo(4);
        assertThat(action.currentCharges()).isEqualTo(4);
    }

    @Test
    @DisplayName("TR-01: Equip Half Plate (half-plate-armor-2024) on character with DEX 14 calculates AC 17")
    void halfPlateCalculatesAcWithMediumArmorDexCap() {
        // DEX 14 (+2)
        AbilityScores abilityScores = new AbilityScores(10, 14, 10, 10, 10, 10);
        UUID characterId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        ArmorMechanics halfPlateMechanics = new ArmorMechanics(
                ArmorCategory.MEDIUM, 15, 0, 0, true
        );
        Item halfPlate = new Item(
                itemId, Ruleset.SRD_2024, "half-plate-armor-2024", "Half Plate Armor",
                "Armadura de placas parcial", "ARMOR", "Armadura", "COMMON", false,
                "750 po", "18,1 kg", "Classe de Armadura: 15", "SRD", null, halfPlateMechanics
        );
        CharacterItem equippedHalfPlate = new CharacterItem(
                UUID.randomUUID(), characterId, itemId, 1, true, false
        );

        PlayerCharacter character = new PlayerCharacter(
                characterId, "Fighter", 1, 12, 12, 0, 10, 2, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                abilityScores, "", HpCalculationMethod.MAX, false,
                List.of(), List.of(), 0, null, List.of(equippedHalfPlate), List.of(),
                CurrencyPouch.empty(), null, Instant.now(), Instant.now()
        );

        CharacterSheetResult response = CharacterSheetHydrator.toSheetResult(
                character, null, null, null, Map.of(itemId, halfPlate), Collections.emptyMap()
        );

        assertThat(response.armorClass()).isEqualTo(17);
    }

    @Test
    @DisplayName("TR-02 & TE-01: WondrousMechanics applies ability score overrides with Math.max and passive AC/save bonuses")
    void wondrousItemAppliesAbilityOverridesAndBonuses() {
        // Base STR 10, DEX 14, CON 12, INT 20, WIS 10, CHA 10
        AbilityScores abilityScores = new AbilityScores(10, 14, 12, 20, 10, 10);
        UUID characterId = UUID.randomUUID();
        UUID gauntletsId = UUID.randomUUID();
        UUID headbandId = UUID.randomUUID();
        UUID ringId = UUID.randomUUID();

        // Gauntlets of Ogre Power: sets STR to 19 (requires attunement)
        Item gauntlets = new Item(
                gauntletsId, Ruleset.SRD_2024, "gauntlets-of-ogre-power-2024", "Gauntlets of Ogre Power",
                "Manoplas do Poder de Ogro", "WONDROUS", "Item Maravilhoso", "UNCOMMON", true,
                "1000 po", "1 kg", "STR 19", "SRD", null,
                new WondrousMechanics(Map.of(), Map.of("STR", 19), 0, 0)
        );
        // Headband of Intellect: sets INT to 19 (character already has INT 20 -> should stay 20 per TE-01)
        Item headband = new Item(
                headbandId, Ruleset.SRD_2024, "headband-of-intellect-2024", "Headband of Intellect",
                "Tiara do Intelecto", "WONDROUS", "Item Maravilhoso", "UNCOMMON", true,
                "1000 po", "0,5 kg", "INT 19", "SRD", null,
                new WondrousMechanics(Map.of(), Map.of("INT", 19), 0, 0)
        );
        // Ring of Protection: +1 AC and +1 saving throws
        Item ring = new Item(
                ringId, Ruleset.SRD_2024, "ring-of-protection-2024", "Ring of Protection",
                "Anel de Proteção", "RING", "Anel", "RARE", true,
                "3500 po", "0 kg", "+1 AC and Saves", "SRD", null,
                new WondrousMechanics(Map.of(), Map.of(), 1, 1)
        );

        List<CharacterItem> charItems = List.of(
                new CharacterItem(UUID.randomUUID(), characterId, gauntletsId, 1, true, true),
                new CharacterItem(UUID.randomUUID(), characterId, headbandId, 1, true, true),
                new CharacterItem(UUID.randomUUID(), characterId, ringId, 1, true, true)
        );

        PlayerCharacter character = new PlayerCharacter(
                characterId, "Paladin", 1, 12, 12, 0, 10, 2, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                abilityScores, "", HpCalculationMethod.MAX, false,
                List.of(), List.of("Athletics"), 0, null, charItems, List.of(),
                CurrencyPouch.empty(), null, Instant.now(), Instant.now()
        );

        CharacterSheetResult response = CharacterSheetHydrator.toSheetResult(
                character,
                null,
                null,
                null,
                Map.of(gauntletsId, gauntlets, headbandId, headband, ringId, ring),
                Collections.emptyMap()
        );

        // Effective STR is 19 (+4 mod), Effective INT remains 20 (+5 mod)
        assertThat(response.abilityScores().getStrength()).isEqualTo(19);
        assertThat(response.abilityScores().getStrengthModifier()).isEqualTo(4);
        assertThat(response.abilityScores().getIntelligence()).isEqualTo(20);
        assertThat(response.abilityScores().getIntelligenceModifier()).isEqualTo(5);

        // AC = 10 + DEX (+2) + Ring (+1) = 13
        assertThat(response.armorClass()).isEqualTo(13);

        // Athletics skill = STR mod (+4) + Prof (+2) = 6
        assertThat(response.skills().get("Athletics")).isEqualTo(6);

        // Saving throws include Ring's +1 savingThrowBonus: STR save = +4 + 1 = 5
        assertThat(response.savingThrows().get("STRENGTH")).isEqualTo(5);
    }

    @Test
    @DisplayName("TE-02: Legacy feat JSON with maxUses and rechargeOn deserializes and hydrates Channel Divinity action with charges")
    void legacyFeatJsonDeserializesAndHydratesChannelDivinity() {
        String legacyJson = "{\"grantsAction\":true,\"actionType\":\"ACTION\",\"actionName\":\"Canalizar Divindade\",\"maxUses\":2,\"rechargeOn\":\"SHORT_REST\"}";
        MechanicalEffects effects = com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity.deserializeEffects(legacyJson);

        assertThat(effects.usesPerRest()).isEqualTo(2);
        assertThat(effects.fixedMaxCharges()).isEqualTo(2);
        assertThat(effects.restType()).isEqualTo(RestType.SHORT);
        assertThat(effects.shortRestRecoveryType()).isEqualTo(ChargeRecoveryType.ALL);

        UUID characterId = UUID.randomUUID();
        UUID featId = UUID.randomUUID();
        Feat channelDivinity = new Feat(
                featId, Ruleset.SRD_2024, "channel-divinity-2024", "Channel Divinity", "Canalizar Divindade",
                "Clérigo Nível 2", "Canalização divina", "SRD", null, FeatType.CLASS, effects
        );
        CharacterFeat charFeat = new CharacterFeat(
                UUID.randomUUID(), characterId, featId, 2, null, null, "{}"
        );
        PlayerCharacter character = new PlayerCharacter(
                characterId, "Cleric", 2, 16, 16, 0, 10, 0, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new AbilityScores(10, 10, 14, 10, 16, 10), "", HpCalculationMethod.MAX, false,
                List.of(), List.of(), 0, null, List.of(), List.of(charFeat),
                CurrencyPouch.empty(), null, Instant.now(), Instant.now()
        );

        CharacterSheetResult response = CharacterSheetHydrator.toSheetResult(
                character, null, null, null, Collections.emptyMap(), Map.of(featId, channelDivinity)
        );

        var action = response.actions().stream()
                .filter(a -> a.name().equals("Canalizar Divindade"))
                .findFirst()
                .orElse(null);
        assertThat(action).isNotNull();
        assertThat(action.maxCharges()).isEqualTo(2);
        assertThat(action.currentCharges()).isEqualTo(2);
    }
}
