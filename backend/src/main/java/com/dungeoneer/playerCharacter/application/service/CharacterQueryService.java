package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterClassSummaryDto;
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
import com.dungeoneer.playerCharacter.domain.model.Subclass;
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
    private final CharacterSheetHydrator characterSheetHydrator;

    public CharacterQueryService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator characterSheetHydrator
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.characterSheetHydrator = characterSheetHydrator;
    }

    @Override
    public CharacterSheetResult getCharacter(UUID characterId, UUID userId) {
        PlayerCharacter character = playerCharacterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found or access denied: " + characterId));

        return characterSheetHydrator.hydrate(character);
    }

    @Override
    public List<CharacterSummaryResult> listCharacters(UUID userId) {
        List<PlayerCharacter> characters = playerCharacterRepository.findAllByUserIdOrderByUpdatedAtDesc(userId);
        List<CharacterSummaryResult> summaries = new ArrayList<>();

        for (PlayerCharacter c : characters) {
            String primaryClassName = "Unknown";
            String primarySubclassName = null;
            List<CharacterClassSummaryDto> classes = new ArrayList<>();

            for (ClassProgression cp : c.getClassProgressions()) {
                Archetype arch = referenceDataRepository.findArchetypeById(cp.getArchetypeId()).orElse(null);
                String cName = arch != null ? arch.getName() : "Unknown";
                String hitDie = arch != null && arch.getHitDice() != null ? arch.getHitDice().name() : "D8";
                String scName = null;
                if (cp.getSubclassId() != null) {
                    scName = referenceDataRepository.findSubclassById(cp.getSubclassId())
                            .map(Subclass::getName)
                            .orElse(null);
                }
                classes.add(new CharacterClassSummaryDto(
                        cp.getId(),
                        cName,
                        scName,
                        cp.getClassLevel(),
                        hitDie,
                        cp.getHitDiceSpent(),
                        cp.getAvailableHitDice()
                ));
            }

            if (!classes.isEmpty()) {
                primaryClassName = classes.get(0).className();
                primarySubclassName = classes.get(0).subclassName();
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
                    primaryClassName,
                    primarySubclassName,
                    lineageName,
                    backgroundName,
                    c.getHitPointsCurrent(),
                    c.getHitPointsMax(),
                    c.getArmorClass(),
                    c.getAvatarUrl(),
                    classes,
                    c.getUpdatedAt()
            ));
        }

        return summaries;
    }
}
