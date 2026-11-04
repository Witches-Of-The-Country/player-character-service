package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTest {

    @Test
    @DisplayName("Should successfully construct valid Item domain model")
    void shouldConstructValidItem() {
        UUID id = UUID.randomUUID();
        Item item = new Item(
                id,
                Ruleset.SRD_2024,
                "longsword-2024",
                "Longsword",
                "Espada Longa",
                "WEAPON",
                "Arma Marcial Corpo a Corpo",
                "NONE",
                false,
                "15 po",
                "1,5 kg",
                "Espada versátil.",
                "SRD",
                null
        );

        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getRuleset()).isEqualTo(Ruleset.SRD_2024);
        assertThat(item.getSlug()).isEqualTo("longsword-2024");
        assertThat(item.getNameEn()).isEqualTo("Longsword");
        assertThat(item.getName()).isEqualTo("Espada Longa");
        assertThat(item.getItemCategory()).isEqualTo("WEAPON");
        assertThat(item.isAttunement()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when required fields are missing")
    void shouldThrowExceptionWhenRequiredFieldsMissing() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> new Item(null, Ruleset.SRD_2024, "slug", "nameEn", "Nome", "WEAPON", "Type", "NONE", false, "", "", "", "SRD", null))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Item id cannot be null");

        assertThatThrownBy(() -> new Item(id, null, "slug", "nameEn", "Nome", "WEAPON", "Type", "NONE", false, "", "", "", "SRD", null))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Ruleset cannot be null");

        assertThatThrownBy(() -> new Item(id, Ruleset.SRD_2024, "", "nameEn", "Nome", "WEAPON", "Type", "NONE", false, "", "", "", "SRD", null))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Item slug cannot be blank");

        assertThatThrownBy(() -> new Item(id, Ruleset.SRD_2024, "slug", "nameEn", "", "WEAPON", "Type", "NONE", false, "", "", "", "SRD", null))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Item name cannot be blank");

        assertThatThrownBy(() -> new Item(id, Ruleset.SRD_2024, "slug", "nameEn", "Nome", "", "Type", "NONE", false, "", "", "", "SRD", null))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Item category cannot be blank");

        assertThatThrownBy(() -> new Item(id, Ruleset.SRD_2024, "slug", "nameEn", "Nome", "WEAPON", "", "NONE", false, "", "", "", "SRD", null))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Item type cannot be blank");
    }
}
