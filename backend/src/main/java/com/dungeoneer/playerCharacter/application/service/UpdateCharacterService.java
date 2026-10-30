package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateCharacterService implements UpdateCharacterUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;
    private final ReferenceDataRepository referenceDataRepository;

    public UpdateCharacterService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            ReferenceDataRepository referenceDataRepository
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    @Override
    public CharacterSheetResult updateCharacter(UpdateCharacterCommand command) {
        PlayerCharacter character = playerCharacterRepository.findByIdAndUserId(command.characterId(), command.userId())
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + command.characterId()));

        PlayerCharacter updated = character.updateSheet(
                command.name(),
                command.hitPointsCurrent(),
                command.hitPointsTemp(),
                command.notes()
        );

        PlayerCharacter saved = playerCharacterRepository.save(updated);

        Archetype archetype = null;
        if (!saved.getClassProgressions().isEmpty()) {
            UUID archetypeId = saved.getClassProgressions().get(0).getArchetypeId();
            archetype = referenceDataRepository.findArchetypeById(archetypeId).orElse(null);
        }

        Lineage lineage = referenceDataRepository.findLineageById(saved.getLineageId()).orElse(null);
        Background background = referenceDataRepository.findBackgroundById(saved.getBackgroundId()).orElse(null);

        return CharacterSheetHydrator.toSheetResult(saved, archetype, lineage, background);
    }
}
