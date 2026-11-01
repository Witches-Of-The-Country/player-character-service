package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataSpellRepository extends JpaRepository<SpellJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM player_character.spells s
        WHERE (CAST(:ruleset AS VARCHAR) IS NULL OR s.ruleset = CAST(:ruleset AS VARCHAR))
          AND (s.owner_id IS NULL OR (CAST(:ownerId AS UUID) IS NOT NULL AND s.owner_id = CAST(:ownerId AS UUID)))
          AND (CAST(:level AS INTEGER) IS NULL OR s.level = CAST(:level AS INTEGER))
          AND (CAST(:school AS VARCHAR) IS NULL OR LOWER(s.school) = LOWER(CAST(:school AS VARCHAR)))
          AND (CAST(:q AS VARCHAR) IS NULL OR CAST(:q AS VARCHAR) = '' OR to_tsvector('portuguese', coalesce(s.name, '') || ' ' || coalesce(s.name_en, '') || ' ' || coalesce(s.description, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR)))
          AND (CAST(:afterName AS VARCHAR) IS NULL OR s.name > CAST(:afterName AS VARCHAR) OR (s.name = CAST(:afterName AS VARCHAR) AND s.id > CAST(:afterId AS UUID)))
        ORDER BY s.name ASC, s.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<SpellJpaEntity> findSpellsWithCursor(
            @Param("ruleset") String ruleset,
            @Param("level") Integer level,
            @Param("school") String school,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit,
            @Param("ownerId") UUID ownerId
    );
}
