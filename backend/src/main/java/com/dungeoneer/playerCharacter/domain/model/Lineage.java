package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Lineage {

    private final UUID id;
    private final String name;
    private final String description;
    private final int speed;
    private final String size;
    private final Map<String, Integer> abilityBonuses;
    private final List<String> traitsSummary;
    private final Ruleset ruleset;
    private final String source;
    private final UUID ownerId;

    public Lineage(
            UUID id,
            String name,
            String description,
            int speed,
            String size,
            Map<String, Integer> abilityBonuses,
            List<String> traitsSummary,
            Ruleset ruleset,
            String source,
            UUID ownerId
    ) {
        if (id == null) {
            throw new CharacterValidationException("Lineage id cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Lineage name cannot be blank");
        }
        if (speed <= 0) {
            throw new CharacterValidationException("Speed must be greater than 0");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null");
        }

        this.id = id;
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.speed = speed;
        this.size = size != null && !size.trim().isEmpty() ? size.trim().toUpperCase() : "MEDIUM";
        this.abilityBonuses = Collections.unmodifiableMap(
                abilityBonuses != null ? new HashMap<>(abilityBonuses) : Collections.emptyMap()
        );
        this.traitsSummary = Collections.unmodifiableList(
                traitsSummary != null ? new ArrayList<>(traitsSummary) : Collections.emptyList()
        );
        this.ruleset = ruleset;
        this.source = source != null && !source.trim().isEmpty() ? source.trim() : "SRD";
        this.ownerId = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSpeed() {
        return speed;
    }

    public String getSize() {
        return size;
    }

    public Map<String, Integer> getAbilityBonuses() {
        return abilityBonuses;
    }

    public List<String> getTraitsSummary() {
        return traitsSummary;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public String getSource() {
        return source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }
}
