package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAndDeleteCharacterServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort playerCharacterRepository;
    @Mock
    private ReferenceDataRepository referenceDataRepository;

    private UpdateCharacterService updateService;
    private DeleteCharacterService deleteService;

    private UUID userId;
    private UUID characterId;

    @BeforeEach
    void setUp() {
        updateService = new UpdateCharacterService(playerCharacterRepository, referenceDataRepository);
        deleteService = new DeleteCharacterService(playerCharacterRepository);
        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
    }

    @Test
    @DisplayName("TP-03: Updates character sheet mutable fields successfully")
    void shouldUpdateCharacterSheetSuccessfully() {
        PlayerCharacter existing = new PlayerCharacter(
                characterId, "Boromir", 5, 45, 45, 0, 16, 1, 30, 3,
                Ruleset.SRD_2024, AbilityScoreMethod.POINT_BUY, UUID.randomUUID(), UUID.randomUUID(), userId,
                new AbilityScores(16, 12, 14, 10, 12, 14), "Initial notes",
                HpCalculationMethod.MAX, false, List.of(),
                Instant.now(), Instant.now()
        );

        when(playerCharacterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(existing));
        when(playerCharacterRepository.save(any(PlayerCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCharacterCommand command = new UpdateCharacterCommand(
                characterId, userId, "Boromir of Gondor", 35, 5, "Defending the Hobbits"
        );

        CharacterSheetResult result = updateService.updateCharacter(command);

        assertThat(result.name()).isEqualTo("Boromir of Gondor");
        assertThat(result.hitPointsCurrent()).isEqualTo(35);
        assertThat(result.hitPointsTemp()).isEqualTo(5);
        assertThat(result.notes()).isEqualTo("Defending the Hobbits");
        // Max HP and Level remain immutable
        assertThat(result.hitPointsMax()).isEqualTo(45);
        assertThat(result.level()).isEqualTo(5);
    }

    @Test
    @DisplayName("TE-02: Update throws CharacterNotFoundException when character belongs to another user")
    void shouldRejectUpdateFromDifferentUser() {
        UUID otherUser = UUID.randomUUID();
        when(playerCharacterRepository.findByIdAndUserId(characterId, otherUser)).thenReturn(Optional.empty());

        UpdateCharacterCommand command = new UpdateCharacterCommand(
                characterId, otherUser, "Hacked Name", 20, 0, "Hacked notes"
        );

        assertThatThrownBy(() -> updateService.updateCharacter(command))
                .isInstanceOf(CharacterNotFoundException.class);
    }

    @Test
    @DisplayName("TP-03: Deletes character when owned by user")
    void shouldDeleteCharacterWhenOwnedByUser() {
        when(playerCharacterRepository.existsByIdAndUserId(characterId, userId)).thenReturn(true);

        deleteService.deleteCharacter(characterId, userId);

        verify(playerCharacterRepository).deleteByIdAndUserId(characterId, userId);
    }

    @Test
    @DisplayName("TE-02: Delete throws CharacterNotFoundException when character belongs to another user")
    void shouldRejectDeleteFromDifferentUser() {
        UUID otherUser = UUID.randomUUID();
        when(playerCharacterRepository.existsByIdAndUserId(characterId, otherUser)).thenReturn(false);

        assertThatThrownBy(() -> deleteService.deleteCharacter(characterId, otherUser))
                .isInstanceOf(CharacterNotFoundException.class);
    }
}
