package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Background {

    private final UUID id;
    private final String name;
    private final String description;
    private final List<String> skillProficiencies;
    private final List<String> toolProficiencies;
    private final List<String> languages;
    private final List<String> allowedAbilityBonuses;
    private final String originFeatName;
    private final String originFeatDescription;
    private final Ruleset ruleset;
    private final String source;
    private final UUID ownerId;

    public Background(
            UUID id,
            String name,
            String description,
            List<String> skillProficiencies,
            List<String> toolProficiencies,
            List<String> languages,
            List<String> allowedAbilityBonuses,
            String originFeatName,
            String originFeatDescription,
            Ruleset ruleset,
            String source,
            UUID ownerId
    ) {
        if (id == null) {
            throw new CharacterValidationException("Background id cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Background name cannot be blank");
        }
        if (skillProficiencies == null) {
            throw new CharacterValidationException("Skill proficiencies cannot be null");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null");
        }

        this.id = id;
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.skillProficiencies = Collections.unmodifiableList(new ArrayList<>(skillProficiencies));
        this.toolProficiencies = Collections.unmodifiableList(
                toolProficiencies != null ? new ArrayList<>(toolProficiencies) : Collections.emptyList()
        );
        this.languages = Collections.unmodifiableList(
                languages != null ? new ArrayList<>(languages) : Collections.emptyList()
        );
        this.allowedAbilityBonuses = Collections.unmodifiableList(
                allowedAbilityBonuses != null ? new ArrayList<>(allowedAbilityBonuses) : Collections.emptyList()
        );
        this.originFeatName = originFeatName != null && !originFeatName.trim().isEmpty()
                ? originFeatName.trim()
                : null;
        this.originFeatDescription = originFeatDescription != null && !originFeatDescription.trim().isEmpty()
                ? originFeatDescription.trim()
                : null;
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

    public List<String> getSkillProficiencies() {
        return skillProficiencies;
    }

    public List<String> getToolProficiencies() {
        return toolProficiencies;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public List<String> getAllowedAbilityBonuses() {
        return allowedAbilityBonuses;
    }

    public String getOriginFeatName() {
        return originFeatName;
    }

    public String getOriginFeatDescription() {
        return originFeatDescription;
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
