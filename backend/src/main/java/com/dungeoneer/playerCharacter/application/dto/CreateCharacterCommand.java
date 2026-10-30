package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record CreateCharacterCommand(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "Character name is required")
        @Size(max = 100, message = "Character name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Level is required")
        @Min(value = 1, message = "Level must be between 1 and 20")
        @Max(value = 20, message = "Level must be between 1 and 20")
        Integer level,

        @NotNull(message = "Ruleset is required")
        Ruleset ruleset,

        @NotNull(message = "Lineage ID is required")
        UUID lineageId,

        @NotNull(message = "Background ID is required")
        UUID backgroundId,

        @NotNull(message = "Archetype ID is required")
        UUID archetypeId,

        UUID subclassId,

        @NotNull(message = "Ability score method is required")
        AbilityScoreMethod abilityScoreMethod,

        @NotNull(message = "Strength score is required")
        @Min(1) @Max(30)
        Integer strength,

        @NotNull(message = "Dexterity score is required")
        @Min(1) @Max(30)
        Integer dexterity,

        @NotNull(message = "Constitution score is required")
        @Min(1) @Max(30)
        Integer constitution,

        @NotNull(message = "Intelligence score is required")
        @Min(1) @Max(30)
        Integer intelligence,

        @NotNull(message = "Wisdom score is required")
        @Min(1) @Max(30)
        Integer wisdom,

        @NotNull(message = "Charisma score is required")
        @Min(1) @Max(30)
        Integer charisma,

        Map<String, Integer> abilityScoreBonuses,

        UUID abilityRollId,

        HpCalculationMethod hpCalculationMethod,

        UUID hpRollId,

        String notes
) {
        // Backwards compatible constructor for legacy tests/mappers
        public CreateCharacterCommand(
                String name,
                Integer level,
                Integer hitPointsMax,
                Ruleset ruleset,
                AbilityScoreMethod abilityScoreMethod,
                UUID lineageId,
                UUID backgroundId,
                UUID userId,
                Integer strength,
                Integer dexterity,
                Integer constitution,
                Integer intelligence,
                Integer wisdom,
                Integer charisma
        ) {
                this(
                        userId != null ? userId : UUID.randomUUID(),
                        name,
                        level,
                        ruleset,
                        lineageId,
                        backgroundId,
                        UUID.randomUUID(),
                        null,
                        abilityScoreMethod,
                        strength,
                        dexterity,
                        constitution,
                        intelligence,
                        wisdom,
                        charisma,
                        null,
                        null,
                        HpCalculationMethod.MAX,
                        null,
                        ""
                );
        }
}
