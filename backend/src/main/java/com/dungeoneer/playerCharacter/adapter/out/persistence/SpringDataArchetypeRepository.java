package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataArchetypeRepository extends JpaRepository<ArchetypeJpaEntity, UUID> {

    List<ArchetypeJpaEntity> findByRuleset(Ruleset ruleset);
}
