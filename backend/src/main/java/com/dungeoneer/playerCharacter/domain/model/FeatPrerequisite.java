package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

public record FeatPrerequisite(
        String type,
        String targetKey,
        Integer minValue,
        String displayLabel
) {
    public FeatPrerequisite {
        if (type == null || type.isBlank()) {
            throw new CharacterValidationException("Prerequisite type cannot be blank");
        }
    }
}
