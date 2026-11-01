package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.Min;

public record UpdateCurrencyRequest(
        @Min(0) Integer cp,
        @Min(0) Integer sp,
        @Min(0) Integer ep,
        @Min(0) Integer gp,
        @Min(0) Integer pp,
        Boolean consolidate
) {}
