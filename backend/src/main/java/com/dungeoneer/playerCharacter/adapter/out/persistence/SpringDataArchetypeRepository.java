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
        WHERE (CAST(:ruleset AS VARCHAR) IS NULL OR a.ruleset = CAST(:ruleset AS VARCHAR))
          AND (a.owner_id IS NULL OR (CAST(:ownerId AS UUID) IS NOT NULL AND a.owner_id = CAST(:ownerId AS UUID)))
          AND (
            CAST(:q AS VARCHAR) IS NULL
            OR TRIM(CAST(:q AS VARCHAR)) = ''
            OR translate(lower(coalesce(a.name, '') || ' ' || coalesce(a.primary_ability, '')), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn')
               LIKE '%' || replace(translate(lower(TRIM(CAST(:q AS VARCHAR))), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn'), ' ', '%') || '%'
            OR to_tsvector('portuguese', coalesce(a.name, '') || ' ' || coalesce(a.primary_ability, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR))
          )
          AND (CAST(:afterName AS VARCHAR) IS NULL OR a.name > CAST(:afterName AS VARCHAR) OR (a.name = CAST(:afterName AS VARCHAR) AND a.id > CAST(:afterId AS UUID)))
        ORDER BY a.name ASC, a.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<ArchetypeJpaEntity> findArchetypesWithCursor(
            @Param("ruleset") String ruleset,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit,
            @Param("ownerId") UUID ownerId
    );
}
