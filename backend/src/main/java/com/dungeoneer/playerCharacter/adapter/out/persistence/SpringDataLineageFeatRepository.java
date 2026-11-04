package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataLineageFeatRepository extends JpaRepository<LineageFeatJpaEntity, LineageFeatId> {
    List<LineageFeatJpaEntity> findByLineageId(UUID lineageId);
    List<LineageFeatJpaEntity> findByLineageIdAndUnlockLevel(UUID lineageId, int unlockLevel);
}
