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
        return fromDomain(
                charFeat,
                feat,
                charFeat != null ? charFeat.getCurrentCharges() : null,
                charFeat != null ? charFeat.getMaxCharges() : null
        );
    }

    public static CharacterFeatDto fromDomain(
            CharacterFeat charFeat,
            Feat feat,
            Integer currentCharges,
            Integer maxCharges
    ) {
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
                currentCharges,
                maxCharges,
                feat != null ? feat.getMechanicalEffects() : null
        );
    }
}
