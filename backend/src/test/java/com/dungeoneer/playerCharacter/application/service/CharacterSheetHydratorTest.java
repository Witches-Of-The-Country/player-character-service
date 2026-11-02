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
}
