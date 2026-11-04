package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record LineageDto(
        UUID id,
        String name,
        String description,
        int speed,
        String size,
        Map<String, Integer> abilityBonuses,
        List<String> traitsSummary,
        Ruleset ruleset,
        String source
) {
    public static LineageDto fromDomain(Lineage domain) {
        return new LineageDto(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getSpeed(),
                domain.getSize(),
                domain.getAbilityBonuses(),
                domain.getTraitsSummary(),
                domain.getRuleset(),
                domain.getSource()
        );
    }
}
