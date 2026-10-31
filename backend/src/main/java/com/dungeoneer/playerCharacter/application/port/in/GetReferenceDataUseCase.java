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

public interface GetReferenceDataUseCase {

    List<ArchetypeDto> getClasses(Ruleset ruleset);

    List<LineageDto> getLineages(Ruleset ruleset);

    List<BackgroundDto> getBackgrounds(Ruleset ruleset);

    CursorPageResponse<SpellDto> getSpells(String cursor, int limit, String q, Ruleset ruleset, Integer level, String school);

    CursorPageResponse<FeatDto> getFeats(String cursor, int limit, String q, Ruleset ruleset);

    CursorPageResponse<ItemDto> getItems(String cursor, int limit, String q, Ruleset ruleset, String category, String rarity);

    CursorPageResponse<ArchetypeDto> getClassesPaginated(String cursor, int limit, String q, Ruleset ruleset);

    CursorPageResponse<LineageDto> getLineagesPaginated(String cursor, int limit, String q, Ruleset ruleset);
}
