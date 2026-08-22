package com.dungeoneer.playerCharacter.adapter.in.web.dto;

public record CreatePlayerCharacterRequest(
    String name,
    Integer level,
    String ruleset,
    java.util.UUID userId,
    java.util.UUID lineageId,
    java.util.UUID backgroundId,
    java.util.Map<String, Integer> abilityScores
) {}
