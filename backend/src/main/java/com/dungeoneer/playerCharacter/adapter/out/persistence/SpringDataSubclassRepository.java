package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSubclassRepository extends JpaRepository<SubclassJpaEntity, UUID> {

    List<SubclassJpaEntity> findByArchetypeIdAndRuleset(UUID archetypeId, Ruleset ruleset);

    @Query("SELECT s FROM SubclassJpaEntity s WHERE s.archetypeId = :archetypeId AND (s.ruleset = :ruleset OR (s.ruleset = com.dungeoneer.playerCharacter.domain.model.Ruleset.HOMEBREW AND (s.ownerId IS NULL OR s.ownerId = :ownerId)))")
    List<SubclassJpaEntity> findAccessibleSubclasses(
            @Param("archetypeId") UUID archetypeId,
            @Param("ruleset") Ruleset ruleset,
            @Param("ownerId") UUID ownerId
    );

    Optional<SubclassJpaEntity> findByRulesetAndSlug(Ruleset ruleset, String slug);
}
