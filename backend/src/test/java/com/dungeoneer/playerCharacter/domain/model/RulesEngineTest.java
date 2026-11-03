package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RulesEngineTest {

    private Lineage createLineage(Map<String, Integer> bonuses, int speed) {
        return new Lineage(
                UUID.randomUUID(),
                "Mountain Dwarf",
                "Stout and strong",
                speed,
                "MEDIUM",
                bonuses,
                List.of("Darkvision", "Dwarven Resilience"),
                Ruleset.SRD_2014,
                "SRD",
                null
        );
    }

    private Background createBackground(List<String> allowedBonuses) {
        return new Background(
                UUID.randomUUID(),
                "Soldier",
                "Military background",
                List.of("Athletics", "Intimidation"),
                List.of("Vehicles (land)"),
                List.of("Common"),
                allowedBonuses,
                "Savage Attacker",
                "Extra damage",
                Ruleset.SRD_2024,
                "SRD",
                null
        );
    }

    @Test
    @DisplayName("TP-01: SRD 2014 applies lineage ASI and caps at 20")
    void shouldApplySrd2014LineageAsiAndCapAt20() {
        Srd2014RulesEngine engine = new Srd2014RulesEngine();
        Lineage dwarf = createLineage(Map.of("STRENGTH", 2, "CONSTITUTION", 2), 25);
        AbilityScores base = new AbilityScores(19, 14, 13, 10, 10, 8);

        AbilityScores result = engine.applyAbilityScoreIncreases(base, dwarf, null, null);

        // STR: 19 + 2 = 21 -> capped at 20
        assertEquals(20, result.getStrength());
        // CON: 13 + 2 = 15
        assertEquals(15, result.getConstitution());
        assertEquals(14, result.getDexterity());
        assertEquals(25, engine.calculateSpeed(dwarf));
    }

    @Test
    @DisplayName("TP-01: SRD 2024 applies +2/+1 bonus from background and caps at 20")
    void shouldApplySrd2024BackgroundTwoPlusOneBonus() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        Background soldier = createBackground(List.of("STRENGTH", "CONSTITUTION", "DEXTERITY"));
        AbilityScores base = new AbilityScores(15, 14, 13, 12, 10, 8);

        AbilityScores result = engine.applyAbilityScoreIncreases(
                base,
                null,
                soldier,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1)
        );

        assertEquals(17, result.getStrength());
        assertEquals(14, result.getConstitution());
        assertEquals(14, result.getDexterity());
    }

    @Test
    @DisplayName("TP-01: SRD 2024 applies +1/+1/+1 bonus from background across 3 abilities")
    void shouldApplySrd2024BackgroundThreeOnesBonus() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        Background soldier = createBackground(List.of("STRENGTH", "CONSTITUTION", "DEXTERITY"));
        AbilityScores base = new AbilityScores(15, 14, 13, 12, 10, 8);

        AbilityScores result = engine.applyAbilityScoreIncreases(
                base,
                null,
                soldier,
                Map.of("STRENGTH", 1, "CONSTITUTION", 1, "DEXTERITY", 1)
        );

        assertEquals(16, result.getStrength());
        assertEquals(15, result.getDexterity());
        assertEquals(14, result.getConstitution());
    }

    @Test
    @DisplayName("TE-03: SRD 2024 rejects bonus to ability not in background's allowed list")
    void shouldRejectSrd2024BonusNotAllowedByBackground() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        Background soldier = createBackground(List.of("STRENGTH", "CONSTITUTION", "DEXTERITY"));
        AbilityScores base = new AbilityScores(15, 14, 13, 12, 10, 8);

        // Intelligence is not allowed for Soldier
        assertThrows(CharacterValidationException.class, () ->
                engine.applyAbilityScoreIncreases(
                        base,
                        null,
                        soldier,
                        Map.of("INTELLIGENCE", 2, "STRENGTH", 1)
                )
        );
    }

    @Test
    @DisplayName("TE-03: SRD 2024 rejects invalid bonus distributions")
    void shouldRejectSrd2024InvalidBonusDistribution() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        Background soldier = createBackground(List.of("STRENGTH", "CONSTITUTION", "DEXTERITY"));
        AbilityScores base = new AbilityScores(15, 14, 13, 12, 10, 8);

        // +3 to one ability is invalid
        assertThrows(CharacterValidationException.class, () ->
                engine.applyAbilityScoreIncreases(
                        base,
                        null,
                        soldier,
                        Map.of("STRENGTH", 3)
                )
        );

        // +2 and +2 is invalid
        assertThrows(CharacterValidationException.class, () ->
                engine.applyAbilityScoreIncreases(
                        base,
                        null,
                        soldier,
                        Map.of("STRENGTH", 2, "CONSTITUTION", 2)
                )
        );
    }

    @Test
    @DisplayName("TP-01: HP calculation for Level 1 fighter with CON 14 (+2 mod)")
    void shouldCalculateLevel1HpCorrectly() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        // Fighter d10 + CON mod (+2) = 12
        int hp = engine.calculateMaxHitPoints(1, HitDiceEnum.D10, 2, HpCalculationMethod.MAX, null);
        assertEquals(12, hp);
    }

    @Test
    @DisplayName("TP-01: HP calculation for Level 3 with MAX method")
    void shouldCalculateLevel3MaxHp() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        // Level 1: 10 + 2 = 12
        // Level 2: 10 + 2 = 12
        // Level 3: 10 + 2 = 12
        // Total = 36
        int hp = engine.calculateMaxHitPoints(3, HitDiceEnum.D10, 2, HpCalculationMethod.MAX, null);
        assertEquals(36, hp);
    }

    @Test
    @DisplayName("TP-01: HP calculation for Level 3 with AVERAGE method")
    void shouldCalculateLevel3AverageHp() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        // d10 avg = 6
        // Level 1: 10 + 2 = 12
        // Level 2: 6 + 2 = 8
        // Level 3: 6 + 2 = 8
        // Total = 28
        int hp = engine.calculateMaxHitPoints(3, HitDiceEnum.D10, 2, HpCalculationMethod.AVERAGE, null);
        assertEquals(28, hp);
    }

    @Test
    @DisplayName("TP-01: HP calculation for Level 3 with ROLLED method")
    void shouldCalculateLevel3RolledHp() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        // Level 1: 10 + 2 = 12
        // Rolls for level 2 & 3: [7, 5]
        // Level 2: 7 + 2 = 9
        // Level 3: 5 + 2 = 7
        // Total = 28
        int hp = engine.calculateMaxHitPoints(3, HitDiceEnum.D10, 2, HpCalculationMethod.ROLLED, List.of(7, 5));
        assertEquals(28, hp);
    }

    @Test
    @DisplayName("TE-01: ROLLED HP calculation with mismatched roll count throws exception")
    void shouldRejectRolledHpWithMismatchedCount() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        assertThrows(CharacterValidationException.class, () ->
                engine.calculateMaxHitPoints(3, HitDiceEnum.D10, 2, HpCalculationMethod.ROLLED, List.of(7))
        );
    }

    @Test
    @DisplayName("TP-01: Derived stats calculations (AC, initiative, proficiency, speed)")
    void shouldCalculateDerivedStatsCorrectly() {
        Srd2024RulesEngine engine = new Srd2024RulesEngine();
        assertEquals(13, engine.calculateArmorClass(3)); // 10 + 3 DEX mod
        assertEquals(3, engine.calculateInitiativeBonus(3));
        assertEquals(2, engine.calculateProficiencyBonus(1));
        assertEquals(3, engine.calculateProficiencyBonus(5));
        assertEquals(4, engine.calculateProficiencyBonus(9));
        assertEquals(5, engine.calculateProficiencyBonus(13));
        assertEquals(6, engine.calculateProficiencyBonus(17));
    }

    @Test
    @DisplayName("TE-07: Rest calculations in SRD 2014 and SRD 2024 RulesEngines")
    void shouldCalculateRestMechanicsForBothEngines() {
        RulesEngine engine2014 = new Srd2014RulesEngine();
        RulesEngine engine2024 = new Srd2024RulesEngine();

        // Hit Die healing: dieRoll + CON mod (minimum 0 per die)
        assertEquals(7, engine2014.calculateHitDieHealing(5, 2));
        assertEquals(7, engine2024.calculateHitDieHealing(5, 2));
        // Negative CON mod clamped at 0
        assertEquals(0, engine2014.calculateHitDieHealing(1, -3));
        assertEquals(0, engine2024.calculateHitDieHealing(1, -3));

        // TE-05: Hit Dice recovery: half of total character level rounded down, minimum 1
        assertEquals(1, engine2014.calculateHitDiceRecovery(1));
        assertEquals(1, engine2024.calculateHitDiceRecovery(1));
        assertEquals(1, engine2014.calculateHitDiceRecovery(2));
        assertEquals(1, engine2024.calculateHitDiceRecovery(3));
        assertEquals(3, engine2014.calculateHitDiceRecovery(7));
        assertEquals(3, engine2024.calculateHitDiceRecovery(7));
        assertEquals(10, engine2014.calculateHitDiceRecovery(20));
        assertEquals(10, engine2024.calculateHitDiceRecovery(20));

        // TE-06: Exhaustion recovery: decrement by 1, minimum 0
        assertEquals(1, engine2014.calculateExhaustionRecovery(2));
        assertEquals(1, engine2024.calculateExhaustionRecovery(2));
        assertEquals(0, engine2014.calculateExhaustionRecovery(1));
        assertEquals(0, engine2024.calculateExhaustionRecovery(1));
        assertEquals(0, engine2014.calculateExhaustionRecovery(0));
        assertEquals(0, engine2024.calculateExhaustionRecovery(0));

        // Factory resolution
        assertSame(Srd2014RulesEngine.class, RulesEngine.forRuleset(Ruleset.SRD_2014).getClass());
        assertSame(Srd2024RulesEngine.class, RulesEngine.forRuleset(Ruleset.SRD_2024).getClass());
    }
}
