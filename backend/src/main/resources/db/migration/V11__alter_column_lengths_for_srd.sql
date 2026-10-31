-- Migration V11: Alter Spells and Reference Column Types to TEXT to Prevent Length Overflows
ALTER TABLE player_character.spells 
    ALTER COLUMN components TYPE TEXT,
    ALTER COLUMN casting_time TYPE TEXT,
    ALTER COLUMN range_area TYPE TEXT,
    ALTER COLUMN duration TYPE TEXT;

ALTER TABLE player_character.feats
    ALTER COLUMN prerequisites TYPE TEXT;

ALTER TABLE player_character.items
    ALTER COLUMN cost TYPE TEXT,
    ALTER COLUMN weight TYPE TEXT,
    ALTER COLUMN item_type TYPE TEXT;
