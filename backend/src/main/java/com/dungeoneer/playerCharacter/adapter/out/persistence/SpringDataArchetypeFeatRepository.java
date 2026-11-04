package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataArchetypeFeatRepository extends JpaRepository<ArchetypeFeatJpaEntity, UUID> {
    List<ArchetypeFeatJpaEntity> findByArchetypeIdOrderByUnlockLevelAsc(UUID archetypeId);
    List<ArchetypeFeatJpaEntity> findByArchetypeIdAndUnlockLevel(UUID archetypeId, int unlockLevel);
}
