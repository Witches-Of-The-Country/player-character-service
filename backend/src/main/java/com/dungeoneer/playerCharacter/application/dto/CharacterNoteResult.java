package com.dungeoneer.playerCharacter.application.dto;

import java.time.Instant;
import java.util.UUID;

public record CharacterNoteResult(
        UUID id,
        UUID characterId,
        String title,
        String body,
        Instant createdAt
) {
}
