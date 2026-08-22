package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCharacterServiceTest {

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @InjectMocks
    private CreateCharacterService createCharacterService;

    private CreateCharacterCommand validCommand() {
        return new CreateCharacterCommand(
                "Thorin Oakenshield",
                1,
                12,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                15, 12, 14, 10, 13, 8
        );
    }

    @Nested
    @DisplayName("Successful Creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("should create and save character when command is valid")
        void shouldCreateAndSaveCharacterWhenCommandIsValid() {
            CreateCharacterCommand command = validCommand();

            when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PlayerCharacter result = createCharacterService.createCharacter(command);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Thorin Oakenshield");
            assertThat(result.getLevel()).isEqualTo(1);
            assertThat(result.getHitPointsMax()).isEqualTo(12);
            assertThat(result.getHitPointsCurrent()).isEqualTo(12);
            assertThat(result.getHitPointsTemp()).isZero();
            assertThat(result.getRuleset()).isEqualTo(Ruleset.SRD_2024);
            assertThat(result.getAbilityScoreMethod()).isEqualTo(AbilityScoreMethod.STANDARD_ARRAY);
            assertThat(result.getLineageId()).isEqualTo(command.lineageId());
            assertThat(result.getBackgroundId()).isEqualTo(command.backgroundId());
            assertThat(result.getUserId()).isEqualTo(command.userId());

            assertThat(result.getAbilityScores()).isNotNull();
            assertThat(result.getAbilityScores().getStrength()).isEqualTo(15);
            assertThat(result.getAbilityScores().getDexterity()).isEqualTo(12);
            assertThat(result.getAbilityScores().getConstitution()).isEqualTo(14);
            assertThat(result.getAbilityScores().getIntelligence()).isEqualTo(10);
            assertThat(result.getAbilityScores().getWisdom()).isEqualTo(13);
            assertThat(result.getAbilityScores().getCharisma()).isEqualTo(8);

            // Derived stats: Base AC 10 + DEX mod (1) = 11
            assertThat(result.getArmorClass()).isEqualTo(11);
            assertThat(result.getInitiativeBonus()).isEqualTo(1);
            assertThat(result.getProficiencyBonus()).isEqualTo(2);

            ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
            verify(playerCharacterRepository).save(captor.capture());
            PlayerCharacter savedCharacter = captor.getValue();
            assertThat(savedCharacter.getName()).isEqualTo("Thorin Oakenshield");
        }
    }

    @Nested
    @DisplayName("Validation and Failure Cases")
    class FailureCases {

        @Test
        @DisplayName("should throw exception if command creates invalid domain entity")
        void shouldThrowExceptionWhenDomainValidationFails() {
            CreateCharacterCommand command = new CreateCharacterCommand(
                    "   ", // Invalid blank name
                    1,
                    12,
                    Ruleset.SRD_2024,
                    AbilityScoreMethod.STANDARD_ARRAY,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    15, 12, 14, 10, 13, 8
            );

            assertThatThrownBy(() -> createCharacterService.createCharacter(command))
                    .isInstanceOf(CharacterValidationException.class);
        }
    }
}
