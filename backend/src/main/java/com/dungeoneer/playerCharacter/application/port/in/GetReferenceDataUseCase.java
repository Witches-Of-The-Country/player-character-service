package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;

public interface GetReferenceDataUseCase {

    List<ArchetypeDto> getClasses(Ruleset ruleset);

    List<LineageDto> getLineages(Ruleset ruleset);

    List<BackgroundDto> getBackgrounds(Ruleset ruleset);
}
