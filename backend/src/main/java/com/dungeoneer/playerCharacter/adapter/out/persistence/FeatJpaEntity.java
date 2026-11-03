package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.FeatType;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.RestType;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "feats", schema = "player_character")
public class FeatJpaEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

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

    @Column(name = "prerequisites", length = 255)
    private String prerequisites;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "feat_type", length = 30, nullable = false)
    private FeatType featType = FeatType.GENERAL;

    @Column(name = "mechanical_effects", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String mechanicalEffects = "{}";

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

    public FeatJpaEntity() {
    }

    public FeatJpaEntity(
            UUID id,
            Ruleset ruleset,
            String slug,
            String nameEn,
            String name,
            String prerequisites,
            String description,
            FeatType featType,
            String mechanicalEffects,
            String source,
            UUID ownerId
    ) {
        this.id = id;
        this.ruleset = ruleset;
        this.slug = slug;
        this.nameEn = nameEn;
        this.name = name;
        this.prerequisites = prerequisites;
        this.description = description;
        this.featType = featType != null ? featType : FeatType.GENERAL;
        this.mechanicalEffects = mechanicalEffects != null ? mechanicalEffects : "{}";
        this.source = source != null ? source : "SRD";
        this.ownerId = ownerId;
    }

    public static FeatJpaEntity fromDomain(Feat feat) {
        String json = serializeEffects(feat.getMechanicalEffects());
        return new FeatJpaEntity(
                feat.getId(),
                feat.getRuleset(),
                feat.getSlug(),
                feat.getNameEn(),
                feat.getName(),
                feat.getPrerequisites(),
                feat.getDescription(),
                feat.getFeatType(),
                json,
                feat.getSource(),
                feat.getOwnerId()
        );
    }

    public Feat toDomain() {
        MechanicalEffects effects = deserializeEffects(this.mechanicalEffects);
        return new Feat(
                this.id,
                this.ruleset,
                this.slug,
                this.nameEn,
                this.name,
                this.prerequisites,
                this.description,
                this.source,
                this.ownerId,
                this.featType,
                effects
        );
    }

    public static String serializeEffects(MechanicalEffects effects) {
        if (effects == null) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(effects);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public static MechanicalEffects deserializeEffects(String json) {
        if (json == null || json.isBlank() || json.trim().equals("{}")) {
            return MechanicalEffects.empty();
        }
        try {
            return OBJECT_MAPPER.readValue(json, MechanicalEffects.class);
        } catch (Exception e) {
            return MechanicalEffects.empty();
        }
    }

    public UUID getId() {
        return id;
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

    public String getPrerequisites() {
        return prerequisites;
    }

    public String getDescription() {
        return description;
    }

    public FeatType getFeatType() {
        return featType;
    }

    public void setFeatType(FeatType featType) {
        this.featType = featType;
    }

    public String getMechanicalEffects() {
        return mechanicalEffects;
    }

    public void setMechanicalEffects(String mechanicalEffects) {
        this.mechanicalEffects = mechanicalEffects;
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
