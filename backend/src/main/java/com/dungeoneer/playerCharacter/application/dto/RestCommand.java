package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.RestType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RestCommand(
        @NotNull(message = "Rest type is required")
        RestType restType,

        Integer hpHealed
) {
}
