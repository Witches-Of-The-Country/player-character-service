package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateCharacterService implements UpdateCharacterUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;
    private final CharacterSheetHydrator characterSheetHydrator;

    public UpdateCharacterService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            CharacterSheetHydrator characterSheetHydrator
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.characterSheetHydrator = characterSheetHydrator;
    }

    @Override
    public CharacterSheetResult updateCharacter(UpdateCharacterCommand command) {
        PlayerCharacter character = playerCharacterRepository.findByIdAndUserId(command.characterId(), command.userId())
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + command.characterId()));

        String newName = (command.name() != null && !command.name().isBlank())
                ? command.name().trim()
                : character.getName();
        int newHpCurrent = command.hitPointsCurrent() != null
                ? command.hitPointsCurrent()
                : character.getHitPointsCurrent();
        int newHpTemp = command.hitPointsTemp() != null
                ? command.hitPointsTemp()
                : character.getHitPointsTemp();
        String newNotes = command.notes() != null
                ? command.notes()
                : character.getNotes();

        PlayerCharacter updated = character.updateSheet(
                newName,
                newHpCurrent,
                newHpTemp,
                newNotes
        );
        if (command.avatarUrl() != null) {
            updated = updated.withAvatarUrl(command.avatarUrl());
        }

        PlayerCharacter saved = playerCharacterRepository.save(updated);

        return characterSheetHydrator.hydrate(saved);
    }
}
