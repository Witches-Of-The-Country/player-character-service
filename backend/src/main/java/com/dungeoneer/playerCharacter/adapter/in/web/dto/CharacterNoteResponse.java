package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import java.time.Instant;
import java.util.UUID;

public record CharacterNoteResponse(
        UUID id,
        UUID characterId,
        String title,
        String body,
        Instant createdAt
) {
}
