package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCharacterItemCommand(
        @NotNull(message = "Item ID is required")
        UUID itemId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {
}
