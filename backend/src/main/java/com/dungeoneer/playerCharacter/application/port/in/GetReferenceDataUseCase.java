package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.CursorPageResponse;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;
import java.util.UUID;

public interface GetReferenceDataUseCase {

    List<ArchetypeDto> getClasses(Ruleset ruleset);

    List<LineageDto> getLineages(Ruleset ruleset);

    List<BackgroundDto> getBackgrounds(Ruleset ruleset);

    default CursorPageResponse<SpellDto> getSpells(String cursor, int limit, String q, Ruleset ruleset, Integer level, String school) {
        return getSpells(cursor, limit, q, ruleset, level != null ? List.of(level) : null, school != null ? List.of(school) : null, null);
    }

    CursorPageResponse<SpellDto> getSpells(String cursor, int limit, String q, Ruleset ruleset, List<Integer> levels, List<String> schools, UUID ownerId);

    default CursorPageResponse<FeatDto> getFeats(String cursor, int limit, String q, Ruleset ruleset) {
        return getFeats(cursor, limit, q, ruleset, null, null);
    }

    default CursorPageResponse<FeatDto> getFeats(String cursor, int limit, String q, Ruleset ruleset, UUID ownerId) {
        return getFeats(cursor, limit, q, ruleset, null, ownerId);
    }

    CursorPageResponse<FeatDto> getFeats(String cursor, int limit, String q, Ruleset ruleset, List<String> featTypes, UUID ownerId);

    default CursorPageResponse<ItemDto> getItems(String cursor, int limit, String q, Ruleset ruleset, String category, String rarity) {
        return getItems(cursor, limit, q, ruleset, category != null ? List.of(category) : null, rarity != null ? List.of(rarity) : null, null);
    }

    CursorPageResponse<ItemDto> getItems(String cursor, int limit, String q, Ruleset ruleset, List<String> categories, List<String> rarities, UUID ownerId);

    default CursorPageResponse<ArchetypeDto> getClassesPaginated(String cursor, int limit, String q, Ruleset ruleset) {
        return getClassesPaginated(cursor, limit, q, ruleset, null);
    }

    CursorPageResponse<ArchetypeDto> getClassesPaginated(String cursor, int limit, String q, Ruleset ruleset, UUID ownerId);

    default CursorPageResponse<LineageDto> getLineagesPaginated(String cursor, int limit, String q, Ruleset ruleset) {
        return getLineagesPaginated(cursor, limit, q, ruleset, null);
    }

    CursorPageResponse<LineageDto> getLineagesPaginated(String cursor, int limit, String q, Ruleset ruleset, UUID ownerId);

    List<com.dungeoneer.playerCharacter.application.dto.SubclassDto> getSubclasses(UUID archetypeId, Ruleset ruleset);

    List<FeatDto> getProgressionFeats(UUID archetypeId, int level);

    default List<FeatDto> getSubclassProgressionFeats(UUID subclassId, int level) {
        return List.of();
    }
}
