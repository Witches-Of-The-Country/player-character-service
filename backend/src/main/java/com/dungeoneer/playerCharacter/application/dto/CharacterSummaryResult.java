package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.time.Instant;
import java.util.UUID;

public record CharacterSummaryResult(
        UUID id,
        String name,
        int level,
        Ruleset ruleset,
        String className,
        String lineageName,
        String backgroundName,
        int hitPointsCurrent,
        int hitPointsMax,
        int armorClass,
        Instant updatedAt
) {
}
