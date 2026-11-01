package com.dungeoneer.playerCharacter.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataItemRepository extends JpaRepository<ItemJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM player_character.items i
        WHERE (CAST(:ruleset AS VARCHAR) IS NULL OR i.ruleset = CAST(:ruleset AS VARCHAR))
          AND (i.owner_id IS NULL OR (CAST(:ownerId AS UUID) IS NOT NULL AND i.owner_id = CAST(:ownerId AS UUID)))
          AND (CAST(:itemCategory AS VARCHAR) IS NULL OR i.item_category = CAST(:itemCategory AS VARCHAR))
          AND (CAST(:rarity AS VARCHAR) IS NULL OR i.rarity = CAST(:rarity AS VARCHAR))
          AND (CAST(:q AS VARCHAR) IS NULL OR CAST(:q AS VARCHAR) = '' OR to_tsvector('portuguese', coalesce(i.name, '') || ' ' || coalesce(i.name_en, '') || ' ' || coalesce(i.description, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR)))
          AND (CAST(:afterName AS VARCHAR) IS NULL OR i.name > CAST(:afterName AS VARCHAR) OR (i.name = CAST(:afterName AS VARCHAR) AND i.id > CAST(:afterId AS UUID)))
        ORDER BY i.name ASC, i.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<ItemJpaEntity> findItemsWithCursor(
            @Param("ruleset") String ruleset,
            @Param("itemCategory") String itemCategory,
            @Param("rarity") String rarity,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit,
            @Param("ownerId") UUID ownerId
    );
}
