package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataHpRollRepository extends JpaRepository<HpRollJpaEntity, UUID> {

    Optional<HpRollJpaEntity> findFirstByUserIdAndArchetypeIdAndTargetLevelAndUsedFalse(
            UUID userId,
            UUID archetypeId,
            int targetLevel
    );
}
