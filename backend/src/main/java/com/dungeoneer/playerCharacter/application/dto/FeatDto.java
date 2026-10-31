package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity;

import java.util.UUID;

public record FeatDto(
        UUID id,
        String ruleset,
        String slug,
        String nameEn,
        String name,
        String prerequisites,
        String description,
        String source
) {
    public static FeatDto fromEntity(FeatJpaEntity entity) {
        return new FeatDto(
                entity.getId(),
                entity.getRuleset() != null ? entity.getRuleset().name() : null,
                entity.getSlug(),
                entity.getNameEn(),
                entity.getName(),
                entity.getPrerequisites(),
                entity.getDescription(),
                entity.getSource()
        );
    }
}
