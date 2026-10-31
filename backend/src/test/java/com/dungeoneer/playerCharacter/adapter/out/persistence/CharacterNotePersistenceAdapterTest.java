package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.CharacterNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CharacterNotePersistenceAdapterTest {

    @Mock
    private SpringDataCharacterNoteRepository repository;

    private CharacterNotePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CharacterNotePersistenceAdapter(repository);
    }

    @Test
    @DisplayName("Should convert domain to entity and save, then map back to domain")
    void shouldSaveCharacterNote() {
        UUID charId = UUID.randomUUID();
        CharacterNote note = CharacterNote.createNew(charId, "Title", "Body");

        CharacterNoteJpaEntity entity = CharacterNoteJpaEntity.fromDomain(note);
        when(repository.save(any(CharacterNoteJpaEntity.class))).thenReturn(entity);

        CharacterNote result = adapter.save(note);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getBody()).isEqualTo("Body");
        assertThat(result.getCharacterId()).isEqualTo(charId);

        ArgumentCaptor<CharacterNoteJpaEntity> captor = ArgumentCaptor.forClass(CharacterNoteJpaEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Title");
    }

    @Test
    @DisplayName("Should find note by id and character id")
    void shouldFindByIdAndCharacterId() {
        UUID charId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();
        CharacterNoteJpaEntity entity = new CharacterNoteJpaEntity(noteId, charId, "Title", "Body", Instant.now());

        when(repository.findByIdAndCharacterId(noteId, charId)).thenReturn(Optional.of(entity));

        Optional<CharacterNote> result = adapter.findByIdAndCharacterId(noteId, charId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(noteId);
        assertThat(result.get().getCharacterId()).isEqualTo(charId);
        assertThat(result.get().getTitle()).isEqualTo("Title");
    }

    @Test
    @DisplayName("Should list notes ordered by creation date descending")
    void shouldListNotesOrderedDesc() {
        UUID charId = UUID.randomUUID();
        CharacterNoteJpaEntity e1 = new CharacterNoteJpaEntity(UUID.randomUUID(), charId, "N1", "B1", Instant.now());
        CharacterNoteJpaEntity e2 = new CharacterNoteJpaEntity(UUID.randomUUID(), charId, "N2", "B2", Instant.now().minusSeconds(60));

        when(repository.findAllByCharacterIdOrderByCreatedAtDesc(charId)).thenReturn(List.of(e1, e2));

        List<CharacterNote> notes = adapter.findAllByCharacterIdOrderByCreatedAtDesc(charId);

        assertThat(notes).hasSize(2);
        assertThat(notes.get(0).getTitle()).isEqualTo("N1");
        assertThat(notes.get(1).getTitle()).isEqualTo("N2");
    }
}
