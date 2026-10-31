package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataItemRepository extends JpaRepository<ItemJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM player_character.items i
        WHERE (:ruleset IS NULL OR i.ruleset = :#{#ruleset?.name()})
          AND (:itemCategory IS NULL OR i.item_category = :itemCategory)
          AND (:rarity IS NULL OR i.rarity = :rarity)
          AND (:q IS NULL OR :q = '' OR to_tsvector('portuguese', coalesce(i.name, '') || ' ' || coalesce(i.name_en, '') || ' ' || coalesce(i.description, '')) @@ plainto_tsquery('portuguese', :q))
          AND (:afterName IS NULL OR i.name > :afterName OR (i.name = :afterName AND i.id > :afterId))
        ORDER BY i.name ASC, i.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<ItemJpaEntity> findItemsWithCursor(
            @Param("ruleset") Ruleset ruleset,
            @Param("itemCategory") String itemCategory,
            @Param("rarity") String rarity,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit
    );
}
