package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;
import java.util.UUID;

public record BackgroundDto(
        UUID id,
        String name,
        String description,
        List<String> skillProficiencies,
        List<String> toolProficiencies,
        List<String> languages,
        List<String> allowedAbilityBonuses,
        String originFeatName,
        String originFeatDescription,
        Ruleset ruleset,
        String source
) {
    public static BackgroundDto fromDomain(Background domain) {
        return new BackgroundDto(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getSkillProficiencies(),
                domain.getToolProficiencies(),
                domain.getLanguages(),
                domain.getAllowedAbilityBonuses(),
                domain.getOriginFeatName(),
                domain.getOriginFeatDescription(),
                domain.getRuleset(),
                domain.getSource()
        );
    }
}
