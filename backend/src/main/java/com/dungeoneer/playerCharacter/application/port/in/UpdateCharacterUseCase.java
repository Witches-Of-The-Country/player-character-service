package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;

public interface UpdateCharacterUseCase {
    CharacterSheetResult updateCharacter(UpdateCharacterCommand command);
}
