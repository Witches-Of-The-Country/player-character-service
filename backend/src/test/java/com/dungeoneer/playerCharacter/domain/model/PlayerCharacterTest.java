package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCharacterTest {

    private AbilityScores createValidAbilityScores() {
        return new AbilityScores(15, 14, 13, 12, 10, 8);
    }

    @Test
    @DisplayName("Should create PlayerCharacter successfully with valid attributes")
    void shouldCreatePlayerCharacterSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AbilityScores abilityScores = createValidAbilityScores();
        Instant now = Instant.now();

        PlayerCharacter character = new PlayerCharacter(
                id,
                "Thorin Stonebeard",
                3,
                24,
                24,
                0,
                16,
                2,
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                now,
                now
        );

        assertEquals(id, character.getId());
        assertEquals("Thorin Stonebeard", character.getName());
        assertEquals(3, character.getLevel());
        assertEquals(24, character.getHitPointsCurrent());
        assertEquals(24, character.getHitPointsMax());
        assertEquals(0, character.getHitPointsTemp());
        assertEquals(16, character.getArmorClass());
        assertEquals(2, character.getInitiativeBonus());
        assertEquals(30, character.getSpeed());
        assertEquals(2, character.getProficiencyBonus());
        assertEquals(Ruleset.SRD_2024, character.getRuleset());
        assertEquals(AbilityScoreMethod.STANDARD_ARRAY, character.getAbilityScoreMethod());
        assertEquals(lineageId, character.getLineageId());
        assertEquals(backgroundId, character.getBackgroundId());
        assertEquals(userId, character.getUserId());
        assertEquals(abilityScores, character.getAbilityScores());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "4, 2",
            "5, 3",
            "8, 3",
            "9, 4",
            "12, 4",
            "13, 5",
            "16, 5",
            "17, 6",
            "20, 6"
    })
    @DisplayName("Should calculate correct proficiency bonus for each level (1-20)")
    void shouldCalculateCorrectProficiencyBonus(int level, int expectedProficiencyBonus) {
        assertEquals(expectedProficiencyBonus, PlayerCharacter.calculateProficiencyBonus(level));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 21, 25, 100})
    @DisplayName("Should throw exception when level is outside 1..20")
    void shouldThrowExceptionWhenLevelIsInvalid(int invalidLevel) {
        assertThrows(CharacterValidationException.class, () ->
                PlayerCharacter.calculateProficiencyBonus(invalidLevel)
        );

        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(
                        UUID.randomUUID(),
                        "Invalid Hero",
                        invalidLevel,
                        10,
                        10,
                        0,
                        10,
                        0,
                        30,
                        2,
                        Ruleset.SRD_2024,
                        AbilityScoreMethod.STANDARD_ARRAY,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        createValidAbilityScores(),
                        Instant.now(),
                        Instant.now()
                )
        );
    }

    @Test
    @DisplayName("Should throw exception when name is null or blank")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(
                        UUID.randomUUID(),
                        null,
                        1,
                        10,
                        10,
                        0,
                        10,
                        0,
                        30,
                        2,
                        Ruleset.SRD_2024,
                        AbilityScoreMethod.STANDARD_ARRAY,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        createValidAbilityScores(),
                        Instant.now(),
                        Instant.now()
                )
        );

        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(
                        UUID.randomUUID(),
                        "   ",
                        1,
                        10,
                        10,
                        0,
                        10,
                        0,
                        30,
                        2,
                        Ruleset.SRD_2024,
                        AbilityScoreMethod.STANDARD_ARRAY,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        createValidAbilityScores(),
                        Instant.now(),
                        Instant.now()
                )
        );
    }

    @Test
    @DisplayName("Should throw exception when mandatory fields are null")
    void shouldThrowExceptionWhenMandatoryFieldsAreNull() {
        UUID id = UUID.randomUUID();
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = createValidAbilityScores();
        Instant now = Instant.now();

        // Null Ruleset
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        null, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, null, scores, now, now)
        );

        // Null AbilityScoreMethod
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, null, lineageId, backgroundId, null, scores, now, now)
        );

        // Null LineageId
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, null, backgroundId, null, scores, now, now)
        );

        // Null BackgroundId
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, null, null, scores, now, now)
        );

        // Null AbilityScores
        assertThrows(CharacterValidationException.class, () ->
                new PlayerCharacter(id, "Hero", 1, 10, 10, 0, 10, 0, 30, 2,
                        Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, null, null, now, now)
        );
    }

    @Test
    @DisplayName("Should create character with builder/factory defaulting calculation methods correctly")
    void shouldCreateNewCharacterWithAutoCalculatedFields() {
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = new AbilityScores(14, 16, 12, 10, 8, 13); // DEX mod = +3

        PlayerCharacter character = PlayerCharacter.createNew(
                "Aramil",
                5, // level 5 -> prof bonus +3
                35, // max HP
                Ruleset.SRD_2024,
                AbilityScoreMethod.POINT_BUY,
                lineageId,
                backgroundId,
                null,
                scores
        );

        assertNotNull(character.getId());
        assertEquals("Aramil", character.getName());
        assertEquals(5, character.getLevel());
        assertEquals(3, character.getProficiencyBonus());
        assertEquals(35, character.getHitPointsMax());
        assertEquals(35, character.getHitPointsCurrent());
        assertEquals(0, character.getHitPointsTemp());
        assertEquals(13, character.getArmorClass()); // 10 + 3 DEX
        assertEquals(3, character.getInitiativeBonus()); // +3 DEX
        assertEquals(30, character.getSpeed()); // default 30
        assertNotNull(character.getCreatedAt());
        assertNotNull(character.getUpdatedAt());
    }

    @Test
    @DisplayName("Should successfully update mutable sheet fields via updateSheet")
    void shouldUpdateMutableSheetFieldsSuccessfully() {
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = createValidAbilityScores();

        PlayerCharacter character = PlayerCharacter.createNew(
                "Aramil",
                3,
                24,
                Ruleset.SRD_2024,
                AbilityScoreMethod.POINT_BUY,
                lineageId,
                backgroundId,
                UUID.randomUUID(),
                scores
        );

        PlayerCharacter updated = character.updateSheet("Aramil the Brave", 18, 5, "Campaign notes");

        assertEquals("Aramil the Brave", updated.getName());
        assertEquals(18, updated.getHitPointsCurrent());
        assertEquals(5, updated.getHitPointsTemp());
        assertEquals("Campaign notes", updated.getNotes());
        // Immutable attributes preserved
        assertEquals(character.getId(), updated.getId());
        assertEquals(character.getLevel(), updated.getLevel());
        assertEquals(character.getHitPointsMax(), updated.getHitPointsMax());
        assertEquals(character.getRuleset(), updated.getRuleset());
        assertEquals(character.getAbilityScores(), updated.getAbilityScores());
    }

    @Test
    @DisplayName("Should reject invalid hit points or name during updateSheet")
    void shouldRejectInvalidUpdateSheetValues() {
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = createValidAbilityScores();

        PlayerCharacter character = PlayerCharacter.createNew(
                "Aramil",
                3,
                24,
                Ruleset.SRD_2024,
                AbilityScoreMethod.POINT_BUY,
                lineageId,
                backgroundId,
                UUID.randomUUID(),
                scores
        );

        // Name blank
        assertThrows(CharacterValidationException.class, () ->
                character.updateSheet("   ", 20, 0, "notes")
        );

        // HP current > max
        assertThrows(CharacterValidationException.class, () ->
                character.updateSheet("Aramil", 25, 0, "notes")
        );

        // HP current < 0
        assertThrows(CharacterValidationException.class, () ->
                character.updateSheet("Aramil", -1, 0, "notes")
        );

        // HP temp < 0
        assertThrows(CharacterValidationException.class, () ->
                character.updateSheet("Aramil", 20, -5, "notes")
        );
    }

    @Test
    @DisplayName("Spec 006: withItems, withFeats, and withLevelUp must retain currencyPouch and avatarUrl")
    void shouldRetainCurrencyPouchAndAvatarUrlAcrossMutations() {
        UUID lineageId = UUID.randomUUID();
        UUID backgroundId = UUID.randomUUID();
        AbilityScores scores = createValidAbilityScores();

        PlayerCharacter character = PlayerCharacter.createNew(
                "Gimli",
                1,
                12,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                lineageId,
                backgroundId,
                UUID.randomUUID(),
                scores
        );

        CurrencyPouch pouch = new CurrencyPouch(10, 20, 5, 50, 2);
        String avatar = "https://example.com/avatar.png";

        PlayerCharacter characterWithPouchAndAvatar = character.withCurrencyPouch(pouch).withAvatarUrl(avatar);
        assertEquals(50, characterWithPouchAndAvatar.getCurrencyPouch().gp());
        assertEquals(avatar, characterWithPouchAndAvatar.getAvatarUrl());

        // withItems
        PlayerCharacter withItems = characterWithPouchAndAvatar.withItems(java.util.List.of());
        assertEquals(50, withItems.getCurrencyPouch().gp());
        assertEquals(avatar, withItems.getAvatarUrl());

        // withFeats
        PlayerCharacter withFeats = withItems.withFeats(java.util.List.of());
        assertEquals(50, withFeats.getCurrencyPouch().gp());
        assertEquals(avatar, withFeats.getAvatarUrl());

        // withLevelUp
        PlayerCharacter leveledUp = withFeats.withLevelUp(
                2,
                20,
                scores,
                java.util.List.of(ClassProgression.createInitial(UUID.randomUUID(), null, 2)),
                java.util.List.of(),
                300,
                15,
                false
        );
        assertEquals(50, leveledUp.getCurrencyPouch().gp());
        assertEquals(avatar, leveledUp.getAvatarUrl());
    }

    @Test
    @DisplayName("Should default exhaustion to 0 and allow valid exhaustion values (0-6)")
    void shouldHandleExhaustionLifecycle() {
        PlayerCharacter character = PlayerCharacter.createNew(
                "Exhaustion Hero",
                1,
                10,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                createValidAbilityScores()
        );

        assertEquals(0, character.getExhaustion());
        assertFalse(character.isDeadFromExhaustion());

        PlayerCharacter exhausted = character.withExhaustion(3);
        assertEquals(3, exhausted.getExhaustion());
        assertFalse(exhausted.isDeadFromExhaustion());

        PlayerCharacter added = exhausted.addExhaustion(2);
        assertEquals(5, added.getExhaustion());

        // Clamps at 6
        PlayerCharacter maxExhausted = added.addExhaustion(3);
        assertEquals(6, maxExhausted.getExhaustion());
        assertTrue(maxExhausted.isDeadFromExhaustion());

        // Remove exhaustion
        PlayerCharacter recovered = maxExhausted.removeExhaustion(2);
        assertEquals(4, recovered.getExhaustion());
        assertFalse(recovered.isDeadFromExhaustion());

        // Clamps at 0
        PlayerCharacter fullRecovered = recovered.removeExhaustion(10);
        assertEquals(0, fullRecovered.getExhaustion());
    }

    @Test
    @DisplayName("Should reject invalid exhaustion levels (< 0 or > 6)")
    void shouldRejectInvalidExhaustionLevels() {
        PlayerCharacter character = PlayerCharacter.createNew(
                "Hero",
                1,
                10,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                createValidAbilityScores()
        );

        assertThrows(CharacterValidationException.class, () -> character.withExhaustion(-1));
        assertThrows(CharacterValidationException.class, () -> character.withExhaustion(7));
    }

    @Test
    @DisplayName("Should preserve exhaustion across various character mutations")
    void shouldPreserveExhaustionAcrossMutations() {
        PlayerCharacter character = PlayerCharacter.createNew(
                "Hero",
                1,
                10,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                createValidAbilityScores()
        ).withExhaustion(2);

        assertEquals(2, character.getExhaustion());

        PlayerCharacter withHp = character.withCurrentHp(5);
        assertEquals(2, withHp.getExhaustion());

        PlayerCharacter withAc = withHp.withArmorClass(14);
        assertEquals(2, withAc.getExhaustion());

        PlayerCharacter withPouch = withAc.withCurrencyPouch(new CurrencyPouch(1, 2, 3, 4, 5));
        assertEquals(2, withPouch.getExhaustion());

        PlayerCharacter withAvatar = withPouch.withAvatarUrl("https://example.com/avatar.png");
        assertEquals(2, withAvatar.getExhaustion());
    }
}
