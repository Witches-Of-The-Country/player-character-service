-- Migration V2: Align schema to player_character and create reference tables
CREATE SCHEMA IF NOT EXISTS player_character;

-- Move existing V1 tables from public to player_character if they exist in public
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'ability_scores') THEN
        ALTER TABLE public.ability_scores SET SCHEMA player_character;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'characters') THEN
        ALTER TABLE public.characters SET SCHEMA player_character;
    END IF;
END $$;

-- Ensure tables exist in player_character even if V1 was baselined without running
CREATE TABLE IF NOT EXISTS player_character.characters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    level INT NOT NULL DEFAULT 1 CHECK (level >= 1 AND level <= 20),
    hit_points_current INT NOT NULL,
    hit_points_max INT NOT NULL,
    hit_points_temp INT NOT NULL DEFAULT 0,
    armor_class INT NOT NULL DEFAULT 10,
    initiative_bonus INT NOT NULL DEFAULT 0,
    speed INT NOT NULL DEFAULT 30,
    proficiency_bonus INT NOT NULL DEFAULT 2,
    ruleset VARCHAR(10) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024')),
    ability_score_method VARCHAR(20) NOT NULL,
    lineage_id UUID NOT NULL,
    background_id UUID NOT NULL,
    user_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS player_character.ability_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL UNIQUE REFERENCES player_character.characters(id) ON DELETE CASCADE,
    strength INT NOT NULL CHECK (strength >= 1 AND strength <= 30),
    dexterity INT NOT NULL CHECK (dexterity >= 1 AND dexterity <= 30),
    constitution INT NOT NULL CHECK (constitution >= 1 AND constitution <= 30),
    intelligence INT NOT NULL CHECK (intelligence >= 1 AND intelligence <= 30),
    wisdom INT NOT NULL CHECK (wisdom >= 1 AND wisdom <= 30),
    charisma INT NOT NULL CHECK (charisma >= 1 AND charisma <= 30)
);

-- 1. Archetypes (Classes)
CREATE TABLE IF NOT EXISTS player_character.archetypes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    hit_dice VARCHAR(3) NOT NULL,
    primary_ability VARCHAR(15) NOT NULL,
    saving_throw_proficiencies VARCHAR[] NOT NULL,
    armor_proficiencies VARCHAR[],
    weapon_proficiencies VARCHAR[],
    spellcasting_type VARCHAR(15) NOT NULL,
    spellcasting_ability VARCHAR(15),
    ruleset VARCHAR(10) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024')),
    source VARCHAR(10) NOT NULL DEFAULT 'SRD',
    owner_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_archetype_name_ruleset_source UNIQUE (name, ruleset, source)
);

-- 2. Lineages (Races)
CREATE TABLE IF NOT EXISTS player_character.lineages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    description TEXT,
    speed INT NOT NULL DEFAULT 30,
    size VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    ability_bonuses JSONB NOT NULL DEFAULT '{}',
    traits_summary TEXT[],
    ruleset VARCHAR(10) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024')),
    source VARCHAR(10) NOT NULL DEFAULT 'SRD',
    owner_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_lineage_name_ruleset_source UNIQUE (name, ruleset, source)
);

-- 3. Backgrounds
CREATE TABLE IF NOT EXISTS player_character.backgrounds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    description TEXT,
    skill_proficiencies VARCHAR[] NOT NULL,
    tool_proficiencies VARCHAR[],
    languages VARCHAR[],
    allowed_ability_bonuses VARCHAR[],
    origin_feat_name VARCHAR(100),
    origin_feat_description TEXT,
    ruleset VARCHAR(10) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024')),
    source VARCHAR(10) NOT NULL DEFAULT 'SRD',
    owner_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_background_name_ruleset_source UNIQUE (name, ruleset, source)
);

-- 4. Foreign key constraints on player_character.characters
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_characters_lineage' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.characters
        ADD CONSTRAINT fk_characters_lineage
        FOREIGN KEY (lineage_id) REFERENCES player_character.lineages(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_characters_background' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.characters
        ADD CONSTRAINT fk_characters_background
        FOREIGN KEY (background_id) REFERENCES player_character.backgrounds(id);
    END IF;
END $$;
