package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.time.Instant;
import java.util.UUID;

public record CharacterSummaryResponse(
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
