-- Migration V16: Add mechanical properties, class progression tables, and character inventory/feats
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. In player_character.items, add mechanical_properties JSONB
ALTER TABLE player_character.items
    ADD COLUMN IF NOT EXISTS mechanical_properties JSONB NOT NULL DEFAULT '{}'::jsonb;

CREATE INDEX IF NOT EXISTS idx_items_mechanical_properties
    ON player_character.items USING GIN (mechanical_properties);

-- 2. In player_character.feats, add feat_type and mechanical_effects JSONB
ALTER TABLE player_character.feats
    ADD COLUMN IF NOT EXISTS feat_type VARCHAR(30) NOT NULL DEFAULT 'GENERAL',
    ADD COLUMN IF NOT EXISTS mechanical_effects JSONB NOT NULL DEFAULT '{}'::jsonb;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'chk_feat_type' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.feats
            ADD CONSTRAINT chk_feat_type CHECK (feat_type IN ('GENERAL', 'ORIGIN', 'CLASS', 'SUBCLASS', 'LINEAGE'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_feats_feat_type ON player_character.feats (feat_type);

-- 3. In player_character.archetypes, add subclass_level
ALTER TABLE player_character.archetypes
    ADD COLUMN IF NOT EXISTS subclass_level INT NOT NULL DEFAULT 3;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'chk_archetype_subclass_level' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.archetypes
            ADD CONSTRAINT chk_archetype_subclass_level CHECK (subclass_level BETWEEN 1 AND 20);
    END IF;
END $$;

-- 4. Create player_character.subclasses table
CREATE TABLE IF NOT EXISTS player_character.subclasses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    archetype_id UUID NOT NULL REFERENCES player_character.archetypes(id) ON DELETE CASCADE,
    ruleset VARCHAR(20) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW')),
    slug VARCHAR(120) NOT NULL,
    name_en VARCHAR(120) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    source VARCHAR(50) NOT NULL DEFAULT 'SRD',
    owner_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_subclasses_ruleset_slug UNIQUE (ruleset, slug)
);

CREATE INDEX IF NOT EXISTS idx_subclasses_archetype ON player_character.subclasses(archetype_id);
CREATE INDEX IF NOT EXISTS idx_subclasses_ruleset_owner ON player_character.subclasses(ruleset, owner_id);

-- Foreign key on class_progressions.subclass_id to subclasses.id
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_class_progressions_subclass' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.class_progressions
            ADD CONSTRAINT fk_class_progressions_subclass
            FOREIGN KEY (subclass_id) REFERENCES player_character.subclasses(id) ON DELETE SET NULL;
    END IF;
END $$;

-- 5. Associative progression tables: archetype_feats and subclass_feats
CREATE TABLE IF NOT EXISTS player_character.archetype_feats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    archetype_id UUID NOT NULL REFERENCES player_character.archetypes(id) ON DELETE CASCADE,
    feat_id UUID NOT NULL REFERENCES player_character.feats(id) ON DELETE CASCADE,
    unlock_level INT NOT NULL CHECK (unlock_level BETWEEN 1 AND 20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_archetype_feat_level UNIQUE (archetype_id, feat_id, unlock_level)
);

CREATE INDEX IF NOT EXISTS idx_archetype_feats_lookup ON player_character.archetype_feats(archetype_id, unlock_level);

CREATE TABLE IF NOT EXISTS player_character.subclass_feats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subclass_id UUID NOT NULL REFERENCES player_character.subclasses(id) ON DELETE CASCADE,
    feat_id UUID NOT NULL REFERENCES player_character.feats(id) ON DELETE CASCADE,
    unlock_level INT NOT NULL CHECK (unlock_level BETWEEN 1 AND 20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_subclass_feat_level UNIQUE (subclass_id, feat_id, unlock_level)
);

CREATE INDEX IF NOT EXISTS idx_subclass_feats_lookup ON player_character.subclass_feats(subclass_id, unlock_level);

-- 6. In player_character.characters, add experience_points and campaign_id
ALTER TABLE player_character.characters
    ADD COLUMN IF NOT EXISTS experience_points INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS campaign_id UUID NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'chk_character_experience_points' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.characters
            ADD CONSTRAINT chk_character_experience_points CHECK (experience_points >= 0);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_characters_campaign_id ON player_character.characters(campaign_id);

-- 7. Create player_character.character_items (inventory)
CREATE TABLE IF NOT EXISTS player_character.character_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL REFERENCES player_character.characters(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES player_character.items(id) ON DELETE RESTRICT,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    is_equipped BOOLEAN NOT NULL DEFAULT FALSE,
    is_attuned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_character_items_character ON player_character.character_items(character_id);

-- 8. Create player_character.character_feats (unlocked feats and charges)
CREATE TABLE IF NOT EXISTS player_character.character_feats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL REFERENCES player_character.characters(id) ON DELETE CASCADE,
    feat_id UUID NOT NULL REFERENCES player_character.feats(id) ON DELETE RESTRICT,
    unlocked_at_level INT NOT NULL DEFAULT 1 CHECK (unlocked_at_level BETWEEN 1 AND 20),
    current_charges INT NULL,
    max_charges INT NULL,
    choices JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_character_feat UNIQUE (character_id, feat_id)
);

CREATE INDEX IF NOT EXISTS idx_character_feats_character ON player_character.character_feats(character_id);
