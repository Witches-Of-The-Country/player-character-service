package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;

public interface CreateCharacterUseCase {
    CharacterSheetResult createCharacter(CreateCharacterCommand command);
}
