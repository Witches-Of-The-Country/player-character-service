package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.CharacterNote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CharacterNoteRepositoryPort {

    CharacterNote save(CharacterNote note);

    Optional<CharacterNote> findByIdAndCharacterId(UUID id, UUID characterId);

    List<CharacterNote> findAllByCharacterIdOrderByCreatedAtDesc(UUID characterId);
}
