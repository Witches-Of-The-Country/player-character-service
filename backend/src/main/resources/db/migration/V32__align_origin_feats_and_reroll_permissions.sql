-- Migration V32: Align Origin Feats, Backgrounds, and Ability Score Reroll Governance
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Ensure all SRD 2024 Origin Feats have feat_type = 'ORIGIN'
UPDATE player_character.feats
SET feat_type = 'ORIGIN',
    mechanical_effects = jsonb_set(
        COALESCE(mechanical_effects, '{}'::jsonb),
        '{featType}',
        '"ORIGIN"'::jsonb
    )
WHERE slug IN (
    'alert-2024',
    'crafter-2024',
    'healer-2024',
    'lucky-2024',
    'magic-initiate-2024',
    'musician-2024',
    'savage-attacker-2024',
    'skilled-2024',
    'tavern-brawler-2024',
    'tough-2024'
) AND ruleset = 'SRD_2024';

-- 2. Align origin_feat_name in backgrounds to match canonical feat names
UPDATE player_character.backgrounds
SET origin_feat_name = 'Iniciado em Magia'
WHERE name = 'Acolyte' AND ruleset = 'SRD_2024';

UPDATE player_character.backgrounds
SET origin_feat_name = 'Alerta'
WHERE name = 'Criminal' AND ruleset = 'SRD_2024';

UPDATE player_character.backgrounds
SET origin_feat_name = 'Vigoroso'
WHERE name = 'Folk Hero' AND ruleset = 'SRD_2024';

UPDATE player_character.backgrounds
SET origin_feat_name = 'Habilidoso'
WHERE name = 'Noble' AND ruleset = 'SRD_2024';

UPDATE player_character.backgrounds
SET origin_feat_name = 'Iniciado em Magia'
WHERE name = 'Sage' AND ruleset = 'SRD_2024';

UPDATE player_character.backgrounds
SET origin_feat_name = 'Atacante Selvagem'
WHERE name = 'Soldier' AND ruleset = 'SRD_2024';

-- 3. Add allow_ability_reroll column to characters table
ALTER TABLE player_character.characters
ADD COLUMN IF NOT EXISTS allow_ability_reroll BOOLEAN NOT NULL DEFAULT FALSE;
