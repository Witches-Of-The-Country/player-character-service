package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AbilityRollResponse(
        UUID rollId,
        UUID userId,
        List<Integer> rolledScores,
        String diceBreakdown,
        boolean isNew,
        Instant createdAt
) {
}
