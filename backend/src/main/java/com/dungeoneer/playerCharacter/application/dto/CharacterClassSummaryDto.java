package com.dungeoneer.playerCharacter.application.dto;

public record CharacterClassSummaryDto(
        String className,
        String subclassName,
        int classLevel
) {}
