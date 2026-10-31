package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class Item {

    private final UUID id;
    private final Ruleset ruleset;
    private final String slug;
    private final String nameEn;
    private final String name;
    private final String itemCategory;
    private final String itemType;
    private final String rarity;
    private final boolean attunement;
    private final String cost;
    private final String weight;
    private final String description;
    private final String source;
    private final UUID ownerId;

    public Item(
            UUID id,
            Ruleset ruleset,
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
            String source,
            UUID ownerId
    ) {
        if (id == null) {
            throw new CharacterValidationException("Item id cannot be null");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null");
        }
        if (slug == null || slug.trim().isEmpty()) {
            throw new CharacterValidationException("Item slug cannot be blank");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Item name cannot be blank");
        }
        if (itemCategory == null || itemCategory.trim().isEmpty()) {
            throw new CharacterValidationException("Item category cannot be blank");
        }
        if (itemType == null || itemType.trim().isEmpty()) {
            throw new CharacterValidationException("Item type cannot be blank");
        }

        this.id = id;
        this.ruleset = ruleset;
        this.slug = slug.trim();
        this.nameEn = nameEn != null ? nameEn.trim() : "";
        this.name = name.trim();
        this.itemCategory = itemCategory.trim().toUpperCase();
        this.itemType = itemType.trim();
        this.rarity = rarity != null ? rarity.trim().toUpperCase() : "NONE";
        this.attunement = attunement;
        this.cost = cost != null ? cost.trim() : "";
        this.weight = weight != null ? weight.trim() : "";
        this.description = description != null ? description.trim() : "";
        this.source = source != null ? source.trim() : "SRD";
        this.ownerId = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public String getSlug() {
        return slug;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getName() {
        return name;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemType() {
        return itemType;
    }

    public String getRarity() {
        return rarity;
    }

    public boolean isAttunement() {
        return attunement;
    }

    public String getCost() {
        return cost;
    }

    public String getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
