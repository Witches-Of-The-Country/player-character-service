package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;
import java.util.UUID;

public class CharacterFeat {

    private final UUID id;
    private final UUID characterId;
    private final UUID featId;
    private final int unlockedAtLevel;
    private final Integer currentCharges;
    private final Integer maxCharges;
    private final String choicesJson;

    public CharacterFeat(
            UUID id,
            UUID characterId,
            UUID featId,
            int unlockedAtLevel,
            Integer currentCharges,
            Integer maxCharges,
            String choicesJson
    ) {
        if (featId == null) {
            throw new CharacterValidationException("FeatId cannot be null.");
        }
        if (unlockedAtLevel < 1 || unlockedAtLevel > 20) {
            throw new CharacterValidationException("Unlocked level must be between 1 and 20. Provided: " + unlockedAtLevel);
        }
        if (currentCharges != null && currentCharges < 0) {
            throw new CharacterValidationException("Current charges cannot be negative.");
        }
        if (maxCharges != null && maxCharges < 0) {
            throw new CharacterValidationException("Max charges cannot be negative.");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.characterId = characterId;
        this.featId = featId;
        this.unlockedAtLevel = unlockedAtLevel;
        this.currentCharges = currentCharges;
        this.maxCharges = maxCharges;
        this.choicesJson = choicesJson != null ? choicesJson : "{}";
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public UUID getFeatId() {
        return featId;
    }

    public int getUnlockedAtLevel() {
        return unlockedAtLevel;
    }

    public Integer getCurrentCharges() {
        return currentCharges;
    }

    public Integer getMaxCharges() {
        return maxCharges;
    }

    public String getChoicesJson() {
        return choicesJson;
    }

    public CharacterFeat withCurrentCharges(int currentCharges) {
        return new CharacterFeat(
                this.id,
                this.characterId,
                this.featId,
                this.unlockedAtLevel,
                currentCharges,
                this.maxCharges,
                this.choicesJson
        );
    }

    public CharacterFeat recoverCharges(RestType restType, RestType featRestType) {
        if (this.maxCharges == null) {
            return this;
        }
        if (restType == RestType.LONG) {
            return withCurrentCharges(this.maxCharges);
        }
        if (restType == RestType.SHORT && featRestType == RestType.SHORT) {
            return withCurrentCharges(this.maxCharges);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterFeat that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
