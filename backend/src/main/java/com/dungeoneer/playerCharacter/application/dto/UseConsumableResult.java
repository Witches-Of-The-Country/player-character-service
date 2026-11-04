package com.dungeoneer.playerCharacter.application.dto;

import java.util.UUID;

public record UseConsumableResult(
        UUID characterId,
        UUID characterItemId,
        String itemName,
        String effectType,
        String diceFormula,
        int rollTotal,
        int currentHp,
        int maxHp,
        int tempHp,
        int remainingQuantity
) {
}
