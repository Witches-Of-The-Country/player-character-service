package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record HpRollResponse(
        UUID rollId,
        UUID userId,
        UUID archetypeId,
        int targetLevel,
        List<Integer> rolledHpPerLevel,
        boolean isNew,
        Instant createdAt
) {
}
