package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateRerollPermissionRequest(
        @NotNull(message = "allowAbilityReroll must not be null") Boolean allowAbilityReroll
) {
}
