package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
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
@Table(name = "ability_rolls", schema = "player_character")
public class AbilityRollJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "rolled_scores", nullable = false, columnDefinition = "integer[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Integer[] rolledScores;

    @Column(name = "dice_breakdown", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String diceBreakdown;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AbilityRollJpaEntity() {
    }

    public AbilityRollJpaEntity(UUID id, UUID userId, Integer[] rolledScores, String diceBreakdown, boolean used, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.rolledScores = rolledScores;
        this.diceBreakdown = diceBreakdown != null ? diceBreakdown : "[]";
        this.used = used;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static AbilityRollJpaEntity fromDomain(AbilityRoll roll) {
        Integer[] scoresArray = roll.getRolledScores().toArray(new Integer[0]);
        return new AbilityRollJpaEntity(
                roll.getId(),
                roll.getUserId(),
                scoresArray,
                roll.getDiceBreakdownJson(),
                roll.isUsed(),
                roll.getCreatedAt()
        );
    }

    public AbilityRoll toDomain() {
        List<Integer> scores = (rolledScores != null)
                ? Arrays.asList(rolledScores)
                : new ArrayList<>();
        return new AbilityRoll(
                this.id,
                this.userId,
                scores,
                this.diceBreakdown,
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

    public Integer[] getRolledScores() {
        return rolledScores;
    }

    public void setRolledScores(Integer[] rolledScores) {
        this.rolledScores = rolledScores;
    }

    public String getDiceBreakdown() {
        return diceBreakdown;
    }

    public void setDiceBreakdown(String diceBreakdown) {
        this.diceBreakdown = diceBreakdown;
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
