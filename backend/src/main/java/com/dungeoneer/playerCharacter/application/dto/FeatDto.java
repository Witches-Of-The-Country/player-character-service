package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity;
import com.dungeoneer.playerCharacter.domain.model.Feat;

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

    public static FeatDto fromDomain(Feat feat) {
        return new FeatDto(
                feat.getId(),
                feat.getRuleset() != null ? feat.getRuleset().name() : null,
                feat.getSlug(),
                feat.getNameEn(),
                feat.getName(),
                feat.getPrerequisites(),
                feat.getDescription(),
                feat.getSource()
        );
    }
}
