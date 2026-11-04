package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Collections;
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
        String spellcastingAbility,

        @Min(value = 1, message = "Subclass level must be between 1 and 20")
        @Max(value = 20, message = "Subclass level must be between 1 and 20")
        Integer subclassLevel,

        List<@Valid HomebrewProgressionFeatRequest> progressionFeats,

        String spellPreparationType
) {
    public CreateHomebrewArchetypeRequest(
            String name,
            String hitDice,
            String primaryAbility,
            List<String> savingThrowProficiencies,
            List<String> armorProficiencies,
            List<String> weaponProficiencies,
            String spellcastingType,
            String spellcastingAbility,
            Integer subclassLevel,
            List<HomebrewProgressionFeatRequest> progressionFeats
    ) {
        this(
                name,
                hitDice,
                primaryAbility,
                savingThrowProficiencies,
                armorProficiencies,
                weaponProficiencies,
                spellcastingType,
                spellcastingAbility,
                subclassLevel,
                progressionFeats,
                null
        );
    }

    public CreateHomebrewArchetypeRequest(
            String name,
            String hitDice,
            String primaryAbility,
            List<String> savingThrowProficiencies,
            List<String> armorProficiencies,
            List<String> weaponProficiencies,
            String spellcastingType,
            String spellcastingAbility
    ) {
        this(
                name,
                hitDice,
                primaryAbility,
                savingThrowProficiencies,
                armorProficiencies,
                weaponProficiencies,
                spellcastingType,
                spellcastingAbility,
                3,
                Collections.emptyList(),
                "NONE"
        );
    }
}
