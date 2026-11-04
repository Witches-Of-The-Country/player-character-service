package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public record WeaponMechanics(
        List<DamageRoll> damageRolls,
        String versatileDice,
        int magicBonus,
        List<WeaponProperty> properties
) implements ItemMechanics {

    private static final Pattern DICE_PATTERN = Pattern.compile("^\\d{1,2}d(4|6|8|10|12|20|100)([+-]\\d{1,3})?$");

    public WeaponMechanics {
        if (damageRolls == null || damageRolls.isEmpty()) {
            throw new CharacterValidationException("WeaponMechanics must have at least one damage roll.");
        }
        if (versatileDice != null && !versatileDice.trim().isEmpty()) {
            String normalizedVersatile = versatileDice.trim();
            if (!DICE_PATTERN.matcher(normalizedVersatile).matches()) {
                throw new CharacterValidationException("Invalid versatile dice formula: " + versatileDice);
            }
            versatileDice = normalizedVersatile;
        } else {
            versatileDice = null;
        }
        if (magicBonus < 0 || magicBonus > 3) {
            throw new CharacterValidationException("Magic bonus must be between 0 and 3. Provided: " + magicBonus);
        }
        properties = properties != null ? Collections.unmodifiableList(properties) : Collections.emptyList();
        damageRolls = Collections.unmodifiableList(damageRolls);
    }
}
