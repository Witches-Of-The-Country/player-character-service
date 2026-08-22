package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCharacterCommand(
        @NotBlank(message = "Character name is required")
        @Size(max = 100, message = "Character name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Level is required")
        @Min(value = 1, message = "Level must be at least 1")
        @Max(value = 20, message = "Level cannot exceed 20")
        Integer level,

        @NotNull(message = "Hit points max is required")
        @Min(value = 1, message = "Hit points max must be at least 1")
        Integer hitPointsMax,

        @NotNull(message = "Ruleset is required")
        Ruleset ruleset,

        @NotNull(message = "Ability score method is required")
        AbilityScoreMethod abilityScoreMethod,

        @NotNull(message = "Lineage ID is required")
        UUID lineageId,

        @NotNull(message = "Background ID is required")
        UUID backgroundId,

        UUID userId,

        @NotNull(message = "Strength score is required")
        @Min(value = 1, message = "Strength must be at least 1")
        @Max(value = 30, message = "Strength cannot exceed 30")
        Integer strength,

        @NotNull(message = "Dexterity score is required")
        @Min(value = 1, message = "Dexterity must be at least 1")
        @Max(value = 30, message = "Dexterity cannot exceed 30")
        Integer dexterity,

        @NotNull(message = "Constitution score is required")
        @Min(value = 1, message = "Constitution must be at least 1")
        @Max(value = 30, message = "Constitution cannot exceed 30")
        Integer constitution,

        @NotNull(message = "Intelligence score is required")
        @Min(value = 1, message = "Intelligence must be at least 1")
        @Max(value = 30, message = "Intelligence cannot exceed 30")
        Integer intelligence,

        @NotNull(message = "Wisdom score is required")
        @Min(value = 1, message = "Wisdom must be at least 1")
        @Max(value = 30, message = "Wisdom cannot exceed 30")
        Integer wisdom,

        @NotNull(message = "Charisma score is required")
        @Min(value = 1, message = "Charisma must be at least 1")
        @Max(value = 30, message = "Charisma cannot exceed 30")
        Integer charisma
) {
}
