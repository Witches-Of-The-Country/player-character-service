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
          AND (i.owner_id IS NULL OR CAST(:ownerId AS UUID) IS NULL OR i.owner_id = CAST(:ownerId AS UUID))
          AND (
            CAST(:itemCategory AS VARCHAR) IS NULL 
            OR UPPER(i.item_category) = UPPER(CAST(:itemCategory AS VARCHAR))
            OR UPPER(i.item_type) ILIKE CONCAT('%', UPPER(CAST(:itemCategory AS VARCHAR)), '%')
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) IN ('EQUIPMENT', 'ADVENTURING_GEAR') AND (UPPER(i.item_category) IN ('EQUIPMENT', 'ADVENTURING_GEAR') OR UPPER(i.item_type) ILIKE '%equipamento%' OR UPPER(i.item_type) ILIKE '%ferramenta%' OR UPPER(i.item_type) ILIKE '%kit%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) IN ('POTION', 'CONSUMABLE') AND (UPPER(i.item_category) IN ('POTION', 'CONSUMABLE') OR UPPER(i.item_type) ILIKE '%poção%' OR UPPER(i.item_type) ILIKE '%potion%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) = 'WONDROUS' AND (UPPER(i.item_category) = 'WONDROUS' OR UPPER(i.item_type) ILIKE '%maravilhoso%' OR UPPER(i.item_type) ILIKE '%wondrous%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) = 'RING' AND (UPPER(i.item_category) = 'RING' OR UPPER(i.item_type) ILIKE '%anel%' OR UPPER(i.item_type) ILIKE '%ring%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) = 'SCROLL' AND (UPPER(i.item_category) = 'SCROLL' OR UPPER(i.item_type) ILIKE '%pergaminho%' OR UPPER(i.item_type) ILIKE '%scroll%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) = 'WAND' AND (UPPER(i.item_category) = 'WAND' OR UPPER(i.item_type) ILIKE '%varinha%' OR UPPER(i.item_type) ILIKE '%wand%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) = 'ROD' AND (UPPER(i.item_category) = 'ROD' OR UPPER(i.item_type) ILIKE '%bastão%' OR UPPER(i.item_type) ILIKE '%rod%'))
            OR (UPPER(CAST(:itemCategory AS VARCHAR)) = 'STAFF' AND (UPPER(i.item_category) = 'STAFF' OR UPPER(i.item_type) ILIKE '%cajado%' OR UPPER(i.item_type) ILIKE '%staff%'))
          )
          AND (
            CAST(:rarity AS VARCHAR) IS NULL
            OR UPPER(i.rarity) = UPPER(CAST(:rarity AS VARCHAR))
            OR (UPPER(CAST(:rarity AS VARCHAR)) IN ('COMMON', 'COMUM') AND (i.rarity IS NULL OR UPPER(i.rarity) IN ('NONE', 'COMMON', 'COMUM')))
            OR (UPPER(CAST(:rarity AS VARCHAR)) IN ('UNCOMMON', 'INCOMUM') AND UPPER(i.rarity) IN ('UNCOMMON', 'INCOMUM'))
            OR (UPPER(CAST(:rarity AS VARCHAR)) IN ('RARE', 'RARO') AND UPPER(i.rarity) IN ('RARE', 'RARO'))
            OR (UPPER(CAST(:rarity AS VARCHAR)) IN ('VERY_RARE', 'MUITO RARO', 'MUITO_RARO') AND UPPER(i.rarity) IN ('VERY_RARE', 'MUITO RARO', 'MUITO_RARO'))
            OR (UPPER(CAST(:rarity AS VARCHAR)) IN ('LEGENDARY', 'LENDÁRIO', 'LENDARIO') AND UPPER(i.rarity) IN ('LEGENDARY', 'LENDÁRIO', 'LENDARIO'))
            OR (UPPER(CAST(:rarity AS VARCHAR)) IN ('ARTIFACT', 'ARTEFATO') AND UPPER(i.rarity) IN ('ARTIFACT', 'ARTEFATO'))
          )
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
