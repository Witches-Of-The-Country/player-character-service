package com.dungeoneer.playerCharacter.domain.model;

import java.util.Collections;
import java.util.List;

public record SpellChoice(
        int cantripsCount,
        int spellsCount,
        int spellLevel,
        List<String> allowedClasses,
        boolean isRitualOnly
) {
    public SpellChoice {
        allowedClasses = allowedClasses != null ? Collections.unmodifiableList(allowedClasses) : Collections.emptyList();
    }
}
