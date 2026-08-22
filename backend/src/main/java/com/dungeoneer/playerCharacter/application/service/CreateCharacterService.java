package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepository;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCharacterService implements CreateCharacterUseCase {

    private final PlayerCharacterRepository playerCharacterRepository;

    public CreateCharacterService(PlayerCharacterRepository playerCharacterRepository) {
        this.playerCharacterRepository = playerCharacterRepository;
    }

    @Override
    public PlayerCharacter createCharacter(CreateCharacterCommand command) {
        AbilityScores abilityScores = new AbilityScores(
                command.strength(),
                command.dexterity(),
                command.constitution(),
                command.intelligence(),
                command.wisdom(),
                command.charisma()
        );

        PlayerCharacter character = PlayerCharacter.createNew(
                command.name(),
                command.level(),
                command.hitPointsMax(),
                command.ruleset(),
                command.abilityScoreMethod(),
                command.lineageId(),
                command.backgroundId(),
                command.userId(),
                abilityScores
        );

        return playerCharacterRepository.save(character);
    }
}
