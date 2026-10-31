package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Item;

import java.util.UUID;

public record ItemDto(
        UUID id,
        String ruleset,
        String slug,
        String nameEn,
        String name,
        String itemCategory,
        String itemType,
        String rarity,
        boolean attunement,
        String cost,
        String weight,
        String description,
        String source
) {
    public static ItemDto fromDomain(Item item) {
        return new ItemDto(
                item.getId(),
                item.getRuleset() != null ? item.getRuleset().name() : null,
                item.getSlug(),
                item.getNameEn(),
                item.getName(),
                item.getItemCategory(),
                item.getItemType(),
                item.getRarity(),
                item.isAttunement(),
                item.getCost(),
                item.getWeight(),
                item.getDescription(),
                item.getSource()
        );
    }
}
