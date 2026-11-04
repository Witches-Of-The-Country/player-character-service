package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record CreatePlayerCharacterRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,
        
        @NotNull(message = "Level is required")
        @Min(value = 1, message = "Level must be at least 1")
        @Max(value = 20, message = "Level must be at most 20")
        Integer level,
        
        @NotBlank(message = "Ruleset cannot be blank")
        String ruleset,
        
        UUID userId,
        
        @NotNull(message = "Lineage ID is required")
        UUID lineageId,
        
        @NotNull(message = "Background ID is required")
        UUID backgroundId,
        
        @NotEmpty(message = "Ability scores are required")
        Map<String, Integer> abilityScores) {
}
