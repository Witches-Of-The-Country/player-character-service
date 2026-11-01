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

    public ClassProgression(UUID id, UUID characterId, UUID archetypeId, UUID subclassId, int classLevel) {
        if (archetypeId == null) {
            throw new CharacterValidationException("ArchetypeId cannot be null.");
        }
        if (classLevel < 1 || classLevel > 20) {
            throw new CharacterValidationException("Class level must be between 1 and 20. Provided: " + classLevel);
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.characterId = characterId;
        this.archetypeId = archetypeId;
        this.subclassId = subclassId;
        this.classLevel = classLevel;
    }

    public static ClassProgression createInitial(UUID archetypeId, UUID subclassId, int level) {
        return new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, level);
    }

    public ClassProgression withClassLevel(int newLevel) {
        return new ClassProgression(this.id, this.characterId, this.archetypeId, this.subclassId, newLevel);
    }

    public ClassProgression withSubclassId(UUID newSubclassId) {
        return new ClassProgression(this.id, this.characterId, this.archetypeId, newSubclassId, this.classLevel);
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
                '}';
    }
}
