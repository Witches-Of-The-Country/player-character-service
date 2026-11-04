package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class Subclass {

    private final UUID id;
    private final UUID archetypeId;
    private final Ruleset ruleset;
    private final String slug;
    private final String nameEn;
    private final String name;
    private final String description;
    private final String source;
    private final UUID ownerId;

    public Subclass(
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
        if (id == null) {
            throw new CharacterValidationException("Subclass id cannot be null.");
        }
        if (archetypeId == null) {
            throw new CharacterValidationException("Archetype id cannot be null.");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null.");
        }
        if (slug == null || slug.trim().isEmpty()) {
            throw new CharacterValidationException("Subclass slug cannot be blank.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Subclass name cannot be blank.");
        }

        this.id = id;
        this.archetypeId = archetypeId;
        this.ruleset = ruleset;
        this.slug = slug.trim();
        this.nameEn = nameEn != null && !nameEn.trim().isEmpty() ? nameEn.trim() : this.slug;
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.source = source != null && !source.trim().isEmpty() ? source.trim() : "SRD";
        this.ownerId = ownerId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subclass subclass)) return false;
        return Objects.equals(id, subclass.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Subclass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ruleset=" + ruleset +
                '}';
    }
}
