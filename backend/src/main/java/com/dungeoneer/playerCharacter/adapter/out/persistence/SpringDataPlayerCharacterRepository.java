package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPlayerCharacterRepository extends JpaRepository<PlayerCharacterJpaEntity, UUID> {

    Optional<PlayerCharacterJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    List<PlayerCharacterJpaEntity> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
