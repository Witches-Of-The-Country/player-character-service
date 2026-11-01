package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

public record DamageRoll(String diceFormula, DamageType damageType) {

    private static final Pattern DICE_PATTERN = Pattern.compile("^\\d{1,2}d(4|6|8|10|12|20|100)([+-]\\d{1,3})?$");

    public DamageRoll {
        if (diceFormula == null || diceFormula.trim().isEmpty()) {
            throw new CharacterValidationException("Dice formula cannot be blank.");
        }
        String normalizedFormula = diceFormula.trim();
        if (!DICE_PATTERN.matcher(normalizedFormula).matches()) {
            throw new CharacterValidationException("Invalid dice formula format: " + diceFormula);
        }
        if (damageType == null) {
            throw new CharacterValidationException("DamageType cannot be null.");
        }
        diceFormula = normalizedFormula;
    }
}
