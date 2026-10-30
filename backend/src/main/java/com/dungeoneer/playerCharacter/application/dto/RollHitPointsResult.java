package com.dungeoneer.playerCharacter.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RollHitPointsResult(
        UUID rollId,
        UUID userId,
        UUID archetypeId,
        int targetLevel,
        List<Integer> rolledHpPerLevel,
        boolean isNew,
        Instant createdAt
) {
}
