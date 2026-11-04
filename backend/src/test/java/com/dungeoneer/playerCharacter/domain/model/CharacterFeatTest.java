package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CharacterFeatTest {

    private final AbilityScores standardScores = new AbilityScores(16, 14, 12, 10, 8, 18);

    @Test
    @DisplayName("Should initialize CharacterFeat and clamp current charges if greater than max")
    void shouldInitializeAndClampCurrentCharges() {
        UUID charId = UUID.randomUUID();
        UUID featId = UUID.randomUUID();

        CharacterFeat feat = new CharacterFeat(
                UUID.randomUUID(),
                charId,
                featId,
                1,
                10,
                5,
                "{}"
        );

        assertEquals(5, feat.getMaxCharges());
        assertEquals(5, feat.getCurrentCharges(), "Current charges must be clamped to max charges");
    }

    @Test
    @DisplayName("TE-05: Current charges clamped if max charges decrease via withMaxCharges")
    void shouldClampCurrentChargesWhenMaxChargesDecrease() {
        CharacterFeat feat = new CharacterFeat(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                5,
                5,
                "{}"
        );
        assertEquals(5, feat.getCurrentCharges());

        CharacterFeat updated = feat.withMaxCharges(3);
        assertEquals(3, updated.getMaxCharges());
        assertEquals(3, updated.getCurrentCharges(), "Current charges must clamp to new reduced max charges");
    }

    @Test
    @DisplayName("Should not clamp current charges when max charges decrease to a value still higher than current")
    void shouldNotClampWhenCurrentIsBelowNewMax() {
        CharacterFeat feat = new CharacterFeat(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                2,
                5,
                "{}"
        );

        CharacterFeat updated = feat.withMaxCharges(4);
        assertEquals(4, updated.getMaxCharges());
        assertEquals(2, updated.getCurrentCharges());
    }

    @Test
    @DisplayName("TE-02: recoverCharges should clamp to max charges when recovery amount exceeds missing")
    void shouldClampRecoveryToMaxCharges() {
        CharacterFeat feat = new CharacterFeat(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                4,
                5,
                "{}"
        );

        CharacterFeat recovered = feat.recoverCharges(3);
        assertEquals(5, recovered.getCurrentCharges());
    }

    @Test
    @DisplayName("TP-02: recoverCharges with MechanicalEffects handles independent short and long rest")
    void shouldRecoverChargesPerRestTypeWithMechanicalEffects() {
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, RestType.NONE,
                null, false, null, List.of(), null,
                ChargeType.FIXED, null, 6,
                ChargeRecoveryType.FIXED, 2,
                ChargeRecoveryType.ALL, null
        );

        CharacterFeat feat = new CharacterFeat(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                1,
                6,
                "{}"
        );

        // Short rest recovers 2
        CharacterFeat afterShort = feat.recoverCharges(RestType.SHORT, effects, standardScores, 2, 3);
        assertEquals(3, afterShort.getCurrentCharges());

        // Long rest recovers all missing (3 -> 6)
        CharacterFeat afterLong = afterShort.recoverCharges(RestType.LONG, effects, standardScores, 2, 3);
        assertEquals(6, afterLong.getCurrentCharges());
    }

    @Test
    @DisplayName("Should throw exception on negative charges or invalid levels")
    void shouldThrowOnNegativeChargesOrInvalidLevels() {
        assertThrows(CharacterValidationException.class, () ->
                new CharacterFeat(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0, 1, 1, "{}")
        );
        assertThrows(CharacterValidationException.class, () ->
                new CharacterFeat(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 21, 1, 1, "{}")
        );
        assertThrows(CharacterValidationException.class, () ->
                new CharacterFeat(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1, -1, 1, "{}")
        );
        assertThrows(CharacterValidationException.class, () ->
                new CharacterFeat(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1, 1, -1, "{}")
        );
    }
}
