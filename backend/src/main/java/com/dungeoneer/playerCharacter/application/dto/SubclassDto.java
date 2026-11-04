package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.Subclass;

import java.util.UUID;

public record SubclassDto(
        UUID id,
        UUID archetypeId,
        String ruleset,
        String slug,
        String nameEn,
        String name,
        String description,
        String source
) {
    public static SubclassDto fromDomain(Subclass subclass) {
        return new SubclassDto(
                subclass.getId(),
                subclass.getArchetypeId(),
                subclass.getRuleset() != null ? subclass.getRuleset().name() : null,
                subclass.getSlug(),
                subclass.getNameEn(),
                subclass.getName(),
                subclass.getDescription(),
                subclass.getSource()
        );
    }
}
