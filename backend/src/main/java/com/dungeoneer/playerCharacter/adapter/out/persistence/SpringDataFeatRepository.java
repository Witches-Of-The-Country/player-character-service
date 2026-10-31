package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataFeatRepository extends JpaRepository<FeatJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM player_character.feats f
        WHERE (:ruleset IS NULL OR f.ruleset = :#{#ruleset?.name()})
          AND (:q IS NULL OR :q = '' OR to_tsvector('portuguese', coalesce(f.name, '') || ' ' || coalesce(f.name_en, '') || ' ' || coalesce(f.description, '')) @@ plainto_tsquery('portuguese', :q))
          AND (:afterName IS NULL OR f.name > :afterName OR (f.name = :afterName AND f.id > :afterId))
        ORDER BY f.name ASC, f.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<FeatJpaEntity> findFeatsWithCursor(
            @Param("ruleset") Ruleset ruleset,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit
    );
}
