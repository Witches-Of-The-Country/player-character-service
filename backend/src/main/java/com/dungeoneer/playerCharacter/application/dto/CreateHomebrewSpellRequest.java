package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateHomebrewSpellRequest(
        @NotBlank(message = "Spell name cannot be blank")
        @Size(max = 150, message = "Spell name cannot exceed 150 characters")
        String name,

        @Size(max = 150, message = "English name cannot exceed 150 characters")
        String nameEn,

        @NotNull(message = "Spell level is required")
        @Min(value = 0, message = "Spell level must be between 0 and 9")
        @Max(value = 9, message = "Spell level must be between 0 and 9")
        Integer level,

        @NotBlank(message = "School cannot be blank")
        @Size(max = 50, message = "School cannot exceed 50 characters")
        String school,

        @Size(max = 100, message = "Casting time cannot exceed 100 characters")
        String castingTime,

        @Size(max = 150, message = "Range/area cannot exceed 150 characters")
        String rangeArea,

        @Size(max = 255, message = "Components cannot exceed 255 characters")
        String components,

        @Size(max = 150, message = "Duration cannot exceed 150 characters")
        String duration,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 4000, message = "Description cannot exceed 4000 characters")
        String description
) {
}
