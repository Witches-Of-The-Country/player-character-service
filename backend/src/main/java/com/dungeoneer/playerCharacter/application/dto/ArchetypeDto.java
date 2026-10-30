package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;

import java.util.List;
import java.util.UUID;

public record ArchetypeDto(
        UUID id,
        String name,
        String hitDice,
        String primaryAbility,
        List<String> savingThrowProficiencies,
        List<String> armorProficiencies,
        List<String> weaponProficiencies,
        SpellcastingType spellcastingType,
        String spellcastingAbility,
        Ruleset ruleset,
        String source
) {
    public static ArchetypeDto fromDomain(Archetype domain) {
        return new ArchetypeDto(
                domain.getId(),
                domain.getName(),
                domain.getHitDice().name().toLowerCase(),
                domain.getPrimaryAbility(),
                domain.getSavingThrowProficiencies(),
                domain.getArmorProficiencies(),
                domain.getWeaponProficiencies(),
                domain.getSpellcastingType(),
                domain.getSpellcastingAbility(),
                domain.getRuleset(),
                domain.getSource()
        );
    }
}
