CREATE TABLE IF NOT EXISTS characters (
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

CREATE TABLE IF NOT EXISTS ability_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL UNIQUE REFERENCES characters(id) ON DELETE CASCADE,
    strength INT NOT NULL CHECK (strength >= 1 AND strength <= 30),
    dexterity INT NOT NULL CHECK (dexterity >= 1 AND dexterity <= 30),
    constitution INT NOT NULL CHECK (constitution >= 1 AND constitution <= 30),
    intelligence INT NOT NULL CHECK (intelligence >= 1 AND intelligence <= 30),
    wisdom INT NOT NULL CHECK (wisdom >= 1 AND wisdom <= 30),
    charisma INT NOT NULL CHECK (charisma >= 1 AND charisma <= 30)
);
