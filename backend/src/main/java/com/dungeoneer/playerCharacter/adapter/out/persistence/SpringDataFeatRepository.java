package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataFeatRepository extends JpaRepository<FeatJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM player_character.feats f
        WHERE (CAST(:ruleset AS VARCHAR) IS NULL OR f.ruleset = CAST(:ruleset AS VARCHAR))
          AND (f.owner_id IS NULL OR (CAST(:ownerId AS UUID) IS NOT NULL AND f.owner_id = CAST(:ownerId AS UUID)))
          AND (CAST(:q AS VARCHAR) IS NULL OR CAST(:q AS VARCHAR) = '' OR to_tsvector('portuguese', coalesce(f.name, '') || ' ' || coalesce(f.name_en, '') || ' ' || coalesce(f.description, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR)))
          AND (CAST(:afterName AS VARCHAR) IS NULL OR f.name > CAST(:afterName AS VARCHAR) OR (f.name = CAST(:afterName AS VARCHAR) AND f.id > CAST(:afterId AS UUID)))
        ORDER BY f.name ASC, f.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<FeatJpaEntity> findFeatsWithCursor(
            @Param("ruleset") String ruleset,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit,
            @Param("ownerId") UUID ownerId
    );
}
