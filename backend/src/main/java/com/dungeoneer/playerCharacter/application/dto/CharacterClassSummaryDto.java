package com.dungeoneer.playerCharacter.application.dto;

import java.util.UUID;

public record CharacterClassSummaryDto(
        UUID classProgressionId,
        String className,
        String subclassName,
        int classLevel,
        String hitDie,
        int hitDiceSpent,
        int availableHitDice
) {
    public CharacterClassSummaryDto(String className, String subclassName, int classLevel) {
        this(null, className, subclassName, classLevel, null, 0, classLevel);
    }

    public CharacterClassSummaryDto(
            String className,
            String subclassName,
            int classLevel,
            String hitDie,
            int hitDiceSpent,
            int availableHitDice
    ) {
        this(null, className, subclassName, classLevel, hitDie, hitDiceSpent, availableHitDice);
    }
}
