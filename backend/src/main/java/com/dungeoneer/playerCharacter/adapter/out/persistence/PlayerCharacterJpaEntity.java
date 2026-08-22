package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "characters")
public class PlayerCharacterJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "hit_points_current", nullable = false)
    private int hitPointsCurrent;

    @Column(name = "hit_points_max", nullable = false)
    private int hitPointsMax;

    @Column(name = "hit_points_temp", nullable = false)
    private int hitPointsTemp;

    @Column(name = "armor_class", nullable = false)
    private int armorClass;

    @Column(name = "initiative_bonus", nullable = false)
    private int initiativeBonus;

    @Column(name = "speed", nullable = false)
    private int speed;

    @Column(name = "proficiency_bonus", nullable = false)
    private int proficiencyBonus;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 10, nullable = false)
    private Ruleset ruleset;

    @Enumerated(EnumType.STRING)
    @Column(name = "ability_score_method", length = 20, nullable = false)
    private AbilityScoreMethod abilityScoreMethod;

    @Column(name = "lineage_id", nullable = false)
    private UUID lineageId;

    @Column(name = "background_id", nullable = false)
    private UUID backgroundId;

    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private AbilityScoresJpaEntity abilityScores;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public PlayerCharacterJpaEntity() {
    }

    public PlayerCharacterJpaEntity(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScoresJpaEntity abilityScores,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.hitPointsCurrent = hitPointsCurrent;
        this.hitPointsMax = hitPointsMax;
        this.hitPointsTemp = hitPointsTemp;
        this.armorClass = armorClass;
        this.initiativeBonus = initiativeBonus;
        this.speed = speed;
        this.proficiencyBonus = proficiencyBonus;
        this.ruleset = ruleset;
        this.abilityScoreMethod = abilityScoreMethod;
        this.lineageId = lineageId;
        this.backgroundId = backgroundId;
        this.userId = userId;
        this.abilityScores = abilityScores;
        if (this.abilityScores != null) {
            this.abilityScores.setCharacter(this);
        }
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PlayerCharacterJpaEntity fromDomain(PlayerCharacter domain) {
        PlayerCharacterJpaEntity entity = new PlayerCharacterJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getLevel(),
                domain.getHitPointsCurrent(),
                domain.getHitPointsMax(),
                domain.getHitPointsTemp(),
                domain.getArmorClass(),
                domain.getInitiativeBonus(),
                domain.getSpeed(),
                domain.getProficiencyBonus(),
                domain.getRuleset(),
                domain.getAbilityScoreMethod(),
                domain.getLineageId(),
                domain.getBackgroundId(),
                domain.getUserId(),
                null,
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );

        if (domain.getAbilityScores() != null) {
            AbilityScoresJpaEntity scoresEntity = AbilityScoresJpaEntity.fromDomain(domain.getAbilityScores(), entity);
            entity.setAbilityScores(scoresEntity);
        }

        return entity;
    }

    public PlayerCharacter toDomain() {
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                this.hitPointsCurrent,
                this.hitPointsMax,
                this.hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores != null ? this.abilityScores.toDomain() : null,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHitPointsCurrent() {
        return hitPointsCurrent;
    }

    public void setHitPointsCurrent(int hitPointsCurrent) {
        this.hitPointsCurrent = hitPointsCurrent;
    }

    public int getHitPointsMax() {
        return hitPointsMax;
    }

    public void setHitPointsMax(int hitPointsMax) {
        this.hitPointsMax = hitPointsMax;
    }

    public int getHitPointsTemp() {
        return hitPointsTemp;
    }

    public void setHitPointsTemp(int hitPointsTemp) {
        this.hitPointsTemp = hitPointsTemp;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(int armorClass) {
        this.armorClass = armorClass;
    }

    public int getInitiativeBonus() {
        return initiativeBonus;
    }

    public void setInitiativeBonus(int initiativeBonus) {
        this.initiativeBonus = initiativeBonus;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getProficiencyBonus() {
        return proficiencyBonus;
    }

    public void setProficiencyBonus(int proficiencyBonus) {
        this.proficiencyBonus = proficiencyBonus;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    public AbilityScoreMethod getAbilityScoreMethod() {
        return abilityScoreMethod;
    }

    public void setAbilityScoreMethod(AbilityScoreMethod abilityScoreMethod) {
        this.abilityScoreMethod = abilityScoreMethod;
    }

    public UUID getLineageId() {
        return lineageId;
    }

    public void setLineageId(UUID lineageId) {
        this.lineageId = lineageId;
    }

    public UUID getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(UUID backgroundId) {
        this.backgroundId = backgroundId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public AbilityScoresJpaEntity getAbilityScores() {
        return abilityScores;
    }

    public void setAbilityScores(AbilityScoresJpaEntity abilityScores) {
        this.abilityScores = abilityScores;
        if (abilityScores != null) {
            abilityScores.setCharacter(this);
        }
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
