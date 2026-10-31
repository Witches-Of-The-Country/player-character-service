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
        WHERE (:ruleset IS NULL OR l.ruleset = :#{#ruleset?.name()})
          AND (:q IS NULL OR :q = '' OR to_tsvector('portuguese', coalesce(l.name, '') || ' ' || coalesce(l.description, '')) @@ plainto_tsquery('portuguese', :q))
          AND (:afterName IS NULL OR l.name > :afterName OR (l.name = :afterName AND l.id > :afterId))
        ORDER BY l.name ASC, l.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<LineageJpaEntity> findLineagesWithCursor(
            @Param("ruleset") Ruleset ruleset,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit
    );
}
