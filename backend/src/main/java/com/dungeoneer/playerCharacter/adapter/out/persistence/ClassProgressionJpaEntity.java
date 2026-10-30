package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "class_progressions", schema = "player_character")
public class ClassProgressionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private PlayerCharacterJpaEntity character;

    @Column(name = "archetype_id", nullable = false)
    private UUID archetypeId;

    @Column(name = "subclass_id")
    private UUID subclassId;

    @Column(name = "class_level", nullable = false)
    private int classLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ClassProgressionJpaEntity() {
    }

    public ClassProgressionJpaEntity(
            UUID id,
            PlayerCharacterJpaEntity character,
            UUID archetypeId,
            UUID subclassId,
            int classLevel,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.character = character;
        this.archetypeId = archetypeId;
        this.subclassId = subclassId;
        this.classLevel = classLevel;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static ClassProgressionJpaEntity fromDomain(ClassProgression domain, PlayerCharacterJpaEntity character) {
        Instant now = Instant.now();
        return new ClassProgressionJpaEntity(
                domain.getId() != null ? domain.getId() : UUID.randomUUID(),
                character,
                domain.getArchetypeId(),
                domain.getSubclassId(),
                domain.getClassLevel(),
                now,
                now
        );
    }

    public ClassProgression toDomain() {
        return new ClassProgression(
                this.id,
                this.character != null ? this.character.getId() : null,
                this.archetypeId,
                this.subclassId,
                this.classLevel
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PlayerCharacterJpaEntity getCharacter() {
        return character;
    }

    public void setCharacter(PlayerCharacterJpaEntity character) {
        this.character = character;
    }

    public UUID getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(UUID archetypeId) {
        this.archetypeId = archetypeId;
    }

    public UUID getSubclassId() {
        return subclassId;
    }

    public void setSubclassId(UUID subclassId) {
        this.subclassId = subclassId;
    }

    public int getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(int classLevel) {
        this.classLevel = classLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
