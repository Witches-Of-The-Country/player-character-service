-- Migration V5: Create character_notes table and migrate legacy notes

CREATE TABLE IF NOT EXISTS player_character.character_notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    character_id UUID NOT NULL REFERENCES player_character.characters(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_character_notes_character_id ON player_character.character_notes(character_id);

-- Migrate existing non-empty notes from player_character.characters
INSERT INTO player_character.character_notes (character_id, title, body, created_at)
SELECT id, 'Nota Inicial', notes, NOW()
FROM player_character.characters
WHERE notes IS NOT NULL AND TRIM(notes) != '';
