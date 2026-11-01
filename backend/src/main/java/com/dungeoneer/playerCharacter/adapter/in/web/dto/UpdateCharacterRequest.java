package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateCharacterRequest(
        @Size(max = 100, message = "Character name cannot exceed 100 characters")
        String name,

        @Min(value = 0, message = "Current hit points cannot be negative")
        Integer hitPointsCurrent,

        @Min(value = 0, message = "Temporary hit points cannot be negative")
        Integer hitPointsTemp,

        String notes,

        @Size(max = 700000, message = "Avatar URL cannot exceed 700,000 characters")
        String avatarUrl
) {
    public UpdateCharacterRequest(String name, Integer hitPointsCurrent, Integer hitPointsTemp, String notes) {
        this(name, hitPointsCurrent, hitPointsTemp, notes, null);
    }
}
