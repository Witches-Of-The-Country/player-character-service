package com.dungeoneer.playerCharacter.adapter.in.web.dto;

import com.dungeoneer.playerCharacter.application.dto.CharacterActionDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterFeatDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterItemDto;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CharacterSheetResponse(
        UUID id,
        UUID userId,
        String name,
        int level,
        int hitPointsCurrent,
        int hitPointsMax,
        int hitPointsTemp,
        int armorClass,
        int initiativeBonus,
        int speed,
        int proficiencyBonus,
        Ruleset ruleset,
        AbilityScoreMethod abilityScoreMethod,
        HpCalculationMethod hpCalculationMethod,
        boolean hasPendingLevelChoices,
        String notes,
        AbilityScores abilityScores,
        Lineage lineage,
        Background background,
        Archetype archetype,
        List<ClassProgression> classProgressions,
        Map<String, Integer> savingThrows,
        Map<String, Integer> skills,
        int experiencePoints,
        UUID campaignId,
        List<CharacterItemDto> inventory,
        List<CharacterFeatDto> feats,
        List<CharacterActionDto> actions,
        Instant createdAt,
        Instant updatedAt
) {
    public CharacterSheetResponse(
            UUID id,
            UUID userId,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            HpCalculationMethod hpCalculationMethod,
            boolean hasPendingLevelChoices,
            String notes,
            AbilityScores abilityScores,
            Lineage lineage,
            Background background,
            Archetype archetype,
            List<ClassProgression> classProgressions,
            Map<String, Integer> savingThrows,
            Map<String, Integer> skills,
            Instant createdAt,
            Instant updatedAt
    ) {
        this(
                id,
                userId,
                name,
                level,
                hitPointsCurrent,
                hitPointsMax,
                hitPointsTemp,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                hpCalculationMethod,
                hasPendingLevelChoices,
                notes,
                abilityScores,
                lineage,
                background,
                archetype,
                classProgressions,
                savingThrows,
                skills,
                0,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                createdAt,
                updatedAt
        );
    }

    public static CharacterSheetResponse fromResult(com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult result) {
        return com.dungeoneer.playerCharacter.adapter.in.web.mapper.PlayerCharacterWebMapper.toResponse(result);
    }
}
