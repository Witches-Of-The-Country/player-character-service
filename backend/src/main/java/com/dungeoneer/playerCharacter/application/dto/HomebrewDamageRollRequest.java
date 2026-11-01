package com.dungeoneer.playerCharacter.application.dto;

public record HomebrewDamageRollRequest(
        String diceFormula,
        String damageType,
        Boolean conditional,
        String conditionNote
) {
    public HomebrewDamageRollRequest(String diceFormula, String damageType) {
        this(diceFormula, damageType, false, null);
    }
}
