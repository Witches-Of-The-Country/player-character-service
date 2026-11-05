package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import java.util.Map;

public record RerollAbilityScoresRequest(
        Map<String, Integer> abilityScores
) {
}
