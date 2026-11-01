package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.DamageRoll;
import com.dungeoneer.playerCharacter.domain.model.DamageType;

import java.util.List;

public record CharacterActionDto(
        String name,
        String actionType,
        String category,
        Integer attackBonus,
        List<DamageRoll> damageRolls,
        String versatileDice,
        String description,
        Integer currentCharges,
        Integer maxCharges
) {
}
