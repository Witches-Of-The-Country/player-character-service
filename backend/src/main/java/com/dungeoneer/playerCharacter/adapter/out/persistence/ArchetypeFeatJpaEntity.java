package com.dungeoneer.playerCharacter.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "archetype_feats", schema = "player_character")
public class ArchetypeFeatJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "archetype_id", nullable = false)
    private UUID archetypeId;

    @Column(name = "feat_id", nullable = false)
    private UUID featId;

    @Column(name = "unlock_level", nullable = false)
    private int unlockLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public ArchetypeFeatJpaEntity() {
    }

    public ArchetypeFeatJpaEntity(UUID id, UUID archetypeId, UUID featId, int unlockLevel) {
        this.id = id != null ? id : UUID.randomUUID();
        this.archetypeId = archetypeId;
        this.featId = featId;
        this.unlockLevel = unlockLevel;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchetypeId() {
        return archetypeId;
    }

    public UUID getFeatId() {
        return featId;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
