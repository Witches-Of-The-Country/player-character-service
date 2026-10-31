package com.dungeoneer.playerCharacter.application.dto;

import java.util.UUID;

public record ClassProgressionCommand(
        UUID archetypeId,
        UUID subclassId,
        Integer classLevel
) {
}
