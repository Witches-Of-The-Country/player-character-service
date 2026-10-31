package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.AddCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;
import com.dungeoneer.playerCharacter.application.port.out.CharacterNoteRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.CharacterNote;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterNoteServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort playerCharacterRepository;

    @Mock
    private CharacterNoteRepositoryPort characterNoteRepository;

    @Mock
    private PlayerCharacter playerCharacter;

    private CharacterNoteService service;

    private UUID userId;
    private UUID characterId;

    @BeforeEach
    void setUp() {
        service = new CharacterNoteService(playerCharacterRepository, characterNoteRepository);
        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should successfully add note when character belongs to user")
    void shouldAddNoteSuccessfully() {
        when(playerCharacterRepository.findByIdAndUserId(characterId, userId))
                .thenReturn(Optional.of(playerCharacter));

        CharacterNote saved = new CharacterNote(UUID.randomUUID(), characterId, "Secret Map", "Found behind the throne.", Instant.now());
        when(characterNoteRepository.save(any(CharacterNote.class))).thenReturn(saved);

        AddCharacterNoteCommand command = new AddCharacterNoteCommand("Secret Map", "Found behind the throne.");
        CharacterNoteResult result = service.addNote(characterId, userId, command);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Secret Map");
        assertThat(result.body()).isEqualTo("Found behind the throne.");
        assertThat(result.characterId()).isEqualTo(characterId);

        ArgumentCaptor<CharacterNote> captor = ArgumentCaptor.forClass(CharacterNote.class);
        verify(characterNoteRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Secret Map");
    }

    @Test
    @DisplayName("Should throw CharacterNotFoundException when adding note to non-existent or inaccessible character")
    void shouldThrowNotFoundWhenAddingNoteToNonExistentCharacter() {
        when(playerCharacterRepository.findByIdAndUserId(characterId, userId))
                .thenReturn(Optional.empty());

        AddCharacterNoteCommand command = new AddCharacterNoteCommand("Title", "Body");

        assertThrows(CharacterNotFoundException.class, () -> service.addNote(characterId, userId, command));
        verify(characterNoteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CharacterValidationException when required parameters are null in addNote")
    void shouldValidateArgumentsInAddNote() {
        AddCharacterNoteCommand command = new AddCharacterNoteCommand("Title", "Body");

        assertThrows(CharacterValidationException.class, () -> service.addNote(null, userId, command));
        assertThrows(CharacterValidationException.class, () -> service.addNote(characterId, null, command));
        assertThrows(CharacterValidationException.class, () -> service.addNote(characterId, userId, null));
    }

    @Test
    @DisplayName("Should successfully list notes in descending order")
    void shouldListNotesSuccessfully() {
        when(playerCharacterRepository.findByIdAndUserId(characterId, userId))
                .thenReturn(Optional.of(playerCharacter));

        CharacterNote n1 = new CharacterNote(UUID.randomUUID(), characterId, "Note 1", "Body 1", Instant.now());
        CharacterNote n2 = new CharacterNote(UUID.randomUUID(), characterId, "Note 2", "Body 2", Instant.now().minusSeconds(10));
        when(characterNoteRepository.findAllByCharacterIdOrderByCreatedAtDesc(characterId))
                .thenReturn(List.of(n1, n2));

        List<CharacterNoteResult> results = service.listNotes(characterId, userId);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).title()).isEqualTo("Note 1");
        assertThat(results.get(1).title()).isEqualTo("Note 2");
    }

    @Test
    @DisplayName("Should throw CharacterNotFoundException when listing notes of non-existent character")
    void shouldThrowNotFoundWhenListingNotesOfNonExistentCharacter() {
        when(playerCharacterRepository.findByIdAndUserId(characterId, userId))
                .thenReturn(Optional.empty());

        assertThrows(CharacterNotFoundException.class, () -> service.listNotes(characterId, userId));
        verify(characterNoteRepository, never()).findAllByCharacterIdOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("Should validate arguments when listing notes")
    void shouldValidateArgumentsInListNotes() {
        assertThrows(CharacterValidationException.class, () -> service.listNotes(null, userId));
        assertThrows(CharacterValidationException.class, () -> service.listNotes(characterId, null));
    }
}
