package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.CharacterItem;
import com.dungeoneer.playerCharacter.domain.model.EquipmentSlot;
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
        EquipmentSlot equippedSlot,
        boolean attuned,
        Object mechanicalProperties,
        String description,
        String cost,
        String slug,
        String ruleset
) {
    public CharacterItemDto(
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
        this(id, characterId, itemId, name, itemCategory, itemType, rarity, requiresAttunement, weight, quantity, equipped, null, attuned, mechanicalProperties, null, null, null, null);
    }

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
                charItem.getEquippedSlot(),
                charItem.isAttuned(),
                item != null ? item.getMechanicalProperties() : null,
                item != null ? item.getDescription() : "",
                item != null ? item.getCost() : "",
                item != null ? item.getSlug() : "",
                item != null && item.getRuleset() != null ? item.getRuleset().name() : ""
        );
    }
}
