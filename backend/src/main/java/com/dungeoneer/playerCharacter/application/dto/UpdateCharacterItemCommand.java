package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.EquipmentSlot;

public record UpdateCharacterItemCommand(
        Boolean equipped,

        EquipmentSlot equippedSlot,

        Boolean attuned,

        Integer quantity
) {
    public UpdateCharacterItemCommand(Boolean equipped, Boolean attuned, Integer quantity) {
        this(equipped, null, attuned, quantity);
    }
}
