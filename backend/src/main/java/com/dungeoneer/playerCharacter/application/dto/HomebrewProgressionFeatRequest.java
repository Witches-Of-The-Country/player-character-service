package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record HomebrewProgressionFeatRequest(
        @Min(value = 1, message = "Unlock level must be between 1 and 20")
        @Max(value = 20, message = "Unlock level must be between 1 and 20")
        int unlockLevel,

        UUID featId,

        @Size(max = 150, message = "Feature name cannot exceed 150 characters")
        String name,

        @Size(max = 4000, message = "Feature description cannot exceed 4000 characters")
        String description,

        MechanicalEffects mechanicalEffects
) {
}
