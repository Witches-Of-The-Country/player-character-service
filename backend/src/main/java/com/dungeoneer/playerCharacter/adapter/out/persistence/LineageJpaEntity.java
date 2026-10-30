package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "lineages", schema = "player_character")
public class LineageJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "speed", nullable = false)
    private int speed;

    @Column(name = "size", length = 10, nullable = false)
    private String size;

    @Column(name = "ability_bonuses", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Integer> abilityBonuses;

    @Column(name = "traits_summary", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> traitsSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 10, nullable = false)
    private Ruleset ruleset;

    @Column(name = "source", length = 10, nullable = false)
    private String source;

    @Column(name = "owner_id")
    private UUID ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public LineageJpaEntity() {
    }

    public Lineage toDomain() {
        return new Lineage(
                this.id,
                this.name,
                this.description,
                this.speed,
                this.size,
                this.abilityBonuses,
                this.traitsSummary,
                this.ruleset,
                this.source,
                this.ownerId
        );
    }

    public static LineageJpaEntity fromDomain(Lineage domain) {
        LineageJpaEntity entity = new LineageJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setSpeed(domain.getSpeed());
        entity.setSize(domain.getSize());
        entity.setAbilityBonuses(domain.getAbilityBonuses());
        entity.setTraitsSummary(domain.getTraitsSummary());
        entity.setRuleset(domain.getRuleset());
        entity.setSource(domain.getSource());
        entity.setOwnerId(domain.getOwnerId());
        return entity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Map<String, Integer> getAbilityBonuses() {
        return abilityBonuses;
    }

    public void setAbilityBonuses(Map<String, Integer> abilityBonuses) {
        this.abilityBonuses = abilityBonuses;
    }

    public List<String> getTraitsSummary() {
        return traitsSummary;
    }

    public void setTraitsSummary(List<String> traitsSummary) {
        this.traitsSummary = traitsSummary;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
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
