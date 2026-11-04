package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;

import java.util.List;
import java.util.UUID;

public interface ListCharactersUseCase {
    List<CharacterSummaryResult> listCharacters(UUID userId);
}
