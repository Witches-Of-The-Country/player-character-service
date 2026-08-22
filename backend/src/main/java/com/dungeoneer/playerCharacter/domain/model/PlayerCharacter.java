package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.time.Instant;
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
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
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
        if (abilityScores == null) {
            throw new CharacterValidationException("AbilityScores cannot be null.");
        }

        int proficiencyBonus = calculateProficiencyBonus(level);
        int dexModifier = abilityScores.getDexterityModifier();
        int armorClass = BASE_ARMOR_CLASS + dexModifier;
        int initiativeBonus = dexModifier;
        int speed = DEFAULT_SPEED;
        Instant now = Instant.now();

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
                now,
                now
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
