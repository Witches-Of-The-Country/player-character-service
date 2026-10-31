ALTER TABLE player_character.characters
    ADD COLUMN IF NOT EXISTS skill_proficiencies VARCHAR[] NOT NULL DEFAULT '{}';
