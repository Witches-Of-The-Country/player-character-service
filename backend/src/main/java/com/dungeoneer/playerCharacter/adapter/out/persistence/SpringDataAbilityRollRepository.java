package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAbilityRollRepository extends JpaRepository<AbilityRollJpaEntity, UUID> {

    Optional<AbilityRollJpaEntity> findFirstByUserIdAndUsedFalse(UUID userId);
}
