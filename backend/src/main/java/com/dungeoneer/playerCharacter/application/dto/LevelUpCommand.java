package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record LevelUpCommand(
        @NotNull(message = "Target Archetype ID is required")
        UUID archetypeId,

        UUID subclassId,

        @NotNull(message = "HP calculation method is required")
        HpCalculationMethod hpCalculationMethod,

        UUID hpRollId,

        Integer hpIncrease,

        Boolean milestone,

        Map<String, Integer> abilityScoreIncreases,

        UUID selectedFeatId
) {
        public LevelUpCommand {
                if (hpIncrease == null) {
                        hpIncrease = 0;
                }
                if (milestone == null) {
                        milestone = false;
                }
        }
}
