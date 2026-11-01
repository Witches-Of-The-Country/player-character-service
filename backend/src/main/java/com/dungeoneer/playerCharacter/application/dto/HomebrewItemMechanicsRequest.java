package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.ArmorCategory;
import com.dungeoneer.playerCharacter.domain.model.ArmorMechanics;
import com.dungeoneer.playerCharacter.domain.model.ConsumableEffectType;
import com.dungeoneer.playerCharacter.domain.model.ConsumableMechanics;
import com.dungeoneer.playerCharacter.domain.model.DamageRoll;
import com.dungeoneer.playerCharacter.domain.model.DamageType;
import com.dungeoneer.playerCharacter.domain.model.EmptyItemMechanics;
import com.dungeoneer.playerCharacter.domain.model.ItemMechanics;
import com.dungeoneer.playerCharacter.domain.model.WeaponMechanics;
import com.dungeoneer.playerCharacter.domain.model.WeaponProperty;

import java.util.ArrayList;
import java.util.List;

public record HomebrewItemMechanicsRequest(
        String type,
        List<HomebrewDamageRollRequest> damageRolls,
        String versatileDice,
        Integer magicBonus,
        List<String> properties,
        String armorCategory,
        Integer baseAc,
        Integer strengthRequirement,
        Boolean stealthDisadvantage,
        String effectType,
        String diceFormula,
        String effectDescription
) {
    public ItemMechanics toDomain(String fallbackItemCategory) {
        String effectiveType = type;
        if (effectiveType == null || effectiveType.isBlank()) {
            if ((damageRolls != null && !damageRolls.isEmpty()) || properties != null || versatileDice != null) {
                effectiveType = "WEAPON";
            } else if (armorCategory != null || baseAc != null) {
                effectiveType = "ARMOR";
            } else if (effectType != null) {
                effectiveType = "CONSUMABLE";
            } else if (fallbackItemCategory != null) {
                effectiveType = fallbackItemCategory.trim().toUpperCase();
            }
        }

        if (effectiveType != null) {
            switch (effectiveType.toUpperCase()) {
                case "WEAPON" -> {
                    List<DamageRoll> rolls = new ArrayList<>();
                    if (damageRolls != null) {
                        for (HomebrewDamageRollRequest dr : damageRolls) {
                            if (dr != null && dr.diceFormula() != null && !dr.diceFormula().isBlank()) {
                                DamageType dt = DamageType.SLASHING;
                                if (dr.damageType() != null && !dr.damageType().isBlank()) {
                                    try {
                                        dt = DamageType.valueOf(dr.damageType().trim().toUpperCase());
                                    } catch (Exception ignored) {
                                    }
                                }
                                rolls.add(new DamageRoll(dr.diceFormula().trim(), dt, Boolean.TRUE.equals(dr.conditional()), dr.conditionNote()));
                            }
                        }
                    }
                    if (rolls.isEmpty()) {
                        rolls.add(new DamageRoll("1d4", DamageType.BLUDGEONING));
                    }
                    List<WeaponProperty> props = new ArrayList<>();
                    if (properties != null) {
                        for (String p : properties) {
                            if (p != null && !p.isBlank()) {
                                try {
                                    props.add(WeaponProperty.valueOf(p.trim().toUpperCase()));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                    String normalizedVersatile = (versatileDice != null && !versatileDice.isBlank()) ? versatileDice.trim() : null;
                    return new WeaponMechanics(
                            rolls,
                            normalizedVersatile,
                            magicBonus != null ? magicBonus : 0,
                            props
                    );
                }
                case "ARMOR" -> {
                    ArmorCategory cat = ArmorCategory.LIGHT;
                    if (armorCategory != null && !armorCategory.isBlank()) {
                        try {
                            cat = ArmorCategory.valueOf(armorCategory.trim().toUpperCase());
                        } catch (Exception ignored) {
                        }
                    }
                    return new ArmorMechanics(
                            cat,
                            baseAc != null ? baseAc : 10,
                            magicBonus != null ? magicBonus : 0,
                            strengthRequirement != null ? strengthRequirement : 0,
                            Boolean.TRUE.equals(stealthDisadvantage)
                    );
                }
                case "CONSUMABLE" -> {
                    ConsumableEffectType et = ConsumableEffectType.BUFF_UTILITY;
                    if (effectType != null && !effectType.isBlank()) {
                        try {
                            et = ConsumableEffectType.valueOf(effectType.trim().toUpperCase());
                        } catch (Exception ignored) {
                        }
                    }
                    String normalizedFormula = (diceFormula != null && !diceFormula.isBlank()) ? diceFormula.trim() : null;
                    return new ConsumableMechanics(
                            et,
                            normalizedFormula,
                            effectDescription != null ? effectDescription : ""
                    );
                }
            }
        }
        return new EmptyItemMechanics();
    }
}
