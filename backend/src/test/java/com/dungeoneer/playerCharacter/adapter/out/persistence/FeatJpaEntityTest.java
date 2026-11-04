package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.AbilityBonusChoice;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.RestType;
import com.dungeoneer.playerCharacter.domain.model.SpellChoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeatJpaEntityTest {

    @Test
    @DisplayName("Should deserialize full JSON including spellChoice, abilityBonusChoice and prerequisiteRules")
    void shouldDeserializeFullEffectsJson() {
        String json = """
        {
            "abilityBonusChoice": {
                "chooseCount": 1,
                "allowedAbilities": ["INT", "WIS", "CHA"],
                "bonusAmount": 1
            },
            "spellChoice": {
                "cantripsCount": 0,
                "spellsCount": 2,
                "spellLevel": 1,
                "allowedClasses": ["BARD", "CLERIC", "DRUID", "SORCERER", "WARLOCK", "WIZARD"],
                "isRitualOnly": true
            },
            "prerequisiteRules": [
                {
                    "type": "LEVEL",
                    "minValue": 4,
                    "displayLabel": "Nível 4+"
                }
            ],
            "actionName": "Conjuração Ritual",
            "actionType": "ACTION",
            "usesPerRest": 1,
            "restType": "LONG"
        }
        """;

        MechanicalEffects effects = FeatJpaEntity.deserializeEffects(json);

        assertNotNull(effects);
        assertEquals("Conjuração Ritual", effects.actionName());
        assertEquals(RestType.LONG, effects.restType());
        assertEquals(1, effects.usesPerRest());

        assertNotNull(effects.spellChoice());
        assertEquals(0, effects.spellChoice().cantripsCount());
        assertEquals(2, effects.spellChoice().spellsCount());
        assertEquals(1, effects.spellChoice().spellLevel());
        assertTrue(effects.spellChoice().isRitualOnly());
        assertEquals(List.of("BARD", "CLERIC", "DRUID", "SORCERER", "WARLOCK", "WIZARD"), effects.spellChoice().allowedClasses());

        assertNotNull(effects.abilityBonusChoice());
        assertEquals(1, effects.abilityBonusChoice().chooseCount());
        assertEquals(List.of("INT", "WIS", "CHA"), effects.abilityBonusChoice().allowedAbilities());

        assertEquals(1, effects.prerequisiteRules().size());
        assertEquals("LEVEL", effects.prerequisiteRules().get(0).type());
        assertEquals(4, effects.prerequisiteRules().get(0).minValue());
    }

    @Test
    @DisplayName("Should return empty effects on null or empty json")
    void shouldReturnEmptyEffectsOnEmptyJson() {
        MechanicalEffects empty1 = FeatJpaEntity.deserializeEffects(null);
        MechanicalEffects empty2 = FeatJpaEntity.deserializeEffects("");
        MechanicalEffects empty3 = FeatJpaEntity.deserializeEffects("{}");

        assertNotNull(empty1);
        assertNotNull(empty2);
        assertNotNull(empty3);
        assertTrue(empty1.abilityBonuses().isEmpty());
    }

    @Test
    @DisplayName("Should preserve all effects in round-trip serialization and deserialization")
    void shouldPreserveAllEffectsInRoundTrip() {
        SpellChoice spellChoice = new SpellChoice(2, 1, 1, List.of("CLERIC", "DRUID", "WIZARD"), false);
        AbilityBonusChoice bonusChoice = new AbilityBonusChoice(1, List.of("STR", "DEX"), 1);
        com.dungeoneer.playerCharacter.domain.model.FeatPrerequisite prereq =
                new com.dungeoneer.playerCharacter.domain.model.FeatPrerequisite("LEVEL", null, 4, "Nível 4+");

        MechanicalEffects original = new MechanicalEffects(
                java.util.Map.of("STR", 1),
                1,
                2,
                10,
                2,
                List.of("Armadura Pesada"),
                "Ação Teste",
                "BONUS_ACTION",
                "1d6",
                2,
                RestType.SHORT,
                bonusChoice,
                true,
                "Condição ativa",
                List.of(prereq),
                spellChoice
        );

        String json = FeatJpaEntity.serializeEffects(original);
        MechanicalEffects deserialized = FeatJpaEntity.deserializeEffects(json);

        assertNotNull(deserialized);
        assertEquals(1, deserialized.abilityBonuses().get("STR"));
        assertEquals(1, deserialized.acBonus());
        assertEquals(2, deserialized.initiativeBonus());
        assertEquals(10, deserialized.speedBonus());
        assertEquals(2, deserialized.hpPerLevelBonus());
        assertEquals("Ação Teste", deserialized.actionName());
        assertEquals(RestType.SHORT, deserialized.restType());
        assertEquals(2, deserialized.usesPerRest());
        assertTrue(deserialized.conditional());
        assertEquals("Condição ativa", deserialized.conditionDescription());

        assertNotNull(deserialized.spellChoice());
        assertEquals(2, deserialized.spellChoice().cantripsCount());
        assertEquals(1, deserialized.spellChoice().spellsCount());
        assertEquals(List.of("CLERIC", "DRUID", "WIZARD"), deserialized.spellChoice().allowedClasses());

        assertNotNull(deserialized.abilityBonusChoice());
        assertEquals(1, deserialized.abilityBonusChoice().chooseCount());
        assertEquals(List.of("STR", "DEX"), deserialized.abilityBonusChoice().allowedAbilities());

        assertEquals(1, deserialized.prerequisiteRules().size());
        assertEquals("LEVEL", deserialized.prerequisiteRules().get(0).type());
    }
}
