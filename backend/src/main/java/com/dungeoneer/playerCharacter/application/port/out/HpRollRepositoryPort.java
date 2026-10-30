package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.HpRoll;

import java.util.Optional;
import java.util.UUID;

public interface HpRollRepositoryPort {

    Optional<HpRoll> findUnusedByUserIdAndArchetypeIdAndTargetLevel(UUID userId, UUID archetypeId, int targetLevel);

    HpRoll save(HpRoll roll);

    void markAsUsed(UUID id);

    Optional<HpRoll> findById(UUID id);
}
