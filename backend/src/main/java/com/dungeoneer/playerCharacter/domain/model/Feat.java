package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class Feat {

    private final UUID id;
    private final Ruleset ruleset;
    private final String slug;
    private final String nameEn;
    private final String name;
    private final String prerequisites;
    private final String description;
    private final String source;
    private final UUID ownerId;

    public Feat(
            UUID id,
            Ruleset ruleset,
            String slug,
            String nameEn,
            String name,
            String prerequisites,
            String description,
            String source,
            UUID ownerId
    ) {
        if (id == null) {
            throw new CharacterValidationException("Feat id cannot be null");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null");
        }
        if (slug == null || slug.trim().isEmpty()) {
            throw new CharacterValidationException("Feat slug cannot be blank");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Feat name cannot be blank");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new CharacterValidationException("Feat description cannot be blank");
        }

        this.id = id;
        this.ruleset = ruleset;
        this.slug = slug.trim();
        this.nameEn = (nameEn != null && !nameEn.trim().isEmpty()) ? nameEn.trim() : this.slug;
        this.name = name.trim();
        this.prerequisites = prerequisites;
        this.description = description.trim();
        this.source = (source != null && !source.trim().isEmpty()) ? source.trim() : "HOMEBREW";
        this.ownerId = ownerId;
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

    public String getSource() {
        return source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feat feat = (Feat) o;
        return Objects.equals(id, feat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
