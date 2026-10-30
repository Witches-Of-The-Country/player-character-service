package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

public enum HitDiceEnum {
    D6(6),
    D8(8),
    D10(10),
    D12(12);

    private final int dieValue;

    HitDiceEnum(int dieValue) {
        this.dieValue = dieValue;
    }

    public int getDieValue() {
        return dieValue;
    }

    public int getAverageHp() {
        return (dieValue / 2) + 1;
    }

    public static HitDiceEnum fromString(String hitDice) {
        if (hitDice == null || hitDice.trim().isEmpty()) {
            throw new CharacterValidationException("Hit dice cannot be null or empty");
        }
        String normalized = hitDice.trim().toUpperCase();
        if (!normalized.startsWith("D")) {
            normalized = "D" + normalized;
        }
        try {
            return HitDiceEnum.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new CharacterValidationException("Unsupported hit dice format: " + hitDice, e);
        }
    }
}
