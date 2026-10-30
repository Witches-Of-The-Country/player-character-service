package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AbilityRoll {

    private final UUID id;
    private final UUID userId;
    private final List<Integer> rolledScores;
    private final String diceBreakdownJson;
    private final boolean used;
    private final Instant createdAt;

    public AbilityRoll(UUID id, UUID userId, List<Integer> rolledScores, String diceBreakdownJson, boolean used, Instant createdAt) {
        if (userId == null) {
            throw new CharacterValidationException("UserId cannot be null for ability roll.");
        }
        if (rolledScores == null || rolledScores.size() != 6) {
            throw new CharacterValidationException("Rolled scores must contain exactly 6 scores.");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.rolledScores = Collections.unmodifiableList(new ArrayList<>(rolledScores));
        this.diceBreakdownJson = diceBreakdownJson != null ? diceBreakdownJson : "[]";
        this.used = used;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static AbilityRoll createNew(UUID userId, List<Integer> rolledScores, String diceBreakdownJson) {
        return new AbilityRoll(UUID.randomUUID(), userId, rolledScores, diceBreakdownJson, false, Instant.now());
    }

    public AbilityRoll markAsUsed() {
        return new AbilityRoll(this.id, this.userId, this.rolledScores, this.diceBreakdownJson, true, this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<Integer> getRolledScores() {
        return rolledScores;
    }

    public String getDiceBreakdownJson() {
        return diceBreakdownJson;
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
        if (!(o instanceof AbilityRoll that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
