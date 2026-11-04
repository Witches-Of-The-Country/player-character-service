package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record CreateHomebrewLineageRequest(
        @NotBlank(message = "Lineage name cannot be blank")
        @Size(max = 50, message = "Lineage name cannot exceed 50 characters")
        String name,

        @Size(max = 4000, message = "Description cannot exceed 4000 characters")
        String description,

        @Min(value = 5, message = "Speed must be at least 5")
        int speed,

        @NotBlank(message = "Size cannot be blank")
        @Size(max = 10, message = "Size cannot exceed 10 characters")
        String size,

        Map<String, Integer> abilityBonuses,

        List<String> traitsSummary,

        List<@Valid HomebrewProgressionFeatRequest> inlineFeats
) {
    public CreateHomebrewLineageRequest(
            String name,
            String description,
            int speed,
            String size,
            Map<String, Integer> abilityBonuses,
            List<String> traitsSummary
    ) {
        this(name, description, speed, size, abilityBonuses, traitsSummary, Collections.emptyList());
    }
}
