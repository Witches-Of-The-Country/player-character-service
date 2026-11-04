package com.dungeoneer.playerCharacter.application.port.in;

import java.util.UUID;

public interface DeleteCharacterUseCase {
    void deleteCharacter(UUID characterId, UUID userId);
}
