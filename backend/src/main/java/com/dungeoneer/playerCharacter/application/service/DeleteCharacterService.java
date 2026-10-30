package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.port.in.DeleteCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteCharacterService implements DeleteCharacterUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;

    public DeleteCharacterService(PlayerCharacterRepositoryPort playerCharacterRepository) {
        this.playerCharacterRepository = playerCharacterRepository;
    }

    @Override
    public void deleteCharacter(UUID characterId, UUID userId) {
        if (!playerCharacterRepository.existsByIdAndUserId(characterId, userId)) {
            throw new CharacterNotFoundException("Character not found or access denied: " + characterId);
        }
        playerCharacterRepository.deleteByIdAndUserId(characterId, userId);
    }
}
