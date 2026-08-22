package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Objects;

public class AbilityScores {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 30;

    private final int strength;
    private final int dexterity;
    private final int constitution;
    private final int intelligence;
    private final int wisdom;
    private final int charisma;

    public AbilityScores(int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        validateScore("Strength", strength);
        validateScore("Dexterity", dexterity);
        validateScore("Constitution", constitution);
        validateScore("Intelligence", intelligence);
        validateScore("Wisdom", wisdom);
        validateScore("Charisma", charisma);

        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    private void validateScore(String abilityName, int score) {
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new CharacterValidationException(
                    abilityName + " score must be between " + MIN_SCORE + " and " + MAX_SCORE + ". Provided: " + score
            );
        }
    }

    public static int calculateModifier(int score) {
        return Math.floorDiv(score - 10, 2);
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getConstitution() {
        return constitution;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public int getStrengthModifier() {
        return calculateModifier(this.strength);
    }

    public int getDexterityModifier() {
        return calculateModifier(this.dexterity);
    }

    public int getConstitutionModifier() {
        return calculateModifier(this.constitution);
    }

    public int getIntelligenceModifier() {
        return calculateModifier(this.intelligence);
    }

    public int getWisdomModifier() {
        return calculateModifier(this.wisdom);
    }

    public int getCharismaModifier() {
        return calculateModifier(this.charisma);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbilityScores that)) return false;
        return strength == that.strength &&
                dexterity == that.dexterity &&
                constitution == that.constitution &&
                intelligence == that.intelligence &&
                wisdom == that.wisdom &&
                charisma == that.charisma;
    }

    @Override
    public int hashCode() {
        return Objects.hash(strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    @Override
    public String toString() {
        return "AbilityScores{" +
                "STR=" + strength + " (" + getStrengthModifier() + ")" +
                ", DEX=" + dexterity + " (" + getDexterityModifier() + ")" +
                ", CON=" + constitution + " (" + getConstitutionModifier() + ")" +
                ", INT=" + intelligence + " (" + getIntelligenceModifier() + ")" +
                ", WIS=" + wisdom + " (" + getWisdomModifier() + ")" +
                ", CHA=" + charisma + " (" + getCharismaModifier() + ")" +
                '}';
    }
}
