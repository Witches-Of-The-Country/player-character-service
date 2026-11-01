package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHomebrewFeatRequest(
        @NotBlank(message = "Feat name cannot be blank")
        @Size(max = 150, message = "Feat name cannot exceed 150 characters")
        String name,

        @Size(max = 150, message = "English name cannot exceed 150 characters")
        String nameEn,

        @Size(max = 255, message = "Prerequisites cannot exceed 255 characters")
        String prerequisites,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 4000, message = "Description cannot exceed 4000 characters")
        String description
) {
}
