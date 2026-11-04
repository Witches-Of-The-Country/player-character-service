package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateCharacterCommand(
        @NotNull(message = "Character ID is required")
        UUID characterId,

        @NotNull(message = "User ID is required")
        UUID userId,

        @Size(max = 100, message = "Character name cannot exceed 100 characters")
        String name,

        @Min(value = 0, message = "Current hit points cannot be negative")
        Integer hitPointsCurrent,

        @Min(value = 0, message = "Temporary hit points cannot be negative")
        Integer hitPointsTemp,

        String notes,

        String avatarUrl
) {
    public UpdateCharacterCommand(
            UUID characterId,
            UUID userId,
            String name,
            Integer hitPointsCurrent,
            Integer hitPointsTemp,
            String notes
    ) {
        this(characterId, userId, name, hitPointsCurrent, hitPointsTemp, notes, null);
    }
}
