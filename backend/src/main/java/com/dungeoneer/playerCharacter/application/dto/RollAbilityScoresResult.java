package com.dungeoneer.playerCharacter.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RollAbilityScoresResult(
        UUID rollId,
        UUID userId,
        List<Integer> rolledScores,
        String diceBreakdownJson,
        boolean isNew,
        Instant createdAt
) {
}
