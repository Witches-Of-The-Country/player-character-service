package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BackgroundTest {

    @Test
    @DisplayName("should construct valid Background for SRD 2024 with 3 allowed abilities and origin feat")
    void shouldConstructValidBackground2024() {
        UUID id = UUID.randomUUID();
        Background background = new Background(
                id,
                "Acolyte",
                "Temple service",
                List.of("INSIGHT", "RELIGION"),
                List.of("CALLIGRAPHERS_SUPPLIES"),
                List.of(),
                List.of("INTELLIGENCE", "WISDOM", "CHARISMA"),
                "Magic Initiate (Cleric)",
                "Gain two cleric cantrips and one 1st-level cleric spell.",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        assertThat(background.getId()).isEqualTo(id);
        assertThat(background.getName()).isEqualTo("Acolyte");
        assertThat(background.getSkillProficiencies()).containsExactly("INSIGHT", "RELIGION");
        assertThat(background.getToolProficiencies()).containsExactly("CALLIGRAPHERS_SUPPLIES");
        assertThat(background.getAllowedAbilityBonuses()).containsExactly("INTELLIGENCE", "WISDOM", "CHARISMA");
        assertThat(background.getOriginFeatName()).isEqualTo("Magic Initiate (Cleric)");
        assertThat(background.getOriginFeatDescription()).isNotBlank();
        assertThat(background.getRuleset()).isEqualTo(Ruleset.SRD_2024);
    }

    @Test
    @DisplayName("should construct valid Background for SRD 2014 without origin feat")
    void shouldConstructValidBackground2014() {
        Background background = new Background(
                UUID.randomUUID(),
                "Soldier",
                "Military life",
                List.of("ATHLETICS", "INTIMIDATION"),
                List.of("GAMING_SET", "VEHICLES_LAND"),
                List.of(),
                List.of(),
                null,
                null,
                Ruleset.SRD_2014,
                "SRD",
                null
        );

        assertThat(background.getAllowedAbilityBonuses()).isEmpty();
        assertThat(background.getOriginFeatName()).isNull();
        assertThat(background.getRuleset()).isEqualTo(Ruleset.SRD_2014);
    }

    @Test
    @DisplayName("should fail validation when name is blank")
    void shouldFailWhenNameIsBlank() {
        assertThatThrownBy(() -> new Background(
                UUID.randomUUID(),
                " ",
                "Desc",
                List.of("ATHLETICS"),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("name cannot be blank");
    }

    @Test
    @DisplayName("should fail validation when skillProficiencies is null")
    void shouldFailWhenSkillProficienciesIsNull() {
        assertThatThrownBy(() -> new Background(
                UUID.randomUUID(),
                "Soldier",
                "Desc",
                null,
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        )).isInstanceOf(CharacterValidationException.class)
          .hasMessageContaining("Skill proficiencies cannot be null");
    }

    @Test
    @DisplayName("should provide immutable collections")
    void shouldProvideImmutableCollections() {
        Background background = new Background(
                UUID.randomUUID(),
                "Sage",
                "Scholar",
                List.of("ARCANA", "HISTORY"),
                List.of(),
                List.of("TWO_OF_CHOICE"),
                List.of("CONSTITUTION", "INTELLIGENCE", "WISDOM"),
                "Magic Initiate (Wizard)",
                "Cantrips",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        assertThatThrownBy(() -> background.getSkillProficiencies().add("STEALTH"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> background.getAllowedAbilityBonuses().add("CHARISMA"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
