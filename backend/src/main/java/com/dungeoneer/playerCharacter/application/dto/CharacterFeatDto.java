package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;

import java.util.UUID;

public record CharacterFeatDto(
        UUID id,
        UUID characterId,
        UUID featId,
        String name,
        String featType,
        String source,
        String description,
        int unlockedAtLevel,
        Integer currentCharges,
        Integer maxCharges,
        MechanicalEffects mechanicalEffects
) {
    public static CharacterFeatDto fromDomain(CharacterFeat charFeat, Feat feat) {
        String source = feat != null && feat.getSource() != null ? feat.getSource() : "ORIGIN";
        return new CharacterFeatDto(
                charFeat.getId(),
                charFeat.getCharacterId(),
                charFeat.getFeatId(),
                feat != null ? feat.getName() : "Talento",
                feat != null ? feat.getFeatType().name() : "GENERAL",
                source,
                feat != null ? feat.getDescription() : "",
                charFeat.getUnlockedAtLevel(),
                charFeat.getCurrentCharges(),
                charFeat.getMaxCharges(),
                feat != null ? feat.getMechanicalEffects() : null
        );
    }
}
