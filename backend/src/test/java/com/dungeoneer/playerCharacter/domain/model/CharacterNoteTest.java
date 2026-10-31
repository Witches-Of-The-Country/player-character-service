package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CharacterNoteTest {

    @Test
    @DisplayName("Should create character note successfully with valid arguments")
    void shouldCreateCharacterNoteSuccessfully() {
        UUID charId = UUID.randomUUID();
        CharacterNote note = CharacterNote.createNew(charId, "Mission Log", "Met with the guild master.");

        assertNotNull(note.getId());
        assertEquals(charId, note.getCharacterId());
        assertEquals("Mission Log", note.getTitle());
        assertEquals("Met with the guild master.", note.getBody());
        assertNotNull(note.getCreatedAt());
    }

    @Test
    @DisplayName("Should trim whitespace on title and body")
    void shouldTrimTitleAndBody() {
        UUID charId = UUID.randomUUID();
        CharacterNote note = new CharacterNote(UUID.randomUUID(), charId, "  Important Clue  ", "  Follow the raven.  ", Instant.now());

        assertEquals("Important Clue", note.getTitle());
        assertEquals("Follow the raven.", note.getBody());
    }

    @Test
    @DisplayName("Should throw exception when characterId is null")
    void shouldThrowExceptionWhenCharacterIdIsNull() {
        CharacterValidationException ex = assertThrows(
                CharacterValidationException.class,
                () -> CharacterNote.createNew(null, "Title", "Body")
        );
        assertTrue(ex.getMessage().contains("CharacterId cannot be null"));
    }

    @Test
    @DisplayName("Should throw exception when title is null or blank")
    void shouldThrowExceptionWhenTitleIsNullOrEmpty() {
        UUID charId = UUID.randomUUID();
        assertThrows(CharacterValidationException.class, () -> CharacterNote.createNew(charId, null, "Body"));
        assertThrows(CharacterValidationException.class, () -> CharacterNote.createNew(charId, "", "Body"));
        assertThrows(CharacterValidationException.class, () -> CharacterNote.createNew(charId, "   ", "Body"));
    }

    @Test
    @DisplayName("Should throw exception when title exceeds 255 characters")
    void shouldThrowExceptionWhenTitleExceeds255Chars() {
        UUID charId = UUID.randomUUID();
        String longTitle = "A".repeat(256);
        CharacterValidationException ex = assertThrows(
                CharacterValidationException.class,
                () -> CharacterNote.createNew(charId, longTitle, "Body")
        );
        assertTrue(ex.getMessage().contains("255"));
    }

    @Test
    @DisplayName("Should throw exception when body is null or blank")
    void shouldThrowExceptionWhenBodyIsNullOrEmpty() {
        UUID charId = UUID.randomUUID();
        assertThrows(CharacterValidationException.class, () -> CharacterNote.createNew(charId, "Title", null));
        assertThrows(CharacterValidationException.class, () -> CharacterNote.createNew(charId, "Title", ""));
        assertThrows(CharacterValidationException.class, () -> CharacterNote.createNew(charId, "Title", "   "));
    }

    @Test
    @DisplayName("Should throw exception when body exceeds 10,000 characters")
    void shouldThrowExceptionWhenBodyExceeds10000Chars() {
        UUID charId = UUID.randomUUID();
        String longBody = "A".repeat(10001);
        CharacterValidationException ex = assertThrows(
                CharacterValidationException.class,
                () -> CharacterNote.createNew(charId, "Title", longBody)
        );
        assertTrue(ex.getMessage().contains("10000"));
    }

    @Test
    @DisplayName("Should correctly compare equality based on id")
    void shouldTestEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID charId = UUID.randomUUID();
        Instant now = Instant.now();

        CharacterNote note1 = new CharacterNote(id, charId, "T1", "B1", now);
        CharacterNote note2 = new CharacterNote(id, charId, "T2", "B2", now);
        CharacterNote note3 = new CharacterNote(UUID.randomUUID(), charId, "T1", "B1", now);

        assertEquals(note1, note2);
        assertNotEquals(note1, note3);
        assertEquals(note1.hashCode(), note2.hashCode());
    }
}
