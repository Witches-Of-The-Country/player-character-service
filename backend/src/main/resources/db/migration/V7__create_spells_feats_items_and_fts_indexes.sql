-- Migration V7: Create tables for spells, feats, items and configure Full-Text Search GIN indexes
CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Spells Table
CREATE TABLE IF NOT EXISTS player_character.spells (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruleset VARCHAR(20) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW')),
    slug VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    name VARCHAR(150) NOT NULL,
    level INT NOT NULL CHECK (level >= 0 AND level <= 9),
    school VARCHAR(50) NOT NULL,
    casting_time VARCHAR(100),
    range_area VARCHAR(150),
    components VARCHAR(255),
    duration VARCHAR(150),
    description TEXT NOT NULL,
    source VARCHAR(20) NOT NULL DEFAULT 'SRD',
    owner_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_spells_ruleset_slug UNIQUE (ruleset, slug)
);

-- 2. Feats Table
CREATE TABLE IF NOT EXISTS player_character.feats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruleset VARCHAR(20) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW')),
    slug VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    name VARCHAR(150) NOT NULL,
    prerequisites VARCHAR(255),
    description TEXT NOT NULL,
    source VARCHAR(20) NOT NULL DEFAULT 'SRD',
    owner_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_feats_ruleset_slug UNIQUE (ruleset, slug)
);

-- 3. Items Table
CREATE TABLE IF NOT EXISTS player_character.items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruleset VARCHAR(20) NOT NULL CHECK (ruleset IN ('SRD_2014', 'SRD_2024', 'HOMEBREW')),
    slug VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    name VARCHAR(150) NOT NULL,
    item_category VARCHAR(30) NOT NULL, -- EQUIPMENT, WEAPON, ARMOR, MAGIC
    item_type VARCHAR(50) NOT NULL,
    rarity VARCHAR(30) NOT NULL DEFAULT 'NONE', -- COMMON, UNCOMMON, RARE, VERY_RARE, LEGENDARY, ARTIFACT, NONE
    attunement BOOLEAN NOT NULL DEFAULT FALSE,
    cost VARCHAR(50),
    weight VARCHAR(30),
    description TEXT NOT NULL,
    source VARCHAR(20) NOT NULL DEFAULT 'SRD',
    owner_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_items_ruleset_slug UNIQUE (ruleset, slug)
);

-- B-Tree Compound Indexes for Cursor Navigation (ordered by name ASC, id ASC)
CREATE INDEX IF NOT EXISTS idx_spells_cursor ON player_character.spells (ruleset, name ASC, id ASC);
CREATE INDEX IF NOT EXISTS idx_feats_cursor ON player_character.feats (ruleset, name ASC, id ASC);
CREATE INDEX IF NOT EXISTS idx_items_cursor ON player_character.items (ruleset, name ASC, id ASC);
CREATE INDEX IF NOT EXISTS idx_archetypes_cursor ON player_character.archetypes (ruleset, name ASC, id ASC);
CREATE INDEX IF NOT EXISTS idx_lineages_cursor ON player_character.lineages (ruleset, name ASC, id ASC);

-- B-Tree Filter Indexes
CREATE INDEX IF NOT EXISTS idx_spells_level_school ON player_character.spells (ruleset, level, school);
CREATE INDEX IF NOT EXISTS idx_items_category_rarity ON player_character.items (ruleset, item_category, rarity);

-- PostgreSQL Full-Text Search GIN Indexes
-- Combines name, name_en, and description into a Portuguese tsvector
CREATE INDEX IF NOT EXISTS idx_spells_fts ON player_character.spells USING GIN (
    to_tsvector('portuguese', coalesce(name, '') || ' ' || coalesce(name_en, '') || ' ' || coalesce(description, ''))
);

CREATE INDEX IF NOT EXISTS idx_feats_fts ON player_character.feats USING GIN (
    to_tsvector('portuguese', coalesce(name, '') || ' ' || coalesce(name_en, '') || ' ' || coalesce(description, ''))
);

CREATE INDEX IF NOT EXISTS idx_items_fts ON player_character.items USING GIN (
    to_tsvector('portuguese', coalesce(name, '') || ' ' || coalesce(name_en, '') || ' ' || coalesce(description, ''))
);

CREATE INDEX IF NOT EXISTS idx_archetypes_fts ON player_character.archetypes USING GIN (
    to_tsvector('portuguese', coalesce(name, '') || ' ' || coalesce(primary_ability, ''))
);

CREATE INDEX IF NOT EXISTS idx_lineages_fts ON player_character.lineages USING GIN (
    to_tsvector('portuguese', coalesce(name, '') || ' ' || coalesce(description, ''))
);
