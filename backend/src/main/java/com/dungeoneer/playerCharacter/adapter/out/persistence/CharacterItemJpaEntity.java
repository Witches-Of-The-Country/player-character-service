package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.CharacterItem;
import com.dungeoneer.playerCharacter.domain.model.EquipmentSlot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "character_items", schema = "player_character")
public class CharacterItemJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private PlayerCharacterJpaEntity character;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @Column(name = "is_equipped", nullable = false)
    private boolean isEquipped = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipped_slot", length = 30)
    private EquipmentSlot equippedSlot;

    @Column(name = "is_attuned", nullable = false)
    private boolean isAttuned = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CharacterItemJpaEntity() {
    }

    public CharacterItemJpaEntity(
            UUID id,
            PlayerCharacterJpaEntity character,
            UUID itemId,
            int quantity,
            boolean isEquipped,
            EquipmentSlot equippedSlot,
            boolean isAttuned
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.character = character;
        this.itemId = itemId;
        this.quantity = quantity;
        this.isEquipped = isEquipped;
        this.equippedSlot = isEquipped ? equippedSlot : null;
        this.isAttuned = isAttuned;
    }

    public CharacterItemJpaEntity(
            UUID id,
            PlayerCharacterJpaEntity character,
            UUID itemId,
            int quantity,
            boolean isEquipped,
            boolean isAttuned
    ) {
        this(id, character, itemId, quantity, isEquipped, null, isAttuned);
    }

    public static CharacterItemJpaEntity fromDomain(CharacterItem item, PlayerCharacterJpaEntity character) {
        return new CharacterItemJpaEntity(
                item.getId(),
                character,
                item.getItemId(),
                item.getQuantity(),
                item.isEquipped(),
                item.getEquippedSlot(),
                item.isAttuned()
        );
    }

    public CharacterItem toDomain() {
        return new CharacterItem(
                this.id,
                this.character != null ? this.character.getId() : null,
                this.itemId,
                this.quantity,
                this.isEquipped,
                this.isAttuned,
                this.equippedSlot
        );
    }

    public UUID getId() {
        return id;
    }

    public PlayerCharacterJpaEntity getCharacter() {
        return character;
    }

    public void setCharacter(PlayerCharacterJpaEntity character) {
        this.character = character;
    }

    public UUID getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
        if (!equipped) {
            this.equippedSlot = null;
        }
    }

    public EquipmentSlot getEquippedSlot() {
        return equippedSlot;
    }

    public void setEquippedSlot(EquipmentSlot equippedSlot) {
        this.equippedSlot = equippedSlot;
    }

    public boolean isAttuned() {
        return isAttuned;
    }

    public void setAttuned(boolean attuned) {
        isAttuned = attuned;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
