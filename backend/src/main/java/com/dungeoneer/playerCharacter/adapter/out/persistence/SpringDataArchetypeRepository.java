package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataArchetypeRepository extends JpaRepository<ArchetypeJpaEntity, UUID> {

    List<ArchetypeJpaEntity> findByRuleset(Ruleset ruleset);

    @Query(value = """
        SELECT * FROM player_character.archetypes a
        WHERE (:ruleset IS NULL OR a.ruleset = :#{#ruleset?.name()})
          AND (:q IS NULL OR :q = '' OR to_tsvector('portuguese', coalesce(a.name, '') || ' ' || coalesce(a.primary_ability, '')) @@ plainto_tsquery('portuguese', :q))
          AND (:afterName IS NULL OR a.name > :afterName OR (a.name = :afterName AND a.id > :afterId))
        ORDER BY a.name ASC, a.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<ArchetypeJpaEntity> findArchetypesWithCursor(
            @Param("ruleset") Ruleset ruleset,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit
    );
}
