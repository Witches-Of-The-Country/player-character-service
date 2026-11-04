package com.dungeoneer.playerCharacter.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "lineage_feats", schema = "player_character")
@IdClass(LineageFeatId.class)
public class LineageFeatJpaEntity {

    @Id
    @Column(name = "lineage_id", nullable = false)
    private UUID lineageId;

    @Id
    @Column(name = "feat_id", nullable = false)
    private UUID featId;

    @Column(name = "unlock_level", nullable = false)
    private int unlockLevel = 1;

    public LineageFeatJpaEntity() {
    }

    public LineageFeatJpaEntity(UUID lineageId, UUID featId, int unlockLevel) {
        this.lineageId = lineageId;
        this.featId = featId;
        this.unlockLevel = unlockLevel;
    }

    public UUID getLineageId() {
        return lineageId;
    }

    public void setLineageId(UUID lineageId) {
        this.lineageId = lineageId;
    }

    public UUID getFeatId() {
        return featId;
    }

    public void setFeatId(UUID featId) {
        this.featId = featId;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public void setUnlockLevel(int unlockLevel) {
        this.unlockLevel = unlockLevel;
    }
}
