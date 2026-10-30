-- Migration V4: Add character CRUD columns, class progression, and roll persistence tables

-- 1. Alter player_character.characters to add CRUD fields and enforce user_id NOT NULL
UPDATE player_character.characters
SET user_id = '00000000-0000-0000-0000-000000000000'::uuid
WHERE user_id IS NULL;

ALTER TABLE player_character.characters
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE player_character.characters
    ADD COLUMN IF NOT EXISTS notes TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS hp_calculation_method VARCHAR(15) NOT NULL DEFAULT 'MAX',
    ADD COLUMN IF NOT EXISTS has_pending_level_choices BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_characters_user_id ON player_character.characters(user_id);

-- 2. Create player_character.class_progressions table
CREATE TABLE IF NOT EXISTS player_character.class_progressions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL REFERENCES player_character.characters(id) ON DELETE CASCADE,
    archetype_id UUID NOT NULL REFERENCES player_character.archetypes(id),
    subclass_id UUID NULL,
    class_level INT NOT NULL CHECK (class_level >= 1 AND class_level <= 20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_class_progression_character_archetype UNIQUE (character_id, archetype_id)
);

CREATE INDEX IF NOT EXISTS idx_class_progressions_character_id ON player_character.class_progressions(character_id);

-- 3. Create player_character.ability_rolls table
CREATE TABLE IF NOT EXISTS player_character.ability_rolls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    rolled_scores INT[] NOT NULL,
    dice_breakdown JSONB NOT NULL DEFAULT '[]',
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_unused_ability_roll
    ON player_character.ability_rolls(user_id)
    WHERE used = FALSE;

-- 4. Create player_character.hp_rolls table
CREATE TABLE IF NOT EXISTS player_character.hp_rolls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    archetype_id UUID NOT NULL REFERENCES player_character.archetypes(id),
    target_level INT NOT NULL CHECK (target_level >= 2 AND target_level <= 20),
    rolled_hp_per_level INT[] NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_unused_hp_roll
    ON player_character.hp_rolls(user_id, archetype_id, target_level)
    WHERE used = FALSE;
