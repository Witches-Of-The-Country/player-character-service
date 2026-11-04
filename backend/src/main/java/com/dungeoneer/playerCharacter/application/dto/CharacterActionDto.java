package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.DamageRoll;
import com.dungeoneer.playerCharacter.domain.model.DamageType;

import java.util.List;
import java.util.UUID;

public record CharacterActionDto(
        UUID id,
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
    public CharacterActionDto(
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
        this(null, name, actionType, category, attackBonus, damageRolls, versatileDice, description, currentCharges, maxCharges);
    }
}
