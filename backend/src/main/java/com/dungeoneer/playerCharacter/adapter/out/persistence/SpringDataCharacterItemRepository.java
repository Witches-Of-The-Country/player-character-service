package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCharacterItemRepository extends JpaRepository<CharacterItemJpaEntity, UUID> {
    List<CharacterItemJpaEntity> findByCharacterId(UUID characterId);
    Optional<CharacterItemJpaEntity> findByIdAndCharacterId(UUID id, UUID characterId);
    List<CharacterItemJpaEntity> findByCharacterIdAndItemId(UUID characterId, UUID itemId);
    void deleteByCharacterId(UUID characterId);
}
