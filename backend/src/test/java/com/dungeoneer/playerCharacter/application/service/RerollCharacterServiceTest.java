package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RerollCharacterServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort playerCharacterRepository;

    @Mock
    private CharacterSheetHydrator characterSheetHydrator;

    private RerollCharacterService service;

    private UUID userId;
    private UUID characterId;

    @BeforeEach
    void setUp() {
        service = new RerollCharacterService(playerCharacterRepository, characterSheetHydrator);
        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
    }

    private PlayerCharacter createTestCharacter(UUID campaignId, boolean allowAbilityReroll, int con, int dex, int hpMax, int hpCurrent) {
        AbilityScores scores = new AbilityScores(10, dex, con, 10, 10, 10);
        return new PlayerCharacter(
                characterId,
                "Test Hero",
                3, // level 3
                hpCurrent,
                hpMax,
                0,
                10 + scores.getDexterityModifier(),
                scores.getDexterityModifier(),
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                userId,
                scores,
                "",
                HpCalculationMethod.MAX,
                false,
                Collections.emptyList(),
                Collections.emptyList(),
                0,
                campaignId,
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                null,
                0,
                allowAbilityReroll,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("TR-02: Should freely reroll ability scores on unbound character (campaignId == null)")
    void shouldRerollAbilityScoresOnUnboundCharacter() {
        // level 3, old CON 10 (mod 0), old DEX 10 (mod 0), old HP max 24, current 24, AC 10
        PlayerCharacter unboundChar = createTestCharacter(null, false, 10, 10, 24, 24);
        when(playerCharacterRepository.findById(characterId)).thenReturn(Optional.of(unboundChar));
        when(playerCharacterRepository.save(any(PlayerCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

        // New scores: CON 14 (+2 mod -> delta +2 * level 3 = +6 HP), DEX 16 (+3 mod -> AC 13)
        AbilityScores newScores = new AbilityScores(12, 16, 14, 10, 10, 10);

        service.rerollAbilityScores(characterId, userId, newScores);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        PlayerCharacter saved = captor.getValue();

        assertThat(saved.getAbilityScores().getConstitution()).isEqualTo(14);
        assertThat(saved.getHitPointsMax()).isEqualTo(30); // 24 + 6
        assertThat(saved.getHitPointsCurrent()).isEqualTo(30); // 24 + 6
        assertThat(saved.getArmorClass()).isEqualTo(13); // 10 + 3
        assertThat(saved.getInitiativeBonus()).isEqualTo(3);
    }

    @Test
    @DisplayName("TE-01: Should reject reroll when character is bound to campaign and allowAbilityReroll is false")
    void shouldRejectRerollOnCampaignBoundCharacterWithoutAuthorization() {
        UUID campaignId = UUID.randomUUID();
        PlayerCharacter boundChar = createTestCharacter(campaignId, false, 10, 10, 24, 24);
        when(playerCharacterRepository.findById(characterId)).thenReturn(Optional.of(boundChar));

        AbilityScores newScores = new AbilityScores(14, 14, 14, 10, 10, 10);

        assertThatThrownBy(() -> service.rerollAbilityScores(characterId, userId, newScores))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Ability score reroll is locked by campaign rules");
    }

    @Test
    @DisplayName("TR-03: Should allow reroll when campaign-bound character has allowAbilityReroll == true and reset flag")
    void shouldAllowRerollWhenAuthorizedByDMAndResetFlag() {
        UUID campaignId = UUID.randomUUID();
        PlayerCharacter authorizedChar = createTestCharacter(campaignId, true, 10, 10, 24, 24);
        when(playerCharacterRepository.findById(characterId)).thenReturn(Optional.of(authorizedChar));
        when(playerCharacterRepository.save(any(PlayerCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

        AbilityScores newScores = new AbilityScores(14, 12, 16, 10, 10, 10); // CON 16 (+3 mod -> delta +3 * 3 = +9 HP)

        service.rerollAbilityScores(characterId, userId, newScores);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        PlayerCharacter saved = captor.getValue();

        assertThat(saved.getHitPointsMax()).isEqualTo(33); // 24 + 9
        assertThat(saved.isAllowAbilityReroll()).isFalse(); // consumed
    }

    @Test
    @DisplayName("Should clamp current HP to new max HP if CON modifier drops")
    void shouldClampCurrentHpWhenConModifierDrops() {
        // level 3, old CON 16 (mod +3), old HP max 30, current HP 28
        PlayerCharacter charWithHighCon = createTestCharacter(null, false, 16, 10, 30, 28);
        when(playerCharacterRepository.findById(characterId)).thenReturn(Optional.of(charWithHighCon));
        when(playerCharacterRepository.save(any(PlayerCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

        // New scores: CON 10 (mod 0 -> delta -3 * level 3 = -9 HP, new max 21)
        AbilityScores newScores = new AbilityScores(10, 10, 10, 10, 10, 10);

        service.rerollAbilityScores(characterId, userId, newScores);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        PlayerCharacter saved = captor.getValue();

        assertThat(saved.getHitPointsMax()).isEqualTo(21);
        assertThat(saved.getHitPointsCurrent()).isEqualTo(21); // clamped from 28 to 21
    }

    @Test
    @DisplayName("Should update reroll permission flag successfully")
    void shouldUpdateRerollPermission() {
        UUID campaignId = UUID.randomUUID();
        PlayerCharacter lockedChar = createTestCharacter(campaignId, false, 10, 10, 20, 20);
        when(playerCharacterRepository.findById(characterId)).thenReturn(Optional.of(lockedChar));
        when(playerCharacterRepository.save(any(PlayerCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

        service.updateRerollPermission(characterId, userId, true);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        PlayerCharacter saved = captor.getValue();

        assertThat(saved.isAllowAbilityReroll()).isTrue();
    }
}
