package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.port.in.RerollCharacterAbilityScoresUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class RerollCharacterService implements RerollCharacterAbilityScoresUseCase {

    private final PlayerCharacterRepositoryPort playerCharacterRepository;
    private final CharacterSheetHydrator characterSheetHydrator;
    private final Random random = new Random();

    public RerollCharacterService(
            PlayerCharacterRepositoryPort playerCharacterRepository,
            CharacterSheetHydrator characterSheetHydrator
    ) {
        this.playerCharacterRepository = playerCharacterRepository;
        this.characterSheetHydrator = characterSheetHydrator;
    }

    @Override
    public CharacterSheetResult rerollAbilityScores(UUID characterId, UUID userId, AbilityScores customScores) {
        if (characterId == null) {
            throw new CharacterValidationException("CharacterId is required.");
        }
        if (userId == null) {
            throw new CharacterValidationException("UserId is required.");
        }

        PlayerCharacter character = playerCharacterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found: " + characterId));

        if (!character.getUserId().equals(userId)) {
            throw new CharacterValidationException("Access denied: You do not own this character.");
        }

        // Campaign governance check:
        if (character.getCampaignId() != null && !character.isAllowAbilityReroll()) {
            throw new CharacterValidationException("Ability score reroll is locked by campaign rules. Dungeon Master authorization is required.");
        }

        AbilityScores scoresToApply = customScores;
        if (scoresToApply == null) {
            // Generate 4d6kh3 rolls for all 6 attributes
            int[] rolls = new int[6];
            for (int i = 0; i < 6; i++) {
                List<Integer> dice = new ArrayList<>();
                for (int d = 0; d < 4; d++) {
                    dice.add(random.nextInt(6) + 1);
                }
                Collections.sort(dice);
                rolls[i] = dice.get(1) + dice.get(2) + dice.get(3);
            }
            scoresToApply = new AbilityScores(rolls[0], rolls[1], rolls[2], rolls[3], rolls[4], rolls[5]);
        }

        PlayerCharacter updated = character.updateAbilityScores(scoresToApply);
        PlayerCharacter saved = playerCharacterRepository.save(updated);

        return characterSheetHydrator.hydrate(saved);
    }

    @Override
    public CharacterSheetResult updateRerollPermission(UUID characterId, UUID userId, boolean allowAbilityReroll) {
        if (characterId == null) {
            throw new CharacterValidationException("CharacterId is required.");
        }
        if (userId == null) {
            throw new CharacterValidationException("UserId is required.");
        }

        PlayerCharacter character = playerCharacterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found: " + characterId));

        PlayerCharacter updated = character.withAllowAbilityReroll(allowAbilityReroll);
        PlayerCharacter saved = playerCharacterRepository.save(updated);

        return characterSheetHydrator.hydrate(saved);
    }
}
