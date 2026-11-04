-- Migration V15: Enable HOMEBREW ruleset and owner indexes
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Update ruleset CHECK constraint to include 'HOMEBREW' on archetypes, lineages, backgrounds
ALTER TABLE player_character.archetypes DROP CONSTRAINT IF EXISTS archetypes_ruleset_check;
ALTER TABLE player_character.archetypes DROP CONSTRAINT IF EXISTS chk_archetype_ruleset;
ALTER TABLE player_character.archetypes ADD CONSTRAINT chk_archetype_ruleset CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW'));

ALTER TABLE player_character.lineages DROP CONSTRAINT IF EXISTS lineages_ruleset_check;
ALTER TABLE player_character.lineages DROP CONSTRAINT IF EXISTS chk_lineage_ruleset;
ALTER TABLE player_character.lineages ADD CONSTRAINT chk_lineage_ruleset CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW'));

ALTER TABLE player_character.backgrounds DROP CONSTRAINT IF EXISTS backgrounds_ruleset_check;
ALTER TABLE player_character.backgrounds DROP CONSTRAINT IF EXISTS chk_background_ruleset;
ALTER TABLE player_character.backgrounds ADD CONSTRAINT chk_background_ruleset CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW'));

-- 2. Ensure composite indexes on (ruleset, owner_id) for optimal visibility filtering
CREATE INDEX IF NOT EXISTS idx_spells_ruleset_owner ON player_character.spells (ruleset, owner_id);
CREATE INDEX IF NOT EXISTS idx_feats_ruleset_owner ON player_character.feats (ruleset, owner_id);
CREATE INDEX IF NOT EXISTS idx_items_ruleset_owner ON player_character.items (ruleset, owner_id);
CREATE INDEX IF NOT EXISTS idx_archetypes_ruleset_owner ON player_character.archetypes (ruleset, owner_id);
CREATE INDEX IF NOT EXISTS idx_lineages_ruleset_owner ON player_character.lineages (ruleset, owner_id);
CREATE INDEX IF NOT EXISTS idx_backgrounds_ruleset_owner ON player_character.backgrounds (ruleset, owner_id);
