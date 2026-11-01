package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateHomebrewArchetypeRequest(
        @NotBlank(message = "Archetype name cannot be blank")
        @Size(max = 50, message = "Archetype name cannot exceed 50 characters")
        String name,

        @NotBlank(message = "Hit dice cannot be blank")
        @Size(max = 3, message = "Hit dice cannot exceed 3 characters")
        String hitDice,

        @NotBlank(message = "Primary ability cannot be blank")
        @Size(max = 15, message = "Primary ability cannot exceed 15 characters")
        String primaryAbility,

        @NotEmpty(message = "Saving throw proficiencies cannot be empty")
        List<String> savingThrowProficiencies,

        List<String> armorProficiencies,

        List<String> weaponProficiencies,

        @NotBlank(message = "Spellcasting type cannot be blank")
        @Size(max = 15, message = "Spellcasting type cannot exceed 15 characters")
        String spellcastingType,

        @Size(max = 15, message = "Spellcasting ability cannot exceed 15 characters")
        String spellcastingAbility
) {
}
