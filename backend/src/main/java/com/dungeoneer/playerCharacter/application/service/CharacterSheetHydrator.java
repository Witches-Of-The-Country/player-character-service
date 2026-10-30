package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Srd2014RulesEngine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class CharacterSheetHydrator {

    private static final Map<String, String> SKILL_TO_ABILITY = new LinkedHashMap<>();

    static {
        SKILL_TO_ABILITY.put("Athletics", "STRENGTH");
        SKILL_TO_ABILITY.put("Acrobatics", "DEXTERITY");
        SKILL_TO_ABILITY.put("Sleight of Hand", "DEXTERITY");
        SKILL_TO_ABILITY.put("Stealth", "DEXTERITY");
        SKILL_TO_ABILITY.put("Arcana", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("History", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Investigation", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Nature", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Religion", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Animal Handling", "WISDOM");
        SKILL_TO_ABILITY.put("Insight", "WISDOM");
        SKILL_TO_ABILITY.put("Medicine", "WISDOM");
        SKILL_TO_ABILITY.put("Perception", "WISDOM");
        SKILL_TO_ABILITY.put("Survival", "WISDOM");
        SKILL_TO_ABILITY.put("Deception", "CHARISMA");
        SKILL_TO_ABILITY.put("Intimidation", "CHARISMA");
        SKILL_TO_ABILITY.put("Performance", "CHARISMA");
        SKILL_TO_ABILITY.put("Persuasion", "CHARISMA");
    }

    private CharacterSheetHydrator() {
    }

    public static CharacterSheetResult toSheetResult(
            PlayerCharacter character,
            Archetype archetype,
            Lineage lineage,
            Background background
    ) {
        Map<String, Integer> savingThrows = calculateSavingThrows(character, archetype);
        Map<String, Integer> skills = calculateSkills(character, background);

        return new CharacterSheetResult(
                character.getId(),
                character.getUserId(),
                character.getName(),
                character.getLevel(),
                character.getHitPointsCurrent(),
                character.getHitPointsMax(),
                character.getHitPointsTemp(),
                character.getArmorClass(),
                character.getInitiativeBonus(),
                character.getSpeed(),
                character.getProficiencyBonus(),
                character.getRuleset(),
                character.getAbilityScoreMethod(),
                character.getHpCalculationMethod(),
                character.isHasPendingLevelChoices(),
                character.getNotes(),
                character.getAbilityScores(),
                lineage,
                background,
                archetype,
                character.getClassProgressions(),
                savingThrows,
                skills,
                character.getCreatedAt(),
                character.getUpdatedAt()
        );
    }

    public static Map<String, Integer> calculateSavingThrows(PlayerCharacter character, Archetype archetype) {
        AbilityScores scores = character.getAbilityScores();
        int prof = character.getProficiencyBonus();

        Set<String> profSavingThrows = (archetype != null && archetype.getSavingThrowProficiencies() != null)
                ? archetype.getSavingThrowProficiencies().stream()
                .map(Srd2014RulesEngine::normalizeAbility)
                .collect(Collectors.toSet())
                : Set.of();

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("STRENGTH", scores.getStrengthModifier() + (profSavingThrows.contains("STRENGTH") ? prof : 0));
        result.put("DEXTERITY", scores.getDexterityModifier() + (profSavingThrows.contains("DEXTERITY") ? prof : 0));
        result.put("CONSTITUTION", scores.getConstitutionModifier() + (profSavingThrows.contains("CONSTITUTION") ? prof : 0));
        result.put("INTELLIGENCE", scores.getIntelligenceModifier() + (profSavingThrows.contains("INTELLIGENCE") ? prof : 0));
        result.put("WISDOM", scores.getWisdomModifier() + (profSavingThrows.contains("WISDOM") ? prof : 0));
        result.put("CHARISMA", scores.getCharismaModifier() + (profSavingThrows.contains("CHARISMA") ? prof : 0));

        return result;
    }

    public static Map<String, Integer> calculateSkills(PlayerCharacter character, Background background) {
        AbilityScores scores = character.getAbilityScores();
        int prof = character.getProficiencyBonus();

        Set<String> profSkills = (background != null && background.getSkillProficiencies() != null)
                ? background.getSkillProficiencies().stream().map(String::trim).map(String::toUpperCase).collect(Collectors.toSet())
                : Set.of();

        Map<String, Integer> result = new LinkedHashMap<>();

        SKILL_TO_ABILITY.forEach((skillName, ability) -> {
            int abilityMod = switch (ability) {
                case "STRENGTH" -> scores.getStrengthModifier();
                case "DEXTERITY" -> scores.getDexterityModifier();
                case "CONSTITUTION" -> scores.getConstitutionModifier();
                case "INTELLIGENCE" -> scores.getIntelligenceModifier();
                case "WISDOM" -> scores.getWisdomModifier();
                case "CHARISMA" -> scores.getCharismaModifier();
                default -> 0;
            };

            boolean isProficient = profSkills.contains(skillName.toUpperCase());
            result.put(skillName, abilityMod + (isProficient ? prof : 0));
        });

        return result;
    }
}
