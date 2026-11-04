package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.AddExperienceCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;

import java.util.UUID;

public interface AddExperienceUseCase {
    CharacterSheetResult addExperience(UUID characterId, UUID userId, AddExperienceCommand command);
}
