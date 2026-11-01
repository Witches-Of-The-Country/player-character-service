package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCharacterFeatRepository extends JpaRepository<CharacterFeatJpaEntity, UUID> {
    List<CharacterFeatJpaEntity> findByCharacterId(UUID characterId);
    Optional<CharacterFeatJpaEntity> findByIdAndCharacterId(UUID id, UUID characterId);
    void deleteByCharacterId(UUID characterId);
}
