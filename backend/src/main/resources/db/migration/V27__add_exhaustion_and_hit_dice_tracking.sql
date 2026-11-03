-- Migration V27: Add Exhaustion and Hit Dice Tracking
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. In player_character.characters, add exhaustion column (0 to 6)
ALTER TABLE player_character.characters
    ADD COLUMN IF NOT EXISTS exhaustion INT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'chk_character_exhaustion' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.characters
            ADD CONSTRAINT chk_character_exhaustion CHECK (exhaustion >= 0 AND exhaustion <= 6);
    END IF;
END $$;

-- 2. In player_character.class_progressions, add hit_dice_spent column (>= 0)
ALTER TABLE player_character.class_progressions
    ADD COLUMN IF NOT EXISTS hit_dice_spent INT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'chk_class_progressions_hit_dice_spent' AND table_schema = 'player_character'
    ) THEN
        ALTER TABLE player_character.class_progressions
            ADD CONSTRAINT chk_class_progressions_hit_dice_spent CHECK (hit_dice_spent >= 0);
    END IF;
END $$;
