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

        String notes
) {
}
