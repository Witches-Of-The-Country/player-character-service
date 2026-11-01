package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddExperienceCommand(
        @Min(value = 1, message = "Experience amount must be at least 1")
        int amount
) {
}
