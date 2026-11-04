package com.dungeoneer.playerCharacter.adapter.in.web.mapper;

import com.dungeoneer.playerCharacter.adapter.in.web.dto.AbilityRollResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterNoteResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterSheetResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterSummaryResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterNoteRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreatePlayerCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.HpRollResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterNoteRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterRequest;
import com.dungeoneer.playerCharacter.application.dto.AddCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterResponseDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.dto.ClassProgressionCommand;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterNoteCommand;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;
import java.util.UUID;

public final class PlayerCharacterWebMapper {

    private PlayerCharacterWebMapper() {
    }

    public static CreateCharacterCommand toCommand(UUID userId, CreateCharacterRequest request) {
        int str = getAbilityScore(request.abilityScores(), "strength");
        int dex = getAbilityScore(request.abilityScores(), "dexterity");
        int con = getAbilityScore(request.abilityScores(), "constitution");
        int intel = getAbilityScore(request.abilityScores(), "intelligence");
        int wis = getAbilityScore(request.abilityScores(), "wisdom");
        int cha = getAbilityScore(request.abilityScores(), "charisma");

        List<ClassProgressionCommand> classProgressions = request.classProgressions() != null
                ? request.classProgressions().stream()
                .map(cp -> new ClassProgressionCommand(cp.archetypeId(), cp.subclassId(), cp.classLevel()))
                .toList()
                : List.of();

        List<String> skillProficiencies = request.skillProficiencies() != null
                ? request.skillProficiencies()
                : List.of();

        List<UUID> hpRollIds = request.hpRollIds() != null
                ? request.hpRollIds()
                : (request.hpRollId() != null ? List.of(request.hpRollId()) : List.of());

        return new CreateCharacterCommand(
                userId,
                request.name(),
                request.level(),
                request.ruleset(),
                request.lineageId(),
                request.backgroundId(),
                request.archetypeId(),
                request.subclassId(),
                request.abilityScoreMethod(),
                str,
                dex,
                con,
                intel,
                wis,
                cha,
                request.abilityScoreBonuses(),
                request.abilityRollId(),
                request.hpCalculationMethod() != null ? request.hpCalculationMethod() : HpCalculationMethod.MAX,
                request.hpRollId(),
                hpRollIds,
                request.notes(),
                classProgressions,
                skillProficiencies,
                request.originFeatId(),
                request.avatarUrl()
        );
    }

    public static UpdateCharacterCommand toCommand(UUID characterId, UUID userId, UpdateCharacterRequest request) {
        return new UpdateCharacterCommand(
                characterId,
                userId,
                request.name(),
                request.hitPointsCurrent(),
                request.hitPointsTemp(),
                request.notes(),
                request.avatarUrl()
        );
    }

    public static CharacterSheetResponse toResponse(CharacterSheetResult result) {
        return new CharacterSheetResponse(
                result.id(),
                result.userId(),
                result.name(),
                result.level(),
                result.hitPointsCurrent(),
                result.hitPointsMax(),
                result.hitPointsTemp(),
                result.armorClass(),
                result.initiativeBonus(),
                result.speed(),
                result.proficiencyBonus(),
                result.ruleset(),
                result.abilityScoreMethod(),
                result.hpCalculationMethod(),
                result.hasPendingLevelChoices(),
                result.notes(),
                result.abilityScores(),
                result.lineage(),
                result.background(),
                result.archetype(),
                result.classProgressions(),
                result.savingThrows(),
                result.skills(),
                result.experiencePoints(),
                result.campaignId(),
                result.inventory(),
                result.feats(),
                result.actions(),
                result.currency(),
                result.avatarUrl(),
                result.classes(),
                result.exhaustion(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public static CharacterSummaryResponse toResponse(CharacterSummaryResult result) {
        return new CharacterSummaryResponse(
                result.id(),
                result.name(),
                result.level(),
                result.ruleset(),
                result.className(),
                result.subclassName(),
                result.lineageName(),
                result.backgroundName(),
                result.hitPointsCurrent(),
                result.hitPointsMax(),
                result.armorClass(),
                result.avatarUrl(),
                result.classes(),
                result.updatedAt()
        );
    }

    public static AbilityRollResponse toResponse(RollAbilityScoresResult result) {
        return new AbilityRollResponse(
                result.rollId(),
                result.userId(),
                result.rolledScores(),
                result.diceBreakdownJson(),
                result.isNew(),
                result.createdAt()
        );
    }

    public static HpRollResponse toResponse(RollHitPointsResult result) {
        return new HpRollResponse(
                result.rollId(),
                result.userId(),
                result.archetypeId(),
                result.targetLevel(),
                result.rolledHpPerLevel(),
                result.isNew(),
                result.createdAt()
        );
    }

    // Legacy support
    public static CreateCharacterCommand toCommand(CreatePlayerCharacterRequest request) {
        return new CreateCharacterCommand(
                request.name(),
                request.level(),
                1,
                Ruleset.valueOf(request.ruleset()),
                AbilityScoreMethod.POINT_BUY,
                request.lineageId(),
                request.backgroundId(),
                request.userId(),
                getAbilityScore(request.abilityScores(), "strength"),
                getAbilityScore(request.abilityScores(), "dexterity"),
                getAbilityScore(request.abilityScores(), "constitution"),
                getAbilityScore(request.abilityScores(), "intelligence"),
                getAbilityScore(request.abilityScores(), "wisdom"),
                getAbilityScore(request.abilityScores(), "charisma")
        );
    }

    public static CharacterResponseDto toDto(PlayerCharacter character) {
        return new CharacterResponseDto(
                character.getId(),
                character.getName(),
                character.getLevel(),
                character.getRuleset().name(),
                character.getHitPointsMax(),
                character.getHitPointsCurrent(),
                character.getArmorClass(),
                character.getSpeed(),
                character.getProficiencyBonus(),
                character.getInitiativeBonus()
        );
    }

    public static CharacterResponseDto toDto(CharacterSheetResult sheet) {
        return new CharacterResponseDto(
                sheet.id(),
                sheet.name(),
                sheet.level(),
                sheet.ruleset().name(),
                sheet.hitPointsMax(),
                sheet.hitPointsCurrent(),
                sheet.armorClass(),
                sheet.speed(),
                sheet.proficiencyBonus(),
                sheet.initiativeBonus()
        );
    }

    public static AddCharacterNoteCommand toCommand(CreateCharacterNoteRequest request) {
        return new AddCharacterNoteCommand(
                request.title(),
                request.body()
        );
    }

    public static UpdateCharacterNoteCommand toCommand(UpdateCharacterNoteRequest request) {
        return new UpdateCharacterNoteCommand(
                request.title(),
                request.body()
        );
    }

    public static CharacterNoteResponse toResponse(CharacterNoteResult result) {
        return new CharacterNoteResponse(
                result.id(),
                result.characterId(),
                result.title(),
                result.body(),
                result.createdAt()
        );
    }

    private static int getAbilityScore(java.util.Map<String, Integer> map, String key) {
        if (map == null) return 10;
        Integer val = map.get(key);
        if (val == null) {
            val = map.get(key.toUpperCase());
        }
        return val != null ? val : 10;
    }
}
