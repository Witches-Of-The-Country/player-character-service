package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;

public interface CreateCharacterUseCase {
    PlayerCharacter createCharacter(CreateCharacterCommand command);
}
