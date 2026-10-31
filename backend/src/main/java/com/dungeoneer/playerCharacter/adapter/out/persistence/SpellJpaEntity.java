package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "spells", schema = "player_character")
public class SpellJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 20, nullable = false)
    private Ruleset ruleset;

    @Column(name = "slug", length = 150, nullable = false)
    private String slug;

    @Column(name = "name_en", length = 150, nullable = false)
    private String nameEn;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "school", length = 50, nullable = false)
    private String school;

    @Column(name = "casting_time", length = 100)
    private String castingTime;

    @Column(name = "range_area", length = 150)
    private String rangeArea;

    @Column(name = "components", length = 255)
    private String components;

    @Column(name = "duration", length = 150)
    private String duration;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "source", length = 20, nullable = false)
    private String source = "SRD";

    @Column(name = "owner_id")
    private UUID ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SpellJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCastingTime() {
        return castingTime;
    }

    public void setCastingTime(String castingTime) {
        this.castingTime = castingTime;
    }

    public String getRangeArea() {
        return rangeArea;
    }

    public void setRangeArea(String rangeArea) {
        this.rangeArea = rangeArea;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
