package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CharacterSummaryResult(
        UUID id,
        String name,
        int level,
        Ruleset ruleset,
        String className,
        String subclassName,
        String lineageName,
        String backgroundName,
        int hitPointsCurrent,
        int hitPointsMax,
        int armorClass,
        String avatarUrl,
        List<CharacterClassSummaryDto> classes,
        Instant updatedAt
) {
    public CharacterSummaryResult(
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
            String avatarUrl,
            Instant updatedAt
    ) {
        this(
                id,
                name,
                level,
                ruleset,
                className,
                null,
                lineageName,
                backgroundName,
                hitPointsCurrent,
                hitPointsMax,
                armorClass,
                avatarUrl,
                List.of(),
                updatedAt
        );
    }

    public CharacterSummaryResult(
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
        this(
                id,
                name,
                level,
                ruleset,
                className,
                null,
                lineageName,
                backgroundName,
                hitPointsCurrent,
                hitPointsMax,
                armorClass,
                null,
                List.of(),
                updatedAt
        );
    }
}
