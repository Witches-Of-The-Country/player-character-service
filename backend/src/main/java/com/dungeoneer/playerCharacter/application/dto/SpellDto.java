package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.adapter.out.persistence.SpellJpaEntity;
import com.dungeoneer.playerCharacter.domain.model.Spell;

import java.util.UUID;

public record SpellDto(
        UUID id,
        String ruleset,
        String slug,
        String nameEn,
        String name,
        int level,
        String school,
        String castingTime,
        String rangeArea,
        String components,
        String duration,
        String description,
        String source
) {
    public static SpellDto fromEntity(SpellJpaEntity entity) {
        return new SpellDto(
                entity.getId(),
                entity.getRuleset() != null ? entity.getRuleset().name() : null,
                entity.getSlug(),
                entity.getNameEn(),
                entity.getName(),
                entity.getLevel(),
                entity.getSchool(),
                entity.getCastingTime(),
                entity.getRangeArea(),
                entity.getComponents(),
                entity.getDuration(),
                entity.getDescription(),
                entity.getSource()
        );
    }

    public static SpellDto fromDomain(Spell spell) {
        return new SpellDto(
                spell.getId(),
                spell.getRuleset() != null ? spell.getRuleset().name() : null,
                spell.getSlug(),
                spell.getNameEn(),
                spell.getName(),
                spell.getLevel(),
                spell.getSchool(),
                spell.getCastingTime(),
                spell.getRangeArea(),
                spell.getComponents(),
                spell.getDuration(),
                spell.getDescription(),
                spell.getSource()
        );
    }
}
