package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;

import java.util.List;
import java.util.UUID;

public interface ListCharacterNotesUseCase {

    List<CharacterNoteResult> listNotes(UUID characterId, UUID userId);
}
