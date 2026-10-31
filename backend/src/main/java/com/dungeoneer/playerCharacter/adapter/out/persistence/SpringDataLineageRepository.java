package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataLineageRepository extends JpaRepository<LineageJpaEntity, UUID> {

    List<LineageJpaEntity> findByRuleset(Ruleset ruleset);

    @Query(value = """
        SELECT * FROM player_character.lineages l
        WHERE (CAST(:ruleset AS VARCHAR) IS NULL OR l.ruleset = CAST(:ruleset AS VARCHAR))
          AND (CAST(:q AS VARCHAR) IS NULL OR CAST(:q AS VARCHAR) = '' OR to_tsvector('portuguese', coalesce(l.name, '') || ' ' || coalesce(l.description, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR)))
          AND (CAST(:afterName AS VARCHAR) IS NULL OR l.name > CAST(:afterName AS VARCHAR) OR (l.name = CAST(:afterName AS VARCHAR) AND l.id > CAST(:afterId AS UUID)))
        ORDER BY l.name ASC, l.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<LineageJpaEntity> findLineagesWithCursor(
            @Param("ruleset") String ruleset,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit
    );
}
