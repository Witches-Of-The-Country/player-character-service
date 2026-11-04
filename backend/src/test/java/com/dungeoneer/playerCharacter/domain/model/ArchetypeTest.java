package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchetypeTest {

    @Test
    @DisplayName("should construct valid Archetype with all properties")
    void shouldConstructValidArchetype() {
        UUID id = UUID.randomUUID();
        Archetype archetype = new Archetype(
                id,
                "Fighter",
                HitDiceEnum.D10,
                "STRENGTH",
                List.of("STRENGTH", "CONSTITUTION"),
                List.of("ALL_ARMOR", "SHIELDS"),
                List.of("SIMPLE", "MARTIAL"),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        assertThat(archetype.getId()).isEqualTo(id);
        assertThat(archetype.getName()).isEqualTo("Fighter");
        assertThat(archetype.getHitDice()).isEqualTo(HitDiceEnum.D10);
        assertThat(archetype.getPrimaryAbility()).isEqualTo("STRENGTH");
        assertThat(archetype.getSavingThrowProficiencies()).containsExactly("STRENGTH", "CONSTITUTION");
        assertThat(archetype.getArmorProficiencies()).containsExactly("ALL_ARMOR", "SHIELDS");
        assertThat(archetype.getWeaponProficiencies()).containsExactly("SIMPLE", "MARTIAL");
        assertThat(archetype.getSpellcastingType()).isEqualTo(SpellcastingType.NONE);
        assertThat(archetype.getSpellcastingAbility()).isNull();
        assertThat(archetype.getRuleset()).isEqualTo(Ruleset.SRD_2024);
        assertThat(archetype.getSource()).isEqualTo("SRD");
        assertThat(archetype.getOwnerId()).isNull();
    }

    @Test
    @DisplayName("should fail validation when name is null or blank")
    void shouldFailWhenNameIsInvalid() {
        assertThatThrownBy(() -> new Archetype(
                UUID.randomUUID(),
                "   ",
                HitDiceEnum.D12,
                "STRENGTH",
                List.of("STRENGTH"),
                List.of(),
                List.of(),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("name cannot be blank");
    }

    @Test
    @DisplayName("should fail validation when hitDice is null")
    void shouldFailWhenHitDiceIsNull() {
        assertThatThrownBy(() -> new Archetype(
                UUID.randomUUID(),
                "Barbarian",
                null,
                "STRENGTH",
                List.of("STRENGTH"),
                List.of(),
                List.of(),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("Hit dice cannot be null");
    }

    @Test
    @DisplayName("should fail validation when ruleset is null")
    void shouldFailWhenRulesetIsNull() {
        assertThatThrownBy(() -> new Archetype(
                UUID.randomUUID(),
                "Barbarian",
                HitDiceEnum.D12,
                "STRENGTH",
                List.of("STRENGTH"),
                List.of(),
                List.of(),
                SpellcastingType.NONE,
                null,
                null,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("Ruleset cannot be null");
    }

    @Test
    @DisplayName("should provide immutable proficiency lists")
    void shouldProvideImmutableProficiencyLists() {
        Archetype archetype = new Archetype(
                UUID.randomUUID(),
                "Wizard",
                HitDiceEnum.D6,
                "INTELLIGENCE",
                List.of("INTELLIGENCE", "WISDOM"),
                List.of(),
                List.of("DAGGERS"),
                SpellcastingType.FULL,
                "INTELLIGENCE",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        List<String> saves = archetype.getSavingThrowProficiencies();
        assertThatThrownBy(() -> saves.add("CHARISMA"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
