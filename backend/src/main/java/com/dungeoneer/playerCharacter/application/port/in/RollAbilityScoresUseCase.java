package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;

import java.util.UUID;

public interface RollAbilityScoresUseCase {
    RollAbilityScoresResult rollAbilityScores(UUID userId);
}
