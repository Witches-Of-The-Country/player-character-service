package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
        String description
) {
    public boolean isAttunement() {
        return Boolean.TRUE.equals(attunement);
    }
}
