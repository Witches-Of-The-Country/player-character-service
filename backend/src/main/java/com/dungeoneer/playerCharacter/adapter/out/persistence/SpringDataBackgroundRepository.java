package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataBackgroundRepository extends JpaRepository<BackgroundJpaEntity, UUID> {

    List<BackgroundJpaEntity> findByRuleset(Ruleset ruleset);
}
