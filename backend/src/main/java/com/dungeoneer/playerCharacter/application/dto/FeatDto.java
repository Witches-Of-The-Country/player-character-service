package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.FeatType;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;

import java.util.UUID;

public record FeatDto(
        UUID id,
        String ruleset,
        String slug,
        String nameEn,
        String name,
        String prerequisites,
        String description,
        String source,
        FeatType featType,
        MechanicalEffects mechanicalEffects
) {
    public FeatDto(
            UUID id,
            String ruleset,
            String slug,
            String nameEn,
            String name,
            String prerequisites,
            String description,
            String source
    ) {
        this(id, ruleset, slug, nameEn, name, prerequisites, description, source, FeatType.GENERAL, MechanicalEffects.empty());
    }

    public static FeatDto fromEntity(FeatJpaEntity entity) {
        return fromDomain(entity.toDomain());
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
                feat.getSource(),
                feat.getFeatType(),
                feat.getMechanicalEffects()
        );
    }
}
