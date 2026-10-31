package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "items", schema = "player_character")
public class ItemJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 20, nullable = false)
    private Ruleset ruleset;

    @Column(name = "slug", length = 150, nullable = false)
    private String slug;

    @Column(name = "name_en", length = 150, nullable = false)
    private String nameEn;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "item_category", length = 30, nullable = false)
    private String itemCategory;

    @Column(name = "item_type", length = 50, nullable = false)
    private String itemType;

    @Column(name = "rarity", length = 30, nullable = false)
    private String rarity = "NONE";

    @Column(name = "attunement", nullable = false)
    private boolean attunement = false;

    @Column(name = "cost", length = 50)
    private String cost;

    @Column(name = "weight", length = 30)
    private String weight;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "source", length = 20, nullable = false)
    private String source = "SRD";

    @Column(name = "owner_id")
    private UUID ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ItemJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public boolean isAttunement() {
        return attunement;
    }

    public void setAttunement(boolean attunement) {
        this.attunement = attunement;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
