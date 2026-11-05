package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Collections;
import java.util.Map;

public record WondrousMechanics(
        Map<String, Integer> abilityBonuses,
        Map<String, Integer> abilityOverrides,
        int acBonus,
        int savingThrowBonus
) implements ItemMechanics {

    public WondrousMechanics(
            Map<String, Integer> abilityBonuses,
            Map<String, Integer> abilityOverrides,
            Integer acBonus,
            Integer savingThrowBonus
    ) {
        this(
                abilityBonuses != null ? Collections.unmodifiableMap(abilityBonuses) : Collections.emptyMap(),
                abilityOverrides != null ? Collections.unmodifiableMap(abilityOverrides) : Collections.emptyMap(),
                acBonus != null ? acBonus : 0,
                savingThrowBonus != null ? savingThrowBonus : 0
        );
    }

    public WondrousMechanics {
        abilityBonuses = abilityBonuses != null ? Collections.unmodifiableMap(abilityBonuses) : Collections.emptyMap();
        abilityOverrides = abilityOverrides != null ? Collections.unmodifiableMap(abilityOverrides) : Collections.emptyMap();
        for (Integer overrideVal : abilityOverrides.values()) {
            if (overrideVal == null || overrideVal < 1 || overrideVal > 30) {
                throw new CharacterValidationException("Ability score override must be between 1 and 30");
            }
        }
    }
}
