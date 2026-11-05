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
          AND (s.owner_id IS NULL OR CAST(:ownerId AS UUID) IS NULL OR s.owner_id = CAST(:ownerId AS UUID))
          AND (
            CAST(:levelsStr AS VARCHAR) IS NULL 
            OR CAST(:levelsStr AS VARCHAR) = '' 
            OR s.level = ANY(string_to_array(CAST(:levelsStr AS VARCHAR), ',')::int[])
          )
          AND (
            CAST(:schoolsStr AS VARCHAR) IS NULL 
            OR CAST(:schoolsStr AS VARCHAR) = '' 
            OR LOWER(s.school) = ANY(string_to_array(LOWER(CAST(:schoolsStr AS VARCHAR)), ','))
          )
          AND (
            CAST(:q AS VARCHAR) IS NULL
            OR TRIM(CAST(:q AS VARCHAR)) = ''
            OR translate(lower(coalesce(s.name, '') || ' ' || coalesce(s.name_en, '') || ' ' || coalesce(s.description, '') || ' ' || coalesce(s.school, '')), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn')
               LIKE '%' || replace(translate(lower(TRIM(CAST(:q AS VARCHAR))), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn'), ' ', '%') || '%'
            OR to_tsvector('portuguese', coalesce(s.name, '') || ' ' || coalesce(s.name_en, '') || ' ' || coalesce(s.description, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR))
          )
          AND (CAST(:afterName AS VARCHAR) IS NULL OR s.name > CAST(:afterName AS VARCHAR) OR (s.name = CAST(:afterName AS VARCHAR) AND s.id > CAST(:afterId AS UUID)))
        ORDER BY s.name ASC, s.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<SpellJpaEntity> findSpellsWithCursor(
            @Param("ruleset") String ruleset,
            @Param("levelsStr") String levelsStr,
            @Param("schoolsStr") String schoolsStr,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit,
            @Param("ownerId") UUID ownerId
    );
}
