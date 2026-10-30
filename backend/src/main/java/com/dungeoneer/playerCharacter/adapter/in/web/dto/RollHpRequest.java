package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RollHpRequest(
        @NotNull(message = "Archetype ID is required")
        UUID archetypeId,

        @NotNull(message = "Level is required")
        @Min(value = 2, message = "Rolled hit points start from level 2")
        @Max(value = 20, message = "Level cannot exceed 20")
        Integer level
) {
}
