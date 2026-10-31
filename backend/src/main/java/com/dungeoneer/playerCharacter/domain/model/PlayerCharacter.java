package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerCharacter {

    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 20;
    public static final int DEFAULT_SPEED = 30;
    public static final int BASE_ARMOR_CLASS = 10;

    private final UUID id;
    private final String name;
    private final int level;
    private final int hitPointsCurrent;
    private final int hitPointsMax;
    private final int hitPointsTemp;
    private final int armorClass;
    private final int initiativeBonus;
    private final int speed;
    private final int proficiencyBonus;
    private final Ruleset ruleset;
    private final AbilityScoreMethod abilityScoreMethod;
    private final UUID lineageId;
    private final UUID backgroundId;
    private final UUID userId;
    private final AbilityScores abilityScores;
    @Deprecated
    private final String notes;
    private final HpCalculationMethod hpCalculationMethod;
    private final boolean hasPendingLevelChoices;
    private final List<ClassProgression> classProgressions;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PlayerCharacter(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            String notes,
            HpCalculationMethod hpCalculationMethod,
            boolean hasPendingLevelChoices,
            List<ClassProgression> classProgressions,
            Instant createdAt,
            Instant updatedAt
    ) {
        validateName(name);
        validateLevel(level);
        validateHitPoints(hitPointsCurrent, hitPointsMax, hitPointsTemp);

        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null.");
        }
        if (abilityScoreMethod == null) {
            throw new CharacterValidationException("AbilityScoreMethod cannot be null.");
        }
        if (lineageId == null) {
            throw new CharacterValidationException("LineageId cannot be null.");
        }
        if (backgroundId == null) {
            throw new CharacterValidationException("BackgroundId cannot be null.");
        }
        if (abilityScores == null) {
            throw new CharacterValidationException("AbilityScores cannot be null.");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.name = name.trim();
        this.level = level;
        this.hitPointsCurrent = hitPointsCurrent;
        this.hitPointsMax = hitPointsMax;
        this.hitPointsTemp = hitPointsTemp;
        this.armorClass = armorClass;
        this.initiativeBonus = initiativeBonus;
        this.speed = speed > 0 ? speed : DEFAULT_SPEED;
        this.proficiencyBonus = proficiencyBonus;
        this.ruleset = ruleset;
        this.abilityScoreMethod = abilityScoreMethod;
        this.lineageId = lineageId;
        this.backgroundId = backgroundId;
        this.userId = userId;
        this.abilityScores = abilityScores;
        this.notes = notes != null ? notes : "";
        this.hpCalculationMethod = hpCalculationMethod != null ? hpCalculationMethod : HpCalculationMethod.MAX;
        this.hasPendingLevelChoices = hasPendingLevelChoices;
        this.classProgressions = classProgressions != null
                ? Collections.unmodifiableList(new ArrayList<>(classProgressions))
                : Collections.emptyList();
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    // Backwards-compatible constructor for existing tests and call sites
    public PlayerCharacter(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            Instant createdAt,
            Instant updatedAt
    ) {
        this(
                id,
                name,
                level,
                hitPointsCurrent,
                hitPointsMax,
                hitPointsTemp,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                "",
                HpCalculationMethod.MAX,
                level > 1,
                Collections.emptyList(),
                createdAt,
                updatedAt
        );
    }

    public static PlayerCharacter createNew(
            String name,
            int level,
            int hitPointsMax,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores
    ) {
        return createNew(
                name,
                level,
                hitPointsMax,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                "",
                HpCalculationMethod.MAX,
                Collections.emptyList()
        );
    }

    public static PlayerCharacter createNew(
            String name,
            int level,
            int hitPointsMax,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            String notes,
            HpCalculationMethod hpCalculationMethod,
            List<ClassProgression> classProgressions
    ) {
        if (abilityScores == null) {
            throw new CharacterValidationException("AbilityScores cannot be null.");
        }

        int proficiencyBonus = calculateProficiencyBonus(level);
        int dexModifier = abilityScores.getDexterityModifier();
        int armorClass = BASE_ARMOR_CLASS + dexModifier;
        int initiativeBonus = dexModifier;
        int speed = DEFAULT_SPEED;
        Instant now = Instant.now();
        boolean hasPendingChoices = level > 1;

        return new PlayerCharacter(
                UUID.randomUUID(),
                name,
                level,
                hitPointsMax,
                hitPointsMax,
                0,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                notes != null ? notes : "",
                hpCalculationMethod != null ? hpCalculationMethod : HpCalculationMethod.MAX,
                hasPendingChoices,
                classProgressions,
                now,
                now
        );
    }

    public PlayerCharacter updateSheet(
            String name,
            int hitPointsCurrent,
            int hitPointsTemp,
            String notes
    ) {
        validateName(name);
        validateHitPoints(hitPointsCurrent, this.hitPointsMax, hitPointsTemp);

        return new PlayerCharacter(
                this.id,
                name,
                this.level,
                hitPointsCurrent,
                this.hitPointsMax,
                hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                notes != null ? notes : "",
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.createdAt,
                Instant.now()
        );
    }

    public static int calculateProficiencyBonus(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new CharacterValidationException("Level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + ". Provided: " + level);
        }
        return (level - 1) / 4 + 2;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Character name cannot be null or empty.");
        }
        if (name.trim().length() > 100) {
            throw new CharacterValidationException("Character name length cannot exceed 100 characters.");
        }
    }

    private void validateLevel(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new CharacterValidationException("Level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + ". Provided: " + level);
        }
    }

    private void validateHitPoints(int current, int max, int temp) {
        if (max <= 0) {
            throw new CharacterValidationException("Max hit points must be greater than zero. Provided: " + max);
        }
        if (current < 0 || current > max) {
            throw new CharacterValidationException("Current hit points must be between 0 and max HP (" + max + "). Provided: " + current);
        }
        if (temp < 0) {
            throw new CharacterValidationException("Temporary hit points cannot be negative. Provided: " + temp);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHitPointsCurrent() {
        return hitPointsCurrent;
    }

    public int getHitPointsMax() {
        return hitPointsMax;
    }

    public int getHitPointsTemp() {
        return hitPointsTemp;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public int getInitiativeBonus() {
        return initiativeBonus;
    }

    public int getSpeed() {
        return speed;
    }

    public int getProficiencyBonus() {
        return proficiencyBonus;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public AbilityScoreMethod getAbilityScoreMethod() {
        return abilityScoreMethod;
    }

    public UUID getLineageId() {
        return lineageId;
    }

    public UUID getBackgroundId() {
        return backgroundId;
    }

    public UUID getUserId() {
        return userId;
    }

    public AbilityScores getAbilityScores() {
        return abilityScores;
    }

    @Deprecated
    public String getNotes() {
        return notes;
    }

    public HpCalculationMethod getHpCalculationMethod() {
        return hpCalculationMethod;
    }

    public boolean isHasPendingLevelChoices() {
        return hasPendingLevelChoices;
    }

    public List<ClassProgression> getClassProgressions() {
        return classProgressions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerCharacter that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PlayerCharacter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", HP=" + hitPointsCurrent + "/" + hitPointsMax +
                ", AC=" + armorClass +
                ", ruleset=" + ruleset +
                '}';
    }
}
