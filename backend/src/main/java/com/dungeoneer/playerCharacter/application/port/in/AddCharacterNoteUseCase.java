package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.AddCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;

import java.util.UUID;

public interface AddCharacterNoteUseCase {

    CharacterNoteResult addNote(UUID characterId, UUID userId, AddCharacterNoteCommand command);
}
