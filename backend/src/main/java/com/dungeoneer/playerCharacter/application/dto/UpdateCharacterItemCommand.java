package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCharacterItemCommand(
        Boolean equipped,

        Boolean attuned,

        Integer quantity
) {
}
