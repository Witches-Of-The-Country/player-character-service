package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.AddCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.port.in.AddCharacterNoteUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharacterNotesUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterNoteUseCase;
import com.dungeoneer.playerCharacter.application.port.out.CharacterNoteRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.CharacterNote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CharacterNoteService implements AddCharacterNoteUseCase, ListCharacterNotesUseCase, UpdateCharacterNoteUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;
    private final CharacterNoteRepositoryPort characterNoteRepository;

    public CharacterNoteService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            CharacterNoteRepositoryPort characterNoteRepository
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.characterNoteRepository = characterNoteRepository;
    }

    @Override
    public CharacterNoteResult addNote(UUID characterId, UUID userId, AddCharacterNoteCommand command) {
        if (userId == null) {
            throw new CharacterValidationException("UserId is required.");
        }
        if (characterId == null) {
            throw new CharacterValidationException("CharacterId is required.");
        }
        if (command == null) {
            throw new CharacterValidationException("Command is required.");
        }

        playerCharacterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + characterId));

        CharacterNote note = CharacterNote.createNew(characterId, command.title(), command.body());
        CharacterNote saved = characterNoteRepository.save(note);

        return new CharacterNoteResult(
                saved.getId(),
                saved.getCharacterId(),
                saved.getTitle(),
                saved.getBody(),
                saved.getCreatedAt()
        );
    }

    @Override
    public CharacterNoteResult updateNote(UUID characterId, UUID noteId, UUID userId, UpdateCharacterNoteCommand command) {
        if (userId == null) {
            throw new CharacterValidationException("UserId is required.");
        }
        if (characterId == null) {
            throw new CharacterValidationException("CharacterId is required.");
        }
        if (noteId == null) {
            throw new CharacterValidationException("NoteId is required.");
        }
        if (command == null) {
            throw new CharacterValidationException("Command is required.");
        }

        playerCharacterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + characterId));

        CharacterNote existing = characterNoteRepository.findByIdAndCharacterId(noteId, characterId)
                .orElseThrow(() -> new CharacterNotFoundException("Character note not found: " + noteId));

        CharacterNote updated = existing.update(command.title(), command.body());
        CharacterNote saved = characterNoteRepository.save(updated);

        return new CharacterNoteResult(
                saved.getId(),
                saved.getCharacterId(),
                saved.getTitle(),
                saved.getBody(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterNoteResult> listNotes(UUID characterId, UUID userId) {
        if (userId == null) {
            throw new CharacterValidationException("UserId is required.");
        }
        if (characterId == null) {
            throw new CharacterValidationException("CharacterId is required.");
        }

        playerCharacterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + characterId));

        return characterNoteRepository.findAllByCharacterIdOrderByCreatedAtDesc(characterId)
                .stream()
                .map(note -> new CharacterNoteResult(
                        note.getId(),
                        note.getCharacterId(),
                        note.getTitle(),
                        note.getBody(),
                        note.getCreatedAt()
                ))
                .toList();
    }
}
