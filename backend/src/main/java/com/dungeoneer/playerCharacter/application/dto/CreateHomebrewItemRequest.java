package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.DamageRoll;
import com.dungeoneer.playerCharacter.domain.model.ItemMechanics;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateHomebrewItemRequest(
        @NotBlank(message = "Item name cannot be blank")
        @Size(max = 150, message = "Item name cannot exceed 150 characters")
        String name,

        @Size(max = 150, message = "English name cannot exceed 150 characters")
        String nameEn,

        @NotBlank(message = "Item category cannot be blank")
        @Size(max = 30, message = "Item category cannot exceed 30 characters")
        String itemCategory,

        @NotBlank(message = "Item type cannot be blank")
        @Size(max = 50, message = "Item type cannot exceed 50 characters")
        String itemType,

        @Size(max = 30, message = "Rarity cannot exceed 30 characters")
        String rarity,

        Boolean attunement,

        @Size(max = 50, message = "Cost cannot exceed 50 characters")
        String cost,

        @Size(max = 50, message = "Weight cannot exceed 50 characters")
        String weight,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 4000, message = "Description cannot exceed 4000 characters")
        String description,

        ItemMechanics mechanicalProperties
) {
    public CreateHomebrewItemRequest(
            String name,
            String nameEn,
            String itemCategory,
            String itemType,
            String rarity,
            Boolean attunement,
            String cost,
            String weight,
            String description
    ) {
        this(name, nameEn, itemCategory, itemType, rarity, attunement, cost, weight, description, new com.dungeoneer.playerCharacter.domain.model.EmptyItemMechanics());
    }

    public boolean isAttunement() {
        return Boolean.TRUE.equals(attunement);
    }
}
