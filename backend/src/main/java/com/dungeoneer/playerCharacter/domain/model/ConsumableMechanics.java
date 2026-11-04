package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.regex.Pattern;

public record ConsumableMechanics(
        ConsumableEffectType effectType,
        String diceFormula,
        String effectDescription
) implements ItemMechanics {

    private static final Pattern DICE_PATTERN = Pattern.compile("^\\d{1,2}d(4|6|8|10|12|20|100)([+-]\\d{1,3})?$");

    public ConsumableMechanics {
        if (effectType == null) {
            throw new CharacterValidationException("ConsumableEffectType cannot be null.");
        }
        if (diceFormula != null && !diceFormula.trim().isEmpty()) {
            String normalizedFormula = diceFormula.trim();
            if (!DICE_PATTERN.matcher(normalizedFormula).matches()) {
                throw new CharacterValidationException("Invalid consumable dice formula: " + diceFormula);
            }
            diceFormula = normalizedFormula;
        } else {
            diceFormula = null;
        }
        effectDescription = effectDescription != null ? effectDescription.trim() : "";
    }
}
