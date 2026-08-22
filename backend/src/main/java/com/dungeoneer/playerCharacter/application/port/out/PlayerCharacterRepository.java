package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;

import java.util.Optional;
import java.util.UUID;

public interface PlayerCharacterRepository {
    PlayerCharacter save(PlayerCharacter character);
    Optional<PlayerCharacter> findById(UUID id);
}
