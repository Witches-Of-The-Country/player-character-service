package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateCharacterRequest(
        @NotBlank(message = "Character name cannot be blank")
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

        @NotNull(message = "Ability scores are required")
        Map<String, Integer> abilityScores,

        Map<String, Integer> abilityScoreBonuses,

        UUID abilityRollId,

        HpCalculationMethod hpCalculationMethod,

        UUID hpRollId,

        String notes,

        List<@Valid ClassProgressionRequest> classProgressions,

        List<String> skillProficiencies
) {
        public CreateCharacterRequest(
                String name,
                Integer level,
                Ruleset ruleset,
                UUID lineageId,
                UUID backgroundId,
                UUID archetypeId,
                UUID subclassId,
                AbilityScoreMethod abilityScoreMethod,
                Map<String, Integer> abilityScores,
                Map<String, Integer> abilityScoreBonuses,
                UUID abilityRollId,
                HpCalculationMethod hpCalculationMethod,
                UUID hpRollId,
                String notes
        ) {
                this(
                        name,
                        level,
                        ruleset,
                        lineageId,
                        backgroundId,
                        archetypeId,
                        subclassId,
                        abilityScoreMethod,
                        abilityScores,
                        abilityScoreBonuses,
                        abilityRollId,
                        hpCalculationMethod,
                        hpRollId,
                        notes,
                        Collections.emptyList(),
                        Collections.emptyList()
                );
        }
}
