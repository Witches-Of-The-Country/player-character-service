package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Archetype {

    private final UUID id;
    private final String name;
    private final HitDiceEnum hitDice;
    private final String primaryAbility;
    private final List<String> savingThrowProficiencies;
    private final List<String> armorProficiencies;
    private final List<String> weaponProficiencies;
    private final SpellcastingType spellcastingType;
    private final String spellcastingAbility;
    private final Ruleset ruleset;
    private final String source;
    private final UUID ownerId;

    public Archetype(
            UUID id,
            String name,
            HitDiceEnum hitDice,
            String primaryAbility,
            List<String> savingThrowProficiencies,
            List<String> armorProficiencies,
            List<String> weaponProficiencies,
            SpellcastingType spellcastingType,
            String spellcastingAbility,
            Ruleset ruleset,
            String source,
            UUID ownerId
    ) {
        if (id == null) {
            throw new CharacterValidationException("Archetype id cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Archetype name cannot be blank");
        }
        if (hitDice == null) {
            throw new CharacterValidationException("Hit dice cannot be null");
        }
        if (primaryAbility == null || primaryAbility.trim().isEmpty()) {
            throw new CharacterValidationException("Primary ability cannot be blank");
        }
        if (savingThrowProficiencies == null) {
            throw new CharacterValidationException("Saving throw proficiencies cannot be null");
        }
        if (spellcastingType == null) {
            throw new CharacterValidationException("Spellcasting type cannot be null");
        }
        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null");
        }

        this.id = id;
        this.name = name.trim();
        this.hitDice = hitDice;
        this.primaryAbility = primaryAbility.trim();
        this.savingThrowProficiencies = Collections.unmodifiableList(new ArrayList<>(savingThrowProficiencies));
        this.armorProficiencies = Collections.unmodifiableList(
                armorProficiencies != null ? new ArrayList<>(armorProficiencies) : Collections.emptyList()
        );
        this.weaponProficiencies = Collections.unmodifiableList(
                weaponProficiencies != null ? new ArrayList<>(weaponProficiencies) : Collections.emptyList()
        );
        this.spellcastingType = spellcastingType;
        this.spellcastingAbility = spellcastingAbility != null && !spellcastingAbility.trim().isEmpty()
                ? spellcastingAbility.trim()
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

    public HitDiceEnum getHitDice() {
        return hitDice;
    }

    public String getPrimaryAbility() {
        return primaryAbility;
    }

    public List<String> getSavingThrowProficiencies() {
        return savingThrowProficiencies;
    }

    public List<String> getArmorProficiencies() {
        return armorProficiencies;
    }

    public List<String> getWeaponProficiencies() {
        return weaponProficiencies;
    }

    public SpellcastingType getSpellcastingType() {
        return spellcastingType;
    }

    public String getSpellcastingAbility() {
        return spellcastingAbility;
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
