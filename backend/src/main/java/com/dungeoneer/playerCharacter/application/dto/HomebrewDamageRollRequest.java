package com.dungeoneer.playerCharacter.application.dto;

public record HomebrewDamageRollRequest(
        String diceFormula,
        String damageType,
        Boolean conditional,
        String conditionNote,
        Integer diceCount,
        Integer diceType,
        Integer bonusModifier
) {
    public HomebrewDamageRollRequest(String diceFormula, String damageType, Boolean conditional, String conditionNote) {
        this(diceFormula, damageType, conditional, conditionNote, null, null, null);
    }

    public HomebrewDamageRollRequest(String diceFormula, String damageType) {
        this(diceFormula, damageType, false, null, null, null, null);
    }

    public String resolvedDiceFormula() {
        if (diceFormula != null && !diceFormula.isBlank()) {
            return diceFormula;
        }
        if (diceCount != null && diceType != null) {
            String mod = (bonusModifier != null && bonusModifier != 0)
                    ? (bonusModifier > 0 ? "+" + bonusModifier : String.valueOf(bonusModifier))
                    : "";
            return diceCount + "d" + diceType + mod;
        }
        return null;
    }
}
