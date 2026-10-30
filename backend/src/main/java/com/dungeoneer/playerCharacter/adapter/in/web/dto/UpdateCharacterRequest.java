package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCharacterRequest(
        @NotBlank(message = "Character name cannot be blank")
        @Size(max = 100, message = "Character name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Current hit points cannot be null")
        @Min(value = 0, message = "Current hit points cannot be negative")
        Integer hitPointsCurrent,

        @NotNull(message = "Temporary hit points cannot be null")
        @Min(value = 0, message = "Temporary hit points cannot be negative")
        Integer hitPointsTemp,

        @NotNull(message = "Notes cannot be null")
        String notes
) {
}
