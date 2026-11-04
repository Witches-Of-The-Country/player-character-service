package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSubclassFeatRepository extends JpaRepository<SubclassFeatJpaEntity, UUID> {
    List<SubclassFeatJpaEntity> findBySubclassIdOrderByUnlockLevelAsc(UUID subclassId);
    List<SubclassFeatJpaEntity> findBySubclassIdAndUnlockLevel(UUID subclassId, int unlockLevel);
}
