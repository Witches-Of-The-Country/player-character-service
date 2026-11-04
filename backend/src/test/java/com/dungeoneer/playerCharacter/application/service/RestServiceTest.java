package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.RestCommand;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.FeatType;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.RestType;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestServiceTest {

    private PlayerCharacterRepositoryPort characterRepository;
    private ReferenceDataRepository referenceDataRepository;
    private CharacterSheetHydrator sheetHydrator;
    private RestService restService;

    private UUID userId;
    private UUID characterId;
    private UUID archetypeId;
    private UUID lineageId;
    private UUID backgroundId;

    @BeforeEach
    void setUp() {
        characterRepository = mock(PlayerCharacterRepositoryPort.class);
        referenceDataRepository = mock(ReferenceDataRepository.class);
        sheetHydrator = mock(CharacterSheetHydrator.class);

        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
        archetypeId = UUID.randomUUID();
        lineageId = UUID.randomUUID();
        backgroundId = UUID.randomUUID();

        // Fixed roll of 5 for deterministic testing
        restService = new RestService(characterRepository, referenceDataRepository, sheetHydrator, sides -> 5);

        Archetype archetype = new Archetype(
                archetypeId,
                "Fighter",
                HitDiceEnum.D8,
                "STR",
                List.of("STR", "CON"),
                List.of("ALL"),
                List.of("ALL"),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        );
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
    }

    private PlayerCharacter createTestCharacter(int level, int currentHp, int maxHp, int tempHp, int exhaustion, List<ClassProgression> progressions) {
        AbilityScores abilityScores = new AbilityScores(10, 10, 14, 10, 10, 10); // CON 14 (+2 mod)
        return PlayerCharacter.createNew(
                "Valeros",
                level,
                maxHp,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                lineageId,
                backgroundId,
                userId,
                abilityScores
        ).withCurrentHp(currentHp)
         .withTempHp(tempHp)
         .withExhaustion(exhaustion)
         .withClassProgressions(progressions);
    }

    @Test
    @DisplayName("TP-01: Short rest with Hit Dice spending heals roll + CON mod and updates spent dice")
    void shouldPerformShortRestWithHitDiceHealingAndSpending() {
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 3, 0);
        PlayerCharacter character = createTestCharacter(3, 10, 30, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));
        when(sheetHydrator.hydrate(any())).thenReturn(mock(CharacterSheetResult.class));

        RestCommand command = new RestCommand(
                RestType.SHORT,
                null,
                List.of(new RestCommand.HitDiceSpendRequest(cpId, 1))
        );

        restService.takeRest(character.getId(), userId, command);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(characterRepository).save(captor.capture());

        PlayerCharacter saved = captor.getValue();
        // Roll = 5, CON mod = +2 -> heal 7 -> 10 + 7 = 17
        assertEquals(17, saved.getHitPointsCurrent());
        assertEquals(1, saved.getClassProgressions().get(0).getHitDiceSpent());
        assertEquals(2, saved.getClassProgressions().get(0).getAvailableHitDice());
    }

    @Test
    @DisplayName("TE-01: Short rest healing is capped at max hit points")
    void shouldCapShortRestHealingAtMaxHp() {
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 3, 0);
        PlayerCharacter character = createTestCharacter(3, 28, 30, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));
        when(sheetHydrator.hydrate(any())).thenReturn(mock(CharacterSheetResult.class));

        RestCommand command = new RestCommand(
                RestType.SHORT,
                null,
                List.of(new RestCommand.HitDiceSpendRequest(cpId, 1))
        );

        restService.takeRest(character.getId(), userId, command);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(characterRepository).save(captor.capture());

        // 28 + 7 = 35 -> capped at 30
        assertEquals(30, captor.getValue().getHitPointsCurrent());
    }

    @Test
    @DisplayName("TE-03: Rest attempt rejects dead or unconscious character (0 HP)")
    void shouldRejectRestWhenCharacterIsDeadOrUnconscious() {
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 1, 0);
        PlayerCharacter deadCharacter = createTestCharacter(1, 0, 10, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(deadCharacter.getId(), userId)).thenReturn(Optional.of(deadCharacter));

        RestCommand command = new RestCommand(RestType.LONG);

        CharacterValidationException ex = assertThrows(CharacterValidationException.class, () ->
                restService.takeRest(deadCharacter.getId(), userId, command)
        );
        assertTrue(ex.getMessage().contains("0 HP"));
        verify(characterRepository, never()).save(any());
    }

    @Test
    @DisplayName("TE-02: Short rest rejects overspending available Hit Dice")
    void shouldRejectShortRestWhenOverspendingHitDice() {
        UUID cpId = UUID.randomUUID();
        // Level 3, but 2 already spent -> only 1 available
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 3, 2);
        PlayerCharacter character = createTestCharacter(3, 10, 30, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));

        // Request spending 2 dice when only 1 is available
        RestCommand command = new RestCommand(
                RestType.SHORT,
                null,
                List.of(new RestCommand.HitDiceSpendRequest(cpId, 2))
        );

        CharacterValidationException ex = assertThrows(CharacterValidationException.class, () ->
                restService.takeRest(character.getId(), userId, command)
        );
        assertTrue(ex.getMessage().contains("Cannot spend 2 Hit Dice"));
        verify(characterRepository, never()).save(any());
    }

    @Test
    @DisplayName("TE-04: Short rest rejects unowned/foreign class progression ID")
    void shouldRejectShortRestWhenClassProgressionNotFound() {
        UUID foreignId = UUID.randomUUID();
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 3, 0);
        PlayerCharacter character = createTestCharacter(3, 10, 30, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));

        RestCommand command = new RestCommand(
                RestType.SHORT,
                null,
                List.of(new RestCommand.HitDiceSpendRequest(foreignId, 1))
        );

        CharacterValidationException ex = assertThrows(CharacterValidationException.class, () ->
                restService.takeRest(character.getId(), userId, command)
        );
        assertTrue(ex.getMessage().contains("Class progression not found"));
        verify(characterRepository, never()).save(any());
    }

    @Test
    @DisplayName("TP-02: Long rest restores full HP, preserves temp HP, recovers Hit Dice, reduces exhaustion, and recharges feats")
    void shouldPerformLongRestFullRecoveryAndPreserveTempHp() {
        UUID cpId = UUID.randomUUID();
        // Level 7 character with 7 hit dice spent
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 7, 7);
        PlayerCharacter character = createTestCharacter(7, 15, 50, 8, 2, List.of(cp));

        UUID featId = UUID.randomUUID();
        CharacterFeat characterFeat = new CharacterFeat(
                UUID.randomUUID(),
                character.getId(),
                featId,
                1,
                0,
                3,
                "{}"
        );

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));
        when(characterRepository.findFeatsByCharacterId(character.getId())).thenReturn(List.of(characterFeat));
        when(sheetHydrator.hydrate(any())).thenReturn(mock(CharacterSheetResult.class));

        RestCommand command = new RestCommand(RestType.LONG);
        restService.takeRest(character.getId(), userId, command);

        ArgumentCaptor<PlayerCharacter> charCaptor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(characterRepository).save(charCaptor.capture());

        PlayerCharacter saved = charCaptor.getValue();
        // Full HP restored
        assertEquals(50, saved.getHitPointsCurrent());
        // Temp HP PRESERVED!
        assertEquals(8, saved.getHitPointsTemp());
        // Exhaustion reduced from 2 to 1
        assertEquals(1, saved.getExhaustion());
        // Hit Dice recovery: half of 7 = 3 recovered -> 7 - 3 = 4 spent
        assertEquals(4, saved.getClassProgressions().get(0).getHitDiceSpent());
        assertEquals(3, saved.getClassProgressions().get(0).getAvailableHitDice());

        // Feat recharged
        ArgumentCaptor<CharacterFeat> featCaptor = ArgumentCaptor.forClass(CharacterFeat.class);
        verify(characterRepository).saveCharacterFeat(featCaptor.capture());
        assertEquals(3, featCaptor.getValue().getCurrentCharges());
    }

    @Test
    @DisplayName("TE-05: Long rest recovers at least 1 Hit Die for level 1 character")
    void shouldRecoverAtLeastOneHitDieOnLongRestForLevel1() {
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 1, 1);
        PlayerCharacter character = createTestCharacter(1, 5, 12, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));
        when(sheetHydrator.hydrate(any())).thenReturn(mock(CharacterSheetResult.class));

        RestCommand command = new RestCommand(RestType.LONG);
        restService.takeRest(character.getId(), userId, command);

        ArgumentCaptor<PlayerCharacter> charCaptor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(characterRepository).save(charCaptor.capture());

        // Level 1 / 2 = 0 -> minimum 1 recovered -> 1 - 1 = 0 spent
        assertEquals(0, charCaptor.getValue().getClassProgressions().get(0).getHitDiceSpent());
        assertEquals(1, charCaptor.getValue().getClassProgressions().get(0).getAvailableHitDice());
    }

    @Test
    @DisplayName("TE-06: Long rest with exhaustion at 0 remains at 0")
    void shouldKeepExhaustionAtZeroWhenAlreadyZero() {
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 1, 0);
        PlayerCharacter character = createTestCharacter(1, 8, 10, 0, 0, List.of(cp));

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));
        when(sheetHydrator.hydrate(any())).thenReturn(mock(CharacterSheetResult.class));

        RestCommand command = new RestCommand(RestType.LONG);
        restService.takeRest(character.getId(), userId, command);

        ArgumentCaptor<PlayerCharacter> charCaptor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(characterRepository).save(charCaptor.capture());

        assertEquals(0, charCaptor.getValue().getExhaustion());
    }

    @Test
    @DisplayName("Short rest recovers feat charges configured with short rest recovery")
    void shouldRecoverShortRestFeatChargesOnShortRest() {
        UUID cpId = UUID.randomUUID();
        ClassProgression cp = new ClassProgression(cpId, characterId, archetypeId, null, 3, 0);
        PlayerCharacter character = createTestCharacter(3, 20, 30, 0, 0, List.of(cp));

        UUID featId = UUID.randomUUID();
        CharacterFeat characterFeat = new CharacterFeat(
                UUID.randomUUID(),
                character.getId(),
                featId,
                1,
                0,
                2,
                "{}"
        );

        Feat featDef = new Feat(
                featId,
                Ruleset.SRD_2024,
                "action-surge",
                "Action Surge",
                "Action Surge",
                null,
                "Action surge feat",
                "SRD",
                null,
                FeatType.GENERAL,
                new MechanicalEffects(
                        Map.of(), 0, 0, 0, 0, List.of(), null, null, null, 2, RestType.SHORT
                )
        );

        when(characterRepository.findByIdAndUserId(character.getId(), userId)).thenReturn(Optional.of(character));
        when(characterRepository.findFeatsByCharacterId(character.getId())).thenReturn(List.of(characterFeat));
        when(referenceDataRepository.findFeatById(featId)).thenReturn(Optional.of(featDef));
        when(sheetHydrator.hydrate(any())).thenReturn(mock(CharacterSheetResult.class));

        RestCommand command = new RestCommand(RestType.SHORT, 0);
        restService.takeRest(character.getId(), userId, command);

        ArgumentCaptor<CharacterFeat> featCaptor = ArgumentCaptor.forClass(CharacterFeat.class);
        verify(characterRepository).saveCharacterFeat(featCaptor.capture());
        assertEquals(2, featCaptor.getValue().getCurrentCharges());
    }
}
