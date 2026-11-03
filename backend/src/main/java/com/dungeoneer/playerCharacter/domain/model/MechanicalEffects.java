package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public record MechanicalEffects(
        Map<String, Integer> abilityBonuses,
        int acBonus,
        int initiativeBonus,
        int speedBonus,
        int hpPerLevelBonus,
        List<String> grantedProficiencies,
        String actionName,
        String actionType,
        String diceFormula,
        Integer usesPerRest,
        RestType restType,
        AbilityBonusChoice abilityBonusChoice,
        boolean conditional,
        String conditionDescription,
        List<FeatPrerequisite> prerequisiteRules,
        SpellChoice spellChoice
) {

    private static final Pattern DICE_PATTERN = Pattern.compile("^\\d{1,2}d(4|6|8|10|12|20|100)([+-]\\d{1,3})?$");

    public MechanicalEffects(
            Map<String, Integer> abilityBonuses,
            int acBonus,
            int initiativeBonus,
            int speedBonus,
            int hpPerLevelBonus,
            List<String> grantedProficiencies,
            String actionName,
            String actionType,
            String diceFormula,
            Integer usesPerRest,
            RestType restType
    ) {
        this(
                abilityBonuses,
                acBonus,
                initiativeBonus,
                speedBonus,
                hpPerLevelBonus,
                grantedProficiencies,
                actionName,
                actionType,
                diceFormula,
                usesPerRest,
                restType,
                null,
                false,
                null,
                Collections.emptyList(),
                null
        );
    }

    public MechanicalEffects(
            Map<String, Integer> abilityBonuses,
            int acBonus,
            int initiativeBonus,
            int speedBonus,
            int hpPerLevelBonus,
            List<String> grantedProficiencies,
            String actionName,
            String actionType,
            String diceFormula,
            Integer usesPerRest,
            RestType restType,
            AbilityBonusChoice abilityBonusChoice,
            boolean conditional,
            String conditionDescription,
            List<FeatPrerequisite> prerequisiteRules
    ) {
        this(
                abilityBonuses,
                acBonus,
                initiativeBonus,
                speedBonus,
                hpPerLevelBonus,
                grantedProficiencies,
                actionName,
                actionType,
                diceFormula,
                usesPerRest,
                restType,
                abilityBonusChoice,
                conditional,
                conditionDescription,
                prerequisiteRules,
                null
        );
    }

    public MechanicalEffects {
        if (diceFormula != null && !diceFormula.trim().isEmpty()) {
            String normalizedFormula = diceFormula.trim();
            if (!DICE_PATTERN.matcher(normalizedFormula).matches()) {
                throw new CharacterValidationException("Invalid feat dice formula: " + diceFormula);
            }
            diceFormula = normalizedFormula;
        } else {
            diceFormula = null;
        }
        if (usesPerRest != null && usesPerRest < 0) {
            throw new CharacterValidationException("Uses per rest cannot be negative.");
        }
        abilityBonuses = abilityBonuses != null ? Collections.unmodifiableMap(abilityBonuses) : Collections.emptyMap();
        grantedProficiencies = grantedProficiencies != null ? Collections.unmodifiableList(grantedProficiencies) : Collections.emptyList();
        prerequisiteRules = prerequisiteRules != null ? Collections.unmodifiableList(prerequisiteRules) : Collections.emptyList();
        restType = restType != null ? restType : RestType.NONE;
    }

    public static MechanicalEffects empty() {
        return new MechanicalEffects(
                Collections.emptyMap(),
                0,
                0,
                0,
                0,
                Collections.emptyList(),
                null,
                null,
                null,
                null,
                RestType.NONE,
                null,
                false,
                null,
                Collections.emptyList(),
                null
        );
    }
}
