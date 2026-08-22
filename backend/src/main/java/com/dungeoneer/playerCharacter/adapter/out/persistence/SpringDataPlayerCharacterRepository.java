package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataPlayerCharacterRepository extends JpaRepository<PlayerCharacterJpaEntity, UUID> {
}
