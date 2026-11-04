package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.RestCommand;

import java.util.UUID;

public interface RestUseCase {
    CharacterSheetResult takeRest(UUID characterId, UUID userId, RestCommand command);
    CharacterSheetResult useFeat(UUID characterId, UUID userId, UUID characterFeatId);
}
