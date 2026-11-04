-- Migration V22: Add Character Avatar URL Column
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

ALTER TABLE player_character.characters
    ADD COLUMN IF NOT EXISTS avatar_url TEXT;
