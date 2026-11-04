package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class CharacterItem {

    private final UUID id;
    private final UUID characterId;
    private final UUID itemId;
    private final int quantity;
    private final boolean equipped;
    private final boolean attuned;

    public CharacterItem(UUID id, UUID characterId, UUID itemId, int quantity, boolean equipped, boolean attuned) {
        if (itemId == null) {
            throw new CharacterValidationException("ItemId cannot be null.");
        }
        if (quantity < 1) {
            throw new CharacterValidationException("Quantity must be at least 1. Provided: " + quantity);
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.characterId = characterId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.equipped = equipped;
        this.attuned = attuned;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public boolean isAttuned() {
        return attuned;
    }

    public CharacterItem withEquipped(boolean equipped) {
        return new CharacterItem(this.id, this.characterId, this.itemId, this.quantity, equipped, this.attuned);
    }

    public CharacterItem withAttuned(boolean attuned) {
        return new CharacterItem(this.id, this.characterId, this.itemId, this.quantity, this.equipped, attuned);
    }

    public CharacterItem withQuantity(int quantity) {
        return new CharacterItem(this.id, this.characterId, this.itemId, quantity, this.equipped, this.attuned);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterItem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
