package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataSpellRepository extends JpaRepository<SpellJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM player_character.spells s
        WHERE (:ruleset IS NULL OR s.ruleset = :#{#ruleset?.name()})
          AND (:level IS NULL OR s.level = :level)
          AND (:school IS NULL OR LOWER(s.school) = LOWER(:school))
          AND (:q IS NULL OR :q = '' OR to_tsvector('portuguese', coalesce(s.name, '') || ' ' || coalesce(s.name_en, '') || ' ' || coalesce(s.description, '')) @@ plainto_tsquery('portuguese', :q))
          AND (:afterName IS NULL OR s.name > :afterName OR (s.name = :afterName AND s.id > :afterId))
        ORDER BY s.name ASC, s.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<SpellJpaEntity> findSpellsWithCursor(
            @Param("ruleset") Ruleset ruleset,
            @Param("level") Integer level,
            @Param("school") String school,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit
    );
}
