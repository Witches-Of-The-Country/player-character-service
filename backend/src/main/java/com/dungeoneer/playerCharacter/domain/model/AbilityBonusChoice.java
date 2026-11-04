package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Collections;
import java.util.List;

public record AbilityBonusChoice(
        int chooseCount,
        List<String> allowedAbilities,
        int bonusAmount
) {
    public AbilityBonusChoice {
        if (chooseCount < 0) {
            throw new CharacterValidationException("chooseCount cannot be negative");
        }
        if (bonusAmount < 0) {
            throw new CharacterValidationException("bonusAmount cannot be negative");
        }
        allowedAbilities = allowedAbilities != null ? Collections.unmodifiableList(allowedAbilities) : Collections.emptyList();
    }
}
