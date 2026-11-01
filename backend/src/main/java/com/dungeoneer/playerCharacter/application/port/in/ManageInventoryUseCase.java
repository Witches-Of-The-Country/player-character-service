package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.AddCharacterItemCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterItemCommand;
import com.dungeoneer.playerCharacter.application.dto.UpdateCurrencyRequest;

import java.util.UUID;

public interface ManageInventoryUseCase {
    CharacterSheetResult addItem(UUID characterId, UUID userId, AddCharacterItemCommand command);
    CharacterSheetResult updateItem(UUID characterId, UUID userId, UUID characterItemId, UpdateCharacterItemCommand command);
    CharacterSheetResult removeItem(UUID characterId, UUID userId, UUID characterItemId);
    CharacterSheetResult updateCurrency(UUID characterId, UUID userId, UpdateCurrencyRequest request);
}
