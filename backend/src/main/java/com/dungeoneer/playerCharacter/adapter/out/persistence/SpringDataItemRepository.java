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
            CAST(:categoriesStr AS VARCHAR) IS NULL
            OR CAST(:categoriesStr AS VARCHAR) = ''
            OR EXISTS (
              SELECT 1 FROM unnest(string_to_array(UPPER(CAST(:categoriesStr AS VARCHAR)), ',')) cat
              WHERE UPPER(i.item_category) = cat
                 OR UPPER(i.item_type) ILIKE CONCAT('%', cat, '%')
                 OR (cat IN ('EQUIPMENT', 'ADVENTURING_GEAR') AND (UPPER(i.item_category) IN ('EQUIPMENT', 'ADVENTURING_GEAR') OR UPPER(i.item_type) ILIKE '%equipamento%' OR UPPER(i.item_type) ILIKE '%ferramenta%' OR UPPER(i.item_type) ILIKE '%kit%'))
                 OR (cat IN ('POTION', 'CONSUMABLE') AND (UPPER(i.item_category) IN ('POTION', 'CONSUMABLE') OR UPPER(i.item_type) ILIKE '%poção%' OR UPPER(i.item_type) ILIKE '%potion%'))
                 OR (cat = 'WONDROUS' AND (UPPER(i.item_category) = 'WONDROUS' OR UPPER(i.item_type) ILIKE '%maravilhoso%' OR UPPER(i.item_type) ILIKE '%wondrous%'))
                 OR (cat = 'RING' AND (UPPER(i.item_category) = 'RING' OR UPPER(i.item_type) ILIKE '%anel%' OR UPPER(i.item_type) ILIKE '%ring%'))
                 OR (cat = 'SCROLL' AND (UPPER(i.item_category) = 'SCROLL' OR UPPER(i.item_type) ILIKE '%pergaminho%' OR UPPER(i.item_type) ILIKE '%scroll%'))
                 OR (cat = 'WAND' AND (UPPER(i.item_category) = 'WAND' OR UPPER(i.item_type) ILIKE '%varinha%' OR UPPER(i.item_type) ILIKE '%wand%'))
                 OR (cat = 'ROD' AND (UPPER(i.item_category) = 'ROD' OR UPPER(i.item_type) ILIKE '%bastão%' OR UPPER(i.item_type) ILIKE '%rod%'))
                 OR (cat = 'STAFF' AND (UPPER(i.item_category) = 'STAFF' OR UPPER(i.item_type) ILIKE '%cajado%' OR UPPER(i.item_type) ILIKE '%staff%'))
            )
          )
          AND (
            CAST(:raritiesStr AS VARCHAR) IS NULL
            OR CAST(:raritiesStr AS VARCHAR) = ''
            OR EXISTS (
              SELECT 1 FROM unnest(string_to_array(UPPER(CAST(:raritiesStr AS VARCHAR)), ',')) r
              WHERE (r IN ('COMMON', 'COMUM') AND (i.rarity IS NULL OR UPPER(i.rarity) IN ('NONE', 'COMMON', 'COMUM')))
                 OR (r IN ('UNCOMMON', 'INCOMUM') AND UPPER(i.rarity) IN ('UNCOMMON', 'INCOMUM'))
                 OR (r IN ('RARE', 'RARO') AND UPPER(i.rarity) IN ('RARE', 'RARO'))
                 OR (r IN ('VERY_RARE', 'MUITO RARO', 'MUITO_RARO') AND UPPER(i.rarity) IN ('VERY_RARE', 'MUITO RARO', 'MUITO_RARO'))
                 OR (r IN ('LEGENDARY', 'LENDÁRIO', 'LENDARIO') AND UPPER(i.rarity) IN ('LEGENDARY', 'LENDÁRIO', 'LENDARIO'))
                 OR (r IN ('ARTIFACT', 'ARTEFATO') AND UPPER(i.rarity) IN ('ARTIFACT', 'ARTEFATO'))
                 OR UPPER(i.rarity) = r
            )
          )
          AND (
            CAST(:q AS VARCHAR) IS NULL
            OR TRIM(CAST(:q AS VARCHAR)) = ''
            OR translate(lower(coalesce(i.name, '') || ' ' || coalesce(i.name_en, '') || ' ' || coalesce(i.description, '') || ' ' || coalesce(i.item_type, '') || ' ' || coalesce(i.item_category, '')), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn')
               LIKE '%' || replace(translate(lower(TRIM(CAST(:q AS VARCHAR))), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn'), ' ', '%') || '%'
            OR to_tsvector('portuguese', coalesce(i.name, '') || ' ' || coalesce(i.name_en, '') || ' ' || coalesce(i.description, '')) @@ plainto_tsquery('portuguese', CAST(:q AS VARCHAR))
          )
          AND (CAST(:afterName AS VARCHAR) IS NULL OR i.name > CAST(:afterName AS VARCHAR) OR (i.name = CAST(:afterName AS VARCHAR) AND i.id > CAST(:afterId AS UUID)))
        ORDER BY i.name ASC, i.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<ItemJpaEntity> findItemsWithCursor(
            @Param("ruleset") String ruleset,
            @Param("categoriesStr") String categoriesStr,
            @Param("raritiesStr") String raritiesStr,
            @Param("q") String q,
            @Param("afterName") String afterName,
            @Param("afterId") UUID afterId,
            @Param("limit") int limit,
            @Param("ownerId") UUID ownerId
    );
}
