package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.port.in.GetCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharactersUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CharacterQueryService implements GetCharacterUseCase, ListCharactersUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;
    private final ReferenceDataRepository referenceDataRepository;

    public CharacterQueryService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            ReferenceDataRepository referenceDataRepository
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    @Override
    public CharacterSheetResult getCharacter(UUID characterId, UUID userId) {
        PlayerCharacter character = playerCharacterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + characterId));

        Archetype archetype = null;
        if (!character.getClassProgressions().isEmpty()) {
            UUID archetypeId = character.getClassProgressions().get(0).getArchetypeId();
            archetype = referenceDataRepository.findArchetypeById(archetypeId).orElse(null);
        }

        Lineage lineage = referenceDataRepository.findLineageById(character.getLineageId()).orElse(null);
        Background background = referenceDataRepository.findBackgroundById(character.getBackgroundId()).orElse(null);

        return CharacterSheetHydrator.toSheetResult(character, archetype, lineage, background);
    }

    @Override
    public List<CharacterSummaryResult> listCharacters(UUID userId) {
        List<PlayerCharacter> characters = playerCharacterRepository.findAllByUserIdOrderByUpdatedAtDesc(userId);
        List<CharacterSummaryResult> summaries = new ArrayList<>();

        for (PlayerCharacter c : characters) {
            String className = "Unknown";
            if (!c.getClassProgressions().isEmpty()) {
                UUID archetypeId = c.getClassProgressions().get(0).getArchetypeId();
                className = referenceDataRepository.findArchetypeById(archetypeId)
                        .map(Archetype::getName)
                        .orElse("Unknown");
            }

            String lineageName = referenceDataRepository.findLineageById(c.getLineageId())
                    .map(Lineage::getName)
                    .orElse("Unknown");

            String backgroundName = referenceDataRepository.findBackgroundById(c.getBackgroundId())
                    .map(Background::getName)
                    .orElse("Unknown");

            summaries.add(new CharacterSummaryResult(
                    c.getId(),
                    c.getName(),
                    c.getLevel(),
                    c.getRuleset(),
                    className,
                    lineageName,
                    backgroundName,
                    c.getHitPointsCurrent(),
                    c.getHitPointsMax(),
                    c.getArmorClass(),
                    c.getUpdatedAt()
            ));
        }

        return summaries;
    }
}
