package com.dungeoneer.playerCharacter.application.dto;

public record CharacterResponseDto(
    java.util.UUID id,
    String name,
    Integer level,
    String ruleset,
    Integer hitPointsMax,
    Integer hitPointsCurrent,
    Integer armorClass,
    Integer speed,
    Integer proficiencyBonus,
    Integer initiativeBonus
) {}
