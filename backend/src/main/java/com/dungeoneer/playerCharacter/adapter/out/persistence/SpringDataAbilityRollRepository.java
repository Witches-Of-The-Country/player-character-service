package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAbilityRollRepository extends JpaRepository<AbilityRollJpaEntity, UUID> {

    Optional<AbilityRollJpaEntity> findFirstByUserIdAndUsedFalse(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AbilityRollJpaEntity a SET a.used = true WHERE a.userId = :userId AND a.used = false")
    void markAllUnusedAsUsedForUser(@Param("userId") UUID userId);
}
