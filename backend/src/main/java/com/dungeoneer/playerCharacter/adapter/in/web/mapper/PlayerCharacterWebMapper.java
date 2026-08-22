package com.dungeoneer.playerCharacter.adapter.in.web.mapper;

import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreatePlayerCharacterRequest;
import com.dungeoneer.playerCharacter.application.dto.CharacterResponseDto;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

public class PlayerCharacterWebMapper {

    public static CreateCharacterCommand toCommand(CreatePlayerCharacterRequest request) {
        return new CreateCharacterCommand(
                request.name(),
                request.level(),
                1, // Default value for hitPointsMax since it's not in the request yet
                Ruleset.valueOf(request.ruleset()),
                AbilityScoreMethod.POINT_BUY, // Default value
                request.lineageId(),
                request.backgroundId(),
                request.userId(),
                request.abilityScores().getOrDefault("strength", 10),
                request.abilityScores().getOrDefault("dexterity", 10),
                request.abilityScores().getOrDefault("constitution", 10),
                request.abilityScores().getOrDefault("intelligence", 10),
                request.abilityScores().getOrDefault("wisdom", 10),
                request.abilityScores().getOrDefault("charisma", 10)
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
}
