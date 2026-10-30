package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;

import java.util.UUID;

public interface GetCharacterUseCase {
    CharacterSheetResult getCharacter(UUID characterId, UUID userId);
}
