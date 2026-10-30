package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerCharacterRepositoryPort {

    PlayerCharacter save(PlayerCharacter character);

    Optional<PlayerCharacter> findByIdAndUserId(UUID id, UUID userId);

    List<PlayerCharacter> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
