-- Migration V19: Add spell mechanics, archetype preparation mode, and lineage feats join table
ALTER TABLE player_character.spells
    ADD COLUMN IF NOT EXISTS mechanical_properties JSONB NOT NULL DEFAULT '{}'::jsonb;

ALTER TABLE player_character.archetypes
    ADD COLUMN IF NOT EXISTS spell_preparation_type VARCHAR(20) NOT NULL DEFAULT 'NONE';

CREATE TABLE IF NOT EXISTS player_character.lineage_feats (
    lineage_id UUID NOT NULL REFERENCES player_character.lineages(id) ON DELETE CASCADE,
    feat_id UUID NOT NULL REFERENCES player_character.feats(id) ON DELETE CASCADE,
    unlock_level INT NOT NULL DEFAULT 1,
    PRIMARY KEY (lineage_id, feat_id)
);

CREATE INDEX IF NOT EXISTS idx_lineage_feats_lineage_id ON player_character.lineage_feats(lineage_id);
