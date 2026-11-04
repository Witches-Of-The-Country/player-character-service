package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.RestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record RestCommand(
        @NotNull(message = "Rest type is required")
        RestType restType,

        Integer hpHealed,

        @Valid
        List<HitDiceSpendRequest> hitDiceSpends
) {
    public RestCommand {
        if (hitDiceSpends == null) {
            hitDiceSpends = Collections.emptyList();
        }
    }

    public RestCommand(RestType restType) {
        this(restType, null, Collections.emptyList());
    }

    public RestCommand(RestType restType, Integer hpHealed) {
        this(restType, hpHealed, Collections.emptyList());
    }

    public record HitDiceSpendRequest(
            @NotNull(message = "Class progression id is required")
            UUID classProgressionId,

            @Min(value = 1, message = "Count must be at least 1")
            int count
    ) {
    }
}
