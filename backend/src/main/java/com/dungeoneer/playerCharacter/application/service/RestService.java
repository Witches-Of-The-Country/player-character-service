package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.RestCommand;
import com.dungeoneer.playerCharacter.application.port.in.RestUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.RestType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class RestService implements RestUseCase {

    private final PlayerCharacterRepositoryPort characterRepository;
    private final CharacterSheetHydrator sheetHydrator;

    public RestService(
            PlayerCharacterRepositoryPort characterRepository,
            CharacterSheetHydrator sheetHydrator
    ) {
        this.characterRepository = characterRepository;
        this.sheetHydrator = sheetHydrator;
    }

    @Override
    public CharacterSheetResult takeRest(UUID characterId, UUID userId, RestCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);
        RestType type = command.restType();

        PlayerCharacter characterToSave = character;

        if (type == RestType.LONG) {
            // Long rest restores HP to max and resets temporary HP to 0
            characterToSave = character.withCurrentHp(character.getHitPointsMax()).withTempHp(0);
        } else if (type == RestType.SHORT && command.hpHealed() != null && command.hpHealed() > 0) {
            int newHp = Math.min(character.getHitPointsMax(), character.getHitPointsCurrent() + command.hpHealed());
            characterToSave = character.withCurrentHp(newHp);
        }

        characterRepository.save(characterToSave);

        // Recharge feats
        List<CharacterFeat> feats = characterRepository.findFeatsByCharacterId(characterId);
        for (CharacterFeat feat : feats) {
            if (feat.getMaxCharges() != null && feat.getMaxCharges() > 0) {
                if (type == RestType.LONG) {
                    characterRepository.saveCharacterFeat(feat.withCurrentCharges(feat.getMaxCharges()));
                }
            }
        }

        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    @Override
    public CharacterSheetResult useFeat(UUID characterId, UUID userId, UUID characterFeatId) {
        PlayerCharacter character = getCharacter(characterId, userId);
        CharacterFeat feat = characterRepository.findFeatsByCharacterId(characterId).stream()
                .filter(cf -> cf.getId().equals(characterFeatId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Character feat not found: " + characterFeatId));

        if (feat.getMaxCharges() != null && feat.getMaxCharges() > 0) {
            if (feat.getCurrentCharges() == null || feat.getCurrentCharges() <= 0) {
                throw new IllegalStateException("No charges remaining for feat");
            }
            characterRepository.saveCharacterFeat(feat.withCurrentCharges(feat.getCurrentCharges() - 1));
        }

        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    private PlayerCharacter getCharacter(UUID characterId, UUID userId) {
        return characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + characterId));
    }
}