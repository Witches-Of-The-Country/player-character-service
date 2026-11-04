package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;

import java.util.Optional;
import java.util.UUID;

public interface AbilityRollRepositoryPort {

    Optional<AbilityRoll> findUnusedByUserId(UUID userId);

    AbilityRoll save(AbilityRoll roll);

    void markAsUsed(UUID id);

    Optional<AbilityRoll> findById(UUID id);
}
