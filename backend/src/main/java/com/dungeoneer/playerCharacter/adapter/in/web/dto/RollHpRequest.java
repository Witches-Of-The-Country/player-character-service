package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RollHpRequest(
        @NotNull(message = "Archetype ID is required")
        UUID archetypeId,

        @NotNull(message = "Level is required")
        @Min(value = 1, message = "Level must be between 1 and 20")
        @Max(value = 20, message = "Level cannot exceed 20")
        Integer level,

        Boolean isSecondaryClass
) {
    public RollHpRequest(UUID archetypeId, Integer level) {
        this(archetypeId, level, false);
    }
}
