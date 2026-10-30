package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CharacterSheetResult(
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
}
