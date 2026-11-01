package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.LevelUpCommand;

import java.util.UUID;

public interface LevelUpUseCase {
    CharacterSheetResult levelUp(UUID characterId, UUID userId, LevelUpCommand command);
}
