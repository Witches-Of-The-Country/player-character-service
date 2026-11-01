package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Subclass;
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
@Table(name = "subclasses", schema = "player_character")
public class SubclassJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "archetype_id", nullable = false)
    private UUID archetypeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 20, nullable = false)
    private Ruleset ruleset;

    @Column(name = "slug", length = 120, nullable = false)
    private String slug;

    @Column(name = "name_en", length = 120, nullable = false)
    private String nameEn;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "source", length = 50, nullable = false)
    private String source = "SRD";

    @Column(name = "owner_id")
    private UUID ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SubclassJpaEntity() {
    }

    public SubclassJpaEntity(
            UUID id,
            UUID archetypeId,
            Ruleset ruleset,
            String slug,
            String nameEn,
            String name,
            String description,
            String source,
            UUID ownerId
    ) {
        this.id = id;
        this.archetypeId = archetypeId;
        this.ruleset = ruleset;
        this.slug = slug;
        this.nameEn = nameEn;
        this.name = name;
        this.description = description;
        this.source = source;
        this.ownerId = ownerId;
    }

    public static SubclassJpaEntity fromDomain(Subclass subclass) {
        return new SubclassJpaEntity(
                subclass.getId(),
                subclass.getArchetypeId(),
                subclass.getRuleset(),
                subclass.getSlug(),
                subclass.getNameEn(),
                subclass.getName(),
                subclass.getDescription(),
                subclass.getSource(),
                subclass.getOwnerId()
        );
    }

    public Subclass toDomain() {
        return new Subclass(
                this.id,
                this.archetypeId,
                this.ruleset,
                this.slug,
                this.nameEn,
                this.name,
                this.description,
                this.source,
                this.ownerId
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchetypeId() {
        return archetypeId;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public String getSlug() {
        return slug;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
