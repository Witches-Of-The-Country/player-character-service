package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "archetypes", schema = "player_character")
public class ArchetypeJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "hit_dice", length = 3, nullable = false)
    private String hitDice;

    @Column(name = "primary_ability", length = 15, nullable = false)
    private String primaryAbility;

    @Column(name = "saving_throw_proficiencies", columnDefinition = "varchar[]", nullable = false)
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> savingThrowProficiencies;

    @Column(name = "armor_proficiencies", columnDefinition = "varchar[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> armorProficiencies;

    @Column(name = "weapon_proficiencies", columnDefinition = "varchar[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> weaponProficiencies;

    @Enumerated(EnumType.STRING)
    @Column(name = "spellcasting_type", length = 15, nullable = false)
    private SpellcastingType spellcastingType;

    @Column(name = "spellcasting_ability", length = 15)
    private String spellcastingAbility;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 10, nullable = false)
    private Ruleset ruleset;

    @Column(name = "source", length = 10, nullable = false)
    private String source;

    @Column(name = "owner_id")
    private UUID ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ArchetypeJpaEntity() {
    }

    public Archetype toDomain() {
        return new Archetype(
                this.id,
                this.name,
                HitDiceEnum.fromString(this.hitDice),
                this.primaryAbility,
                this.savingThrowProficiencies,
                this.armorProficiencies,
                this.weaponProficiencies,
                this.spellcastingType,
                this.spellcastingAbility,
                this.ruleset,
                this.source,
                this.ownerId
        );
    }

    public static ArchetypeJpaEntity fromDomain(Archetype domain) {
        ArchetypeJpaEntity entity = new ArchetypeJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setHitDice(domain.getHitDice().name().toLowerCase());
        entity.setPrimaryAbility(domain.getPrimaryAbility());
        entity.setSavingThrowProficiencies(domain.getSavingThrowProficiencies());
        entity.setArmorProficiencies(domain.getArmorProficiencies());
        entity.setWeaponProficiencies(domain.getWeaponProficiencies());
        entity.setSpellcastingType(domain.getSpellcastingType());
        entity.setSpellcastingAbility(domain.getSpellcastingAbility());
        entity.setRuleset(domain.getRuleset());
        entity.setSource(domain.getSource());
        entity.setOwnerId(domain.getOwnerId());
        return entity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHitDice() {
        return hitDice;
    }

    public void setHitDice(String hitDice) {
        this.hitDice = hitDice;
    }

    public String getPrimaryAbility() {
        return primaryAbility;
    }

    public void setPrimaryAbility(String primaryAbility) {
        this.primaryAbility = primaryAbility;
    }

    public List<String> getSavingThrowProficiencies() {
        return savingThrowProficiencies;
    }

    public void setSavingThrowProficiencies(List<String> savingThrowProficiencies) {
        this.savingThrowProficiencies = savingThrowProficiencies;
    }

    public List<String> getArmorProficiencies() {
        return armorProficiencies;
    }

    public void setArmorProficiencies(List<String> armorProficiencies) {
        this.armorProficiencies = armorProficiencies;
    }

    public List<String> getWeaponProficiencies() {
        return weaponProficiencies;
    }

    public void setWeaponProficiencies(List<String> weaponProficiencies) {
        this.weaponProficiencies = weaponProficiencies;
    }

    public SpellcastingType getSpellcastingType() {
        return spellcastingType;
    }

    public void setSpellcastingType(SpellcastingType spellcastingType) {
        this.spellcastingType = spellcastingType;
    }

    public String getSpellcastingAbility() {
        return spellcastingAbility;
    }

    public void setSpellcastingAbility(String spellcastingAbility) {
        this.spellcastingAbility = spellcastingAbility;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
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
