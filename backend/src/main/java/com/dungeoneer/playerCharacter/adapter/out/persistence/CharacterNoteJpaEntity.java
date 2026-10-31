package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.CharacterNote;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "character_notes", schema = "player_character")
public class CharacterNoteJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public CharacterNoteJpaEntity() {
    }

    public CharacterNoteJpaEntity(UUID id, UUID characterId, String title, String body, Instant createdAt) {
        this.id = id;
        this.characterId = characterId;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static CharacterNoteJpaEntity fromDomain(CharacterNote note) {
        return new CharacterNoteJpaEntity(
                note.getId(),
                note.getCharacterId(),
                note.getTitle(),
                note.getBody(),
                note.getCreatedAt()
        );
    }

    public CharacterNote toDomain() {
        return new CharacterNote(
                this.id,
                this.characterId,
                this.title,
                this.body,
                this.createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public void setCharacterId(UUID characterId) {
        this.characterId = characterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
