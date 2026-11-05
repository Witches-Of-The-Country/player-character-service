-- V30__add_equipment_slots_and_custom_actions.sql
-- Add equipment slots to character_items, migrate legacy equipped items, and create character_custom_actions table

-- 1. Add equipped_slot column to character_items
ALTER TABLE player_character.character_items
    ADD COLUMN IF NOT EXISTS equipped_slot VARCHAR(30) NULL;

-- 2. Create character_custom_actions table
CREATE TABLE IF NOT EXISTS player_character.character_custom_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL REFERENCES player_character.characters(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    action_type VARCHAR(30) NOT NULL DEFAULT 'ACTION',
    attack_bonus INT NULL,
    damage_formula VARCHAR(100) NULL,
    damage_type VARCHAR(30) NULL,
    description TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_character_custom_actions_character_id
    ON player_character.character_custom_actions(character_id);

-- 3. Resolve duplicate equipped body armors and assign CHEST (highest AC remains equipped, others unequipped)
WITH ranked_armors AS (
    SELECT ci.id,
           ci.character_id,
           ROW_NUMBER() OVER (
               PARTITION BY ci.character_id
               ORDER BY 
                   COALESCE((i.mechanical_properties->>'baseAc')::int, 0) DESC,
                   ci.created_at ASC
           ) as rn
    FROM player_character.character_items ci
    JOIN player_character.items i ON ci.item_id = i.id
    WHERE ci.is_equipped = TRUE
      AND i.item_category = 'ARMOR'
      AND COALESCE(i.mechanical_properties->>'armorCategory', '') != 'SHIELD'
      AND i.name NOT ILIKE '%escudo%'
      AND i.name NOT ILIKE '%shield%'
)
UPDATE player_character.character_items ci
SET equipped_slot = CASE WHEN ra.rn = 1 THEN 'CHEST' ELSE NULL END,
    is_equipped = CASE WHEN ra.rn = 1 THEN TRUE ELSE FALSE END
FROM ranked_armors ra
WHERE ci.id = ra.id;

-- 4. Assign LEFT_HAND to shields
UPDATE player_character.character_items ci
SET equipped_slot = 'LEFT_HAND'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND (
      i.item_category = 'ARMOR' 
      AND (
          COALESCE(i.mechanical_properties->>'armorCategory', '') = 'SHIELD'
          OR i.name ILIKE '%escudo%'
          OR i.name ILIKE '%shield%'
      )
  );

-- 5. Assign weapons (RIGHT_HAND for 1st, LEFT_HAND for 2nd, unequip 3rd+)
WITH ranked_weapons AS (
    SELECT ci.id,
           ROW_NUMBER() OVER (
               PARTITION BY ci.character_id
               ORDER BY ci.created_at ASC
           ) as rn
    FROM player_character.character_items ci
    JOIN player_character.items i ON ci.item_id = i.id
    WHERE ci.is_equipped = TRUE
      AND i.item_category = 'WEAPON'
)
UPDATE player_character.character_items ci
SET equipped_slot = CASE 
        WHEN rw.rn = 1 THEN 'RIGHT_HAND'
        WHEN rw.rn = 2 THEN 'LEFT_HAND'
        ELSE NULL 
    END,
    is_equipped = CASE 
        WHEN rw.rn <= 2 THEN TRUE 
        ELSE FALSE 
    END
FROM ranked_weapons rw
WHERE ci.id = rw.id;

-- 6. Assign rings (RING_1 for 1st, RING_2 for 2nd, unequip 3rd+)
WITH ranked_rings AS (
    SELECT ci.id,
           ROW_NUMBER() OVER (
               PARTITION BY ci.character_id
               ORDER BY ci.created_at ASC
           ) as rn
    FROM player_character.character_items ci
    JOIN player_character.items i ON ci.item_id = i.id
    WHERE ci.is_equipped = TRUE
      AND (i.item_category = 'RING' OR i.name ILIKE '%anel%' OR i.name ILIKE '%ring%')
)
UPDATE player_character.character_items ci
SET equipped_slot = CASE 
        WHEN rr.rn = 1 THEN 'RING_1'
        WHEN rr.rn = 2 THEN 'RING_2'
        ELSE NULL 
    END,
    is_equipped = CASE 
        WHEN rr.rn <= 2 THEN TRUE 
        ELSE FALSE 
    END
FROM ranked_rings rr
WHERE ci.id = rr.id;

-- 7. Assign accessory slots by item name keywords
UPDATE player_character.character_items ci
SET equipped_slot = 'BOOTS'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND ci.equipped_slot IS NULL
  AND (i.name ILIKE '%bota%' OR i.name ILIKE '%boot%');

UPDATE player_character.character_items ci
SET equipped_slot = 'GLOVES'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND ci.equipped_slot IS NULL
  AND (i.name ILIKE '%luva%' OR i.name ILIKE '%manopla%' OR i.name ILIKE '%gauntlet%' OR i.name ILIKE '%bracer%');

UPDATE player_character.character_items ci
SET equipped_slot = 'HELMET'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND ci.equipped_slot IS NULL
  AND (i.name ILIKE '%capacete%' OR i.name ILIKE '%tiara%' OR i.name ILIKE '%helm%' OR i.name ILIKE '%diadema%' OR i.name ILIKE '%headband%');

UPDATE player_character.character_items ci
SET equipped_slot = 'PANTS'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND ci.equipped_slot IS NULL
  AND (i.name ILIKE '%calça%' OR i.name ILIKE '%perneira%');

UPDATE player_character.character_items ci
SET equipped_slot = 'CLOAK'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND ci.equipped_slot IS NULL
  AND (i.name ILIKE '%capa%' OR i.name ILIKE '%manto%' OR i.name ILIKE '%cloak%');

UPDATE player_character.character_items ci
SET equipped_slot = 'NECKLACE'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND ci.equipped_slot IS NULL
  AND (i.name ILIKE '%colar%' OR i.name ILIKE '%amuleto%' OR i.name ILIKE '%medalhão%' OR i.name ILIKE '%amulet%' OR i.name ILIKE '%necklace%' OR i.name ILIKE '%periapt%');

-- 8. Fallback: Any remaining equipped item without a slot defaults to CHEST
UPDATE player_character.character_items
SET equipped_slot = 'CHEST'
WHERE is_equipped = TRUE AND equipped_slot IS NULL;
