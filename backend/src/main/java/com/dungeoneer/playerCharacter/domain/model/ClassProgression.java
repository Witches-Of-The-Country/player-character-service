package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class ClassProgression {

    private final UUID id;
    private final UUID characterId;
    private final UUID archetypeId;
    private final UUID subclassId;
    private final int classLevel;
    private final int hitDiceSpent;

    public ClassProgression(
            UUID id,
            UUID characterId,
            UUID archetypeId,
            UUID subclassId,
            int classLevel,
            int hitDiceSpent
    ) {
        if (archetypeId == null) {
            throw new CharacterValidationException("ArchetypeId cannot be null.");
        }
        if (classLevel < 1 || classLevel > 20) {
            throw new CharacterValidationException("Class level must be between 1 and 20. Provided: " + classLevel);
        }
        if (hitDiceSpent < 0 || hitDiceSpent > classLevel) {
            throw new CharacterValidationException(
                    "Hit dice spent must be between 0 and class level (" + classLevel + "). Provided: " + hitDiceSpent
            );
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.characterId = characterId;
        this.archetypeId = archetypeId;
        this.subclassId = subclassId;
        this.classLevel = classLevel;
        this.hitDiceSpent = hitDiceSpent;
    }

    public ClassProgression(UUID id, UUID characterId, UUID archetypeId, UUID subclassId, int classLevel) {
        this(id, characterId, archetypeId, subclassId, classLevel, 0);
    }

    public static ClassProgression createInitial(UUID archetypeId, UUID subclassId, int level) {
        return new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, level, 0);
    }

    public ClassProgression withClassLevel(int newLevel) {
        return new ClassProgression(this.id, this.characterId, this.archetypeId, this.subclassId, newLevel, this.hitDiceSpent);
    }

    public ClassProgression withSubclassId(UUID newSubclassId) {
        return new ClassProgression(this.id, this.characterId, this.archetypeId, newSubclassId, this.classLevel, this.hitDiceSpent);
    }

    public ClassProgression withHitDiceSpent(int newSpent) {
        return new ClassProgression(this.id, this.characterId, this.archetypeId, this.subclassId, this.classLevel, newSpent);
    }

    public ClassProgression spendHitDice(int count) {
        if (count <= 0) {
            throw new CharacterValidationException("Hit dice spend count must be greater than 0. Provided: " + count);
        }
        int available = getAvailableHitDice();
        if (count > available) {
            throw new CharacterValidationException(
                    "Cannot spend " + count + " hit dice. Only " + available + " available for this class."
            );
        }
        return withHitDiceSpent(this.hitDiceSpent + count);
    }

    public ClassProgression recoverHitDice(int count) {
        if (count <= 0) {
            throw new CharacterValidationException("Hit dice recover count must be greater than 0. Provided: " + count);
        }
        int newSpent = Math.max(0, this.hitDiceSpent - count);
        return withHitDiceSpent(newSpent);
    }

    public int getAvailableHitDice() {
        return this.classLevel - this.hitDiceSpent;
    }

    public int getHitDiceSpent() {
        return hitDiceSpent;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public UUID getArchetypeId() {
        return archetypeId;
    }

    public UUID getSubclassId() {
        return subclassId;
    }

    public int getClassLevel() {
        return classLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassProgression that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ClassProgression{" +
                "id=" + id +
                ", characterId=" + characterId +
                ", archetypeId=" + archetypeId +
                ", subclassId=" + subclassId +
                ", classLevel=" + classLevel +
                ", hitDiceSpent=" + hitDiceSpent +
                '}';
    }
}
