package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClassProgressionRequest(
        @NotNull(message = "Archetype ID is required")
        UUID archetypeId,

        UUID subclassId,

        @NotNull(message = "Class level is required")
        @Min(value = 1, message = "Class level must be at least 1")
        @Max(value = 20, message = "Class level cannot exceed 20")
        Integer classLevel
) {
}
