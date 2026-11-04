package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CharacterNote {

    public static final int MAX_TITLE_LENGTH = 255;
    public static final int MAX_BODY_LENGTH = 10000;

    private final UUID id;
    private final UUID characterId;
    private final String title;
    private final String body;
    private final Instant createdAt;

    public CharacterNote(UUID id, UUID characterId, String title, String body, Instant createdAt) {
        if (characterId == null) {
            throw new CharacterValidationException("CharacterId cannot be null for CharacterNote.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new CharacterValidationException("Note title cannot be null or blank.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new CharacterValidationException("Note title cannot exceed " + MAX_TITLE_LENGTH + " characters.");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new CharacterValidationException("Note body cannot be null or blank.");
        }
        if (body.length() > MAX_BODY_LENGTH) {
            throw new CharacterValidationException("Note body cannot exceed " + MAX_BODY_LENGTH + " characters.");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.characterId = characterId;
        this.title = title.trim();
        this.body = body.trim();
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static CharacterNote createNew(UUID characterId, String title, String body) {
        return new CharacterNote(UUID.randomUUID(), characterId, title, body, Instant.now());
    }

    public CharacterNote update(String title, String body) {
        return new CharacterNote(this.id, this.characterId, title, body, this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterNote that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
