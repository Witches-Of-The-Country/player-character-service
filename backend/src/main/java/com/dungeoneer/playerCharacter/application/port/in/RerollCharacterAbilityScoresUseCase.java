package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;

import java.util.UUID;

public interface RerollCharacterAbilityScoresUseCase {
    CharacterSheetResult rerollAbilityScores(UUID characterId, UUID userId, AbilityScores customScores);
    CharacterSheetResult updateRerollPermission(UUID characterId, UUID userId, boolean allowAbilityReroll);
}
