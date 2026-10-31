package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.CharacterNote;

import java.util.List;
import java.util.UUID;

public interface CharacterNoteRepositoryPort {

    CharacterNote save(CharacterNote note);

    List<CharacterNote> findAllByCharacterIdOrderByCreatedAtDesc(UUID characterId);
}
