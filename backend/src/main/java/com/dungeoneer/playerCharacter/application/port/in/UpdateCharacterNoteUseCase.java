package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterNoteCommand;

import java.util.UUID;

public interface UpdateCharacterNoteUseCase {

    CharacterNoteResult updateNote(UUID characterId, UUID noteId, UUID userId, UpdateCharacterNoteCommand command);
}
