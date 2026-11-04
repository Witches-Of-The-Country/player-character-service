package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "character_feats", schema = "player_character")
public class CharacterFeatJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private PlayerCharacterJpaEntity character;

    @Column(name = "feat_id", nullable = false)
    private UUID featId;

    @Column(name = "unlocked_at_level", nullable = false)
    private int unlockedAtLevel = 1;

    @Column(name = "current_charges")
    private Integer currentCharges;

    @Column(name = "max_charges")
    private Integer maxCharges;

    @Column(name = "choices", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String choices = "{}";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CharacterFeatJpaEntity() {
    }

    public CharacterFeatJpaEntity(
            UUID id,
            PlayerCharacterJpaEntity character,
            UUID featId,
            int unlockedAtLevel,
            Integer currentCharges,
            Integer maxCharges,
            String choices
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.character = character;
        this.featId = featId;
        this.unlockedAtLevel = unlockedAtLevel;
        this.currentCharges = currentCharges;
        this.maxCharges = maxCharges;
        this.choices = sanitizeChoicesJson(choices);
    }

    private static String sanitizeChoicesJson(String input) {
        if (input == null || input.isBlank()) {
            return "{}";
        }
        String trimmed = input.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return trimmed;
        }
        return "{\"source\":\"" + trimmed.replace("\"", "\\\"") + "\"}";
    }

    public static CharacterFeatJpaEntity fromDomain(CharacterFeat feat, PlayerCharacterJpaEntity character) {
        return new CharacterFeatJpaEntity(
                feat.getId(),
                character,
                feat.getFeatId(),
                feat.getUnlockedAtLevel(),
                feat.getCurrentCharges(),
                feat.getMaxCharges(),
                feat.getChoicesJson()
        );
    }

    public CharacterFeat toDomain() {
        return new CharacterFeat(
                this.id,
                this.character != null ? this.character.getId() : null,
                this.featId,
                this.unlockedAtLevel,
                this.currentCharges,
                this.maxCharges,
                this.choices
        );
    }

    public UUID getId() {
        return id;
    }

    public PlayerCharacterJpaEntity getCharacter() {
        return character;
    }

    public void setCharacter(PlayerCharacterJpaEntity character) {
        this.character = character;
    }

    public UUID getFeatId() {
        return featId;
    }

    public int getUnlockedAtLevel() {
        return unlockedAtLevel;
    }

    public void setUnlockedAtLevel(int unlockedAtLevel) {
        this.unlockedAtLevel = unlockedAtLevel;
    }

    public Integer getCurrentCharges() {
        return currentCharges;
    }

    public void setCurrentCharges(Integer currentCharges) {
        this.currentCharges = currentCharges;
    }

    public Integer getMaxCharges() {
        return maxCharges;
    }

    public void setMaxCharges(Integer maxCharges) {
        this.maxCharges = maxCharges;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = sanitizeChoicesJson(choices);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
