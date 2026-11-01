package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

public record ArmorMechanics(
        ArmorCategory armorCategory,
        int baseAc,
        int magicBonus,
        int strengthRequirement,
        boolean stealthDisadvantage
) implements ItemMechanics {

    public ArmorMechanics {
        if (armorCategory == null) {
            throw new CharacterValidationException("ArmorCategory cannot be null.");
        }
        if (baseAc < 0) {
            throw new CharacterValidationException("Base AC cannot be negative. Provided: " + baseAc);
        }
        if (magicBonus < 0 || magicBonus > 3) {
            throw new CharacterValidationException("Magic bonus must be between 0 and 3. Provided: " + magicBonus);
        }
        if (strengthRequirement < 0) {
            throw new CharacterValidationException("Strength requirement cannot be negative.");
        }
    }
}
