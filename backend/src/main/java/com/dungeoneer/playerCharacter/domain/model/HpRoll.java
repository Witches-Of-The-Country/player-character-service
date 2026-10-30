package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HpRoll {

    private final UUID id;
    private final UUID userId;
    private final UUID archetypeId;
    private final int targetLevel;
    private final List<Integer> rolledHpPerLevel;
    private final boolean used;
    private final Instant createdAt;

    public HpRoll(UUID id, UUID userId, UUID archetypeId, int targetLevel, List<Integer> rolledHpPerLevel, boolean used, Instant createdAt) {
        if (userId == null) {
            throw new CharacterValidationException("UserId cannot be null for HP roll.");
        }
        if (archetypeId == null) {
            throw new CharacterValidationException("ArchetypeId cannot be null for HP roll.");
        }
        if (targetLevel < 2 || targetLevel > 20) {
            throw new CharacterValidationException("Target level for HP roll must be between 2 and 20. Provided: " + targetLevel);
        }
        int expectedRolls = targetLevel - 1;
        if (rolledHpPerLevel == null || rolledHpPerLevel.size() != expectedRolls) {
            throw new CharacterValidationException("Rolled HP per level must contain exactly " + expectedRolls + " rolls.");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.archetypeId = archetypeId;
        this.targetLevel = targetLevel;
        this.rolledHpPerLevel = Collections.unmodifiableList(new ArrayList<>(rolledHpPerLevel));
        this.used = used;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static HpRoll createNew(UUID userId, UUID archetypeId, int targetLevel, List<Integer> rolledHpPerLevel) {
        return new HpRoll(UUID.randomUUID(), userId, archetypeId, targetLevel, rolledHpPerLevel, false, Instant.now());
    }

    public HpRoll markAsUsed() {
        return new HpRoll(this.id, this.userId, this.archetypeId, this.targetLevel, this.rolledHpPerLevel, true, this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getArchetypeId() {
        return archetypeId;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public List<Integer> getRolledHpPerLevel() {
        return rolledHpPerLevel;
    }

    public boolean isUsed() {
        return used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HpRoll that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
