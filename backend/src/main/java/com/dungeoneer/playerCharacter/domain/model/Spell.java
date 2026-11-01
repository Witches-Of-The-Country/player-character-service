package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class Spell {

    private final UUID id;
    private final Ruleset ruleset;
    private final String slug;
    private final String nameEn;
    private final String name;
    private final int level;
    private final String school;
    private final String castingTime;
    private final String rangeArea;
    private final String components;
    private final String duration;
    private final String description;
    private final String source;
    private final UUID ownerId;

    public Spell(
            UUID id,
            Ruleset ruleset,
            String slug,
            String nameEn,
            String name,
            int level,
            String school,
            String castingTime,
            String rangeArea,
            String components,
            String duration,
            String description,
            String source,
            UUID ownerId
    ) {
        if (id == null) {
            throw new CharacterValidationException("Spell id cannot be null");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null");
        }
        if (slug == null || slug.trim().isEmpty()) {
            throw new CharacterValidationException("Spell slug cannot be blank");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Spell name cannot be blank");
        }
        if (level < 0 || level > 9) {
            throw new CharacterValidationException("Spell level must be between 0 and 9");
        }
        if (school == null || school.trim().isEmpty()) {
            throw new CharacterValidationException("Spell school cannot be blank");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new CharacterValidationException("Spell description cannot be blank");
        }

        this.id = id;
        this.ruleset = ruleset;
        this.slug = slug.trim();
        this.nameEn = (nameEn != null && !nameEn.trim().isEmpty()) ? nameEn.trim() : this.slug;
        this.name = name.trim();
        this.level = level;
        this.school = school.trim();
        this.castingTime = castingTime;
        this.rangeArea = rangeArea;
        this.components = components;
        this.duration = duration;
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

    public int getLevel() {
        return level;
    }

    public String getSchool() {
        return school;
    }

    public String getCastingTime() {
        return castingTime;
    }

    public String getRangeArea() {
        return rangeArea;
    }

    public String getComponents() {
        return components;
    }

    public String getDuration() {
        return duration;
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
        Spell spell = (Spell) o;
        return Objects.equals(id, spell.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
