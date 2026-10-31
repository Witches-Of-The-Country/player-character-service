package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCharacterNoteRepository extends JpaRepository<CharacterNoteJpaEntity, UUID> {

    List<CharacterNoteJpaEntity> findAllByCharacterIdOrderByCreatedAtDesc(UUID characterId);
}
