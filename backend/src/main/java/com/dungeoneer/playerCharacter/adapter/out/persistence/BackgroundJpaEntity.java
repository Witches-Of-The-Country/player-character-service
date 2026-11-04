package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Background;
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
import java.util.UUID;

@Entity
@Table(name = "backgrounds", schema = "player_character")
public class BackgroundJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "skill_proficiencies", columnDefinition = "varchar[]", nullable = false)
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> skillProficiencies;

    @Column(name = "tool_proficiencies", columnDefinition = "varchar[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> toolProficiencies;

    @Column(name = "languages", columnDefinition = "varchar[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> languages;

    @Column(name = "allowed_ability_bonuses", columnDefinition = "varchar[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> allowedAbilityBonuses;

    @Column(name = "origin_feat_name", length = 100)
    private String originFeatName;

    @Column(name = "origin_feat_description", columnDefinition = "text")
    private String originFeatDescription;

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

    public BackgroundJpaEntity() {
    }

    public Background toDomain() {
        return new Background(
                this.id,
                this.name,
                this.description,
                this.skillProficiencies,
                this.toolProficiencies,
                this.languages,
                this.allowedAbilityBonuses,
                this.originFeatName,
                this.originFeatDescription,
                this.ruleset,
                this.source,
                this.ownerId
        );
    }

    public static BackgroundJpaEntity fromDomain(Background domain) {
        BackgroundJpaEntity entity = new BackgroundJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setSkillProficiencies(domain.getSkillProficiencies());
        entity.setToolProficiencies(domain.getToolProficiencies());
        entity.setLanguages(domain.getLanguages());
        entity.setAllowedAbilityBonuses(domain.getAllowedAbilityBonuses());
        entity.setOriginFeatName(domain.getOriginFeatName());
        entity.setOriginFeatDescription(domain.getOriginFeatDescription());
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

    public List<String> getSkillProficiencies() {
        return skillProficiencies;
    }

    public void setSkillProficiencies(List<String> skillProficiencies) {
        this.skillProficiencies = skillProficiencies;
    }

    public List<String> getToolProficiencies() {
        return toolProficiencies;
    }

    public void setToolProficiencies(List<String> toolProficiencies) {
        this.toolProficiencies = toolProficiencies;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getAllowedAbilityBonuses() {
        return allowedAbilityBonuses;
    }

    public void setAllowedAbilityBonuses(List<String> allowedAbilityBonuses) {
        this.allowedAbilityBonuses = allowedAbilityBonuses;
    }

    public String getOriginFeatName() {
        return originFeatName;
    }

    public void setOriginFeatName(String originFeatName) {
        this.originFeatName = originFeatName;
    }

    public String getOriginFeatDescription() {
        return originFeatDescription;
    }

    public void setOriginFeatDescription(String originFeatDescription) {
        this.originFeatDescription = originFeatDescription;
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
