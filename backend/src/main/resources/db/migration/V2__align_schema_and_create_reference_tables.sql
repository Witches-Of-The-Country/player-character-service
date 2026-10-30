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
