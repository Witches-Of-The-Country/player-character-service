-- V31__add_trinket_equipment_slot.sql
-- Migrates existing equipped belts, sashes, talismans, charms, and trinkets to TRINKET slot.

UPDATE player_character.character_items ci
SET equipped_slot = 'TRINKET'
FROM player_character.items i
WHERE ci.item_id = i.id
  AND ci.is_equipped = TRUE
  AND (ci.equipped_slot = 'CHEST' OR ci.equipped_slot IS NULL)
  AND (
    i.name ILIKE '%cinto%' OR i.name ILIKE '%cinturão%' OR i.name ILIKE '%cinturao%' OR
    i.name ILIKE '%belt%' OR i.name ILIKE '%girdle%' OR i.name ILIKE '%charm%' OR
    i.name ILIKE '%talismã%' OR i.name ILIKE '%talisma%' OR i.name ILIKE '%talisman%' OR
    i.name ILIKE '%trinket%' OR i.name ILIKE '%faixa%' OR i.name ILIKE '%berloque%'
  );
