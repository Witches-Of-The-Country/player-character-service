package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "ability_scores")
public class AbilityScoresJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false, unique = true)
    private PlayerCharacterJpaEntity character;

    @Column(name = "strength", nullable = false)
    private int strength;

    @Column(name = "dexterity", nullable = false)
    private int dexterity;

    @Column(name = "constitution", nullable = false)
    private int constitution;

    @Column(name = "intelligence", nullable = false)
    private int intelligence;

    @Column(name = "wisdom", nullable = false)
    private int wisdom;

    @Column(name = "charisma", nullable = false)
    private int charisma;

    public AbilityScoresJpaEntity() {
    }

    public AbilityScoresJpaEntity(
            UUID id,
            PlayerCharacterJpaEntity character,
            int strength,
            int dexterity,
            int constitution,
            int intelligence,
            int wisdom,
            int charisma
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.character = character;
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    public static AbilityScoresJpaEntity fromDomain(AbilityScores domain, PlayerCharacterJpaEntity characterEntity) {
        return new AbilityScoresJpaEntity(
                UUID.randomUUID(),
                characterEntity,
                domain.getStrength(),
                domain.getDexterity(),
                domain.getConstitution(),
                domain.getIntelligence(),
                domain.getWisdom(),
                domain.getCharisma()
        );
    }

    public AbilityScores toDomain() {
        return new AbilityScores(
                this.strength,
                this.dexterity,
                this.constitution,
                this.intelligence,
                this.wisdom,
                this.charisma
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

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getConstitution() {
        return constitution;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
}
