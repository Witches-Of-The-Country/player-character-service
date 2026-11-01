package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.CharacterItem;
import com.dungeoneer.playerCharacter.domain.model.Item;

import java.util.UUID;

public record CharacterItemDto(
        UUID id,
        UUID characterId,
        UUID itemId,
        String name,
        String itemCategory,
        String itemType,
        String rarity,
        boolean requiresAttunement,
        String weight,
        int quantity,
        boolean equipped,
        boolean attuned,
        Object mechanicalProperties
) {
    public static CharacterItemDto fromDomain(CharacterItem charItem, Item item) {
        return new CharacterItemDto(
                charItem.getId(),
                charItem.getCharacterId(),
                charItem.getItemId(),
                item != null ? item.getName() : "Item",
                item != null ? item.getItemCategory() : "EQUIPMENT",
                item != null ? item.getItemType() : "",
                item != null ? item.getRarity() : "NONE",
                item != null && item.isAttunement(),
                item != null ? item.getWeight() : "0 kg",
                charItem.getQuantity(),
                charItem.isEquipped(),
                charItem.isAttuned(),
                item != null ? item.getMechanicalProperties() : null
        );
    }
}
