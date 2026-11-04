package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;

public interface RollHitPointsUseCase {
    RollHitPointsResult rollHitPoints(RollHitPointsCommand command);
}
