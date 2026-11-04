-- Migration V21: Add Currency Columns to Characters
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

ALTER TABLE player_character.characters
    ADD COLUMN IF NOT EXISTS copper_pieces INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS silver_pieces INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS electrum_pieces INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS gold_pieces INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS platinum_pieces INT NOT NULL DEFAULT 0;
