package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record CreateHomebrewSubclassRequest(
        @NotNull(message = "Archetype ID cannot be null")
        UUID archetypeId,

        @NotBlank(message = "Subclass name cannot be blank")
        @Size(max = 120, message = "Subclass name cannot exceed 120 characters")
        String name,

        @Size(max = 120, message = "English name cannot exceed 120 characters")
        String nameEn,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 4000, message = "Description cannot exceed 4000 characters")
        String description,

        List<@Valid HomebrewProgressionFeatRequest> progressionFeats
) {
    public CreateHomebrewSubclassRequest(
            UUID archetypeId,
            String name,
            String nameEn,
            String description
    ) {
        this(archetypeId, name, nameEn, description, Collections.emptyList());
    }
}
