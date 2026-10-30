package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.HpRoll;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hp_rolls", schema = "player_character")
public class HpRollJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "archetype_id", nullable = false)
    private UUID archetypeId;

    @Column(name = "target_level", nullable = false)
    private int targetLevel;

    @Column(name = "rolled_hp_per_level", nullable = false, columnDefinition = "integer[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Integer[] rolledHpPerLevel;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public HpRollJpaEntity() {
    }

    public HpRollJpaEntity(UUID id, UUID userId, UUID archetypeId, int targetLevel, Integer[] rolledHpPerLevel, boolean used, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.archetypeId = archetypeId;
        this.targetLevel = targetLevel;
        this.rolledHpPerLevel = rolledHpPerLevel;
        this.used = used;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static HpRollJpaEntity fromDomain(HpRoll roll) {
        Integer[] hpArray = roll.getRolledHpPerLevel().toArray(new Integer[0]);
        return new HpRollJpaEntity(
                roll.getId(),
                roll.getUserId(),
                roll.getArchetypeId(),
                roll.getTargetLevel(),
                hpArray,
                roll.isUsed(),
                roll.getCreatedAt()
        );
    }

    public HpRoll toDomain() {
        List<Integer> hpList = (rolledHpPerLevel != null)
                ? Arrays.asList(rolledHpPerLevel)
                : new ArrayList<>();
        return new HpRoll(
                this.id,
                this.userId,
                this.archetypeId,
                this.targetLevel,
                hpList,
                this.used,
                this.createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(UUID archetypeId) {
        this.archetypeId = archetypeId;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(int targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Integer[] getRolledHpPerLevel() {
        return rolledHpPerLevel;
    }

    public void setRolledHpPerLevel(Integer[] rolledHpPerLevel) {
        this.rolledHpPerLevel = rolledHpPerLevel;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
