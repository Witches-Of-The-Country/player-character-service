package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineageTest {

    @Test
    @DisplayName("should construct valid Lineage for SRD 2014 with ability bonuses map")
    void shouldConstructValidLineage2014() {
        UUID id = UUID.randomUUID();
        Map<String, Integer> bonuses = Map.of("DEXTERITY", 2);
        List<String> traits = List.of("Darkvision", "Keen Senses", "Fey Ancestry", "Trance");

        Lineage lineage = new Lineage(
                id,
                "Elf",
                "Magical grace",
                30,
                "MEDIUM",
                bonuses,
                traits,
                Ruleset.SRD_2014,
                "SRD",
                null
        );

        assertThat(lineage.getId()).isEqualTo(id);
        assertThat(lineage.getName()).isEqualTo("Elf");
        assertThat(lineage.getSpeed()).isEqualTo(30);
        assertThat(lineage.getSize()).isEqualTo("MEDIUM");
        assertThat(lineage.getAbilityBonuses()).containsEntry("DEXTERITY", 2);
        assertThat(lineage.getTraitsSummary()).containsExactlyElementsOf(traits);
        assertThat(lineage.getRuleset()).isEqualTo(Ruleset.SRD_2014);
    }

    @Test
    @DisplayName("should construct valid Lineage for SRD 2024 with empty ability bonuses")
    void shouldConstructValidLineage2024() {
        Lineage lineage = new Lineage(
                UUID.randomUUID(),
                "Human",
                "Adaptable",
                30,
                "MEDIUM",
                Map.of(),
                List.of("Resourceful", "Skillful", "Versatile"),
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        assertThat(lineage.getAbilityBonuses()).isEmpty();
        assertThat(lineage.getTraitsSummary()).hasSize(3);
    }

    @Test
    @DisplayName("should fail validation when speed is less than or equal to 0")
    void shouldFailWhenSpeedIsInvalid() {
        assertThatThrownBy(() -> new Lineage(
                UUID.randomUUID(),
                "Dwarf",
                "Stout",
                0,
                "MEDIUM",
                Map.of(),
                List.of(),
                Ruleset.SRD_2024,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("Speed must be greater than 0");
    }

    @Test
    @DisplayName("should fail validation when name is blank")
    void shouldFailWhenNameIsBlank() {
        assertThatThrownBy(() -> new Lineage(
                UUID.randomUUID(),
                "",
                "Stout",
                25,
                "MEDIUM",
                Map.of(),
                List.of(),
                Ruleset.SRD_2024,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("name cannot be blank");
    }

    @Test
    @DisplayName("should provide immutable traits list and ability bonuses map")
    void shouldProvideImmutableCollections() {
        Lineage lineage = new Lineage(
                UUID.randomUUID(),
                "Elf",
                "Graceful",
                30,
                "MEDIUM",
                Map.of("DEXTERITY", 2),
                List.of("Darkvision"),
                Ruleset.SRD_2014,
                "SRD",
                null
        );

        assertThatThrownBy(() -> lineage.getTraitsSummary().add("New Trait"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> lineage.getAbilityBonuses().put("WISDOM", 1))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
