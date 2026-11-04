package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.UseConsumableResult;

import java.util.UUID;

public interface UseConsumableUseCase {
    UseConsumableResult useConsumable(UUID characterId, UUID userId, UUID characterItemId);
}
