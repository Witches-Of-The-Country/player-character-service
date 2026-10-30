package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RollHitPointsCommand(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Archetype ID is required")
        UUID archetypeId,

        @NotNull(message = "Target level is required")
        @Min(value = 2, message = "Rolled hit points start from level 2")
        @Max(value = 20, message = "Target level cannot exceed 20")
        Integer targetLevel
) {
}
