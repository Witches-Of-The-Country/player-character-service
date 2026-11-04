-- V23: Allow target_level = 1 for multiclass HP rolls
CREATE SCHEMA IF NOT EXISTS player_character;

ALTER TABLE player_character.hp_rolls
    DROP CONSTRAINT IF EXISTS hp_rolls_target_level_check;

ALTER TABLE player_character.hp_rolls
    ADD CONSTRAINT hp_rolls_target_level_check
    CHECK (target_level >= 1 AND target_level <= 20);
