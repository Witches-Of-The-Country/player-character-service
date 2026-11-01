package com.dungeoneer.playerCharacter.application.port.in;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewArchetypeRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewItemRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewLineageRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSpellRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSubclassRequest;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.dto.SubclassDto;

import java.util.UUID;

public interface CreateHomebrewUseCase {

    SpellDto createSpell(UUID ownerId, CreateHomebrewSpellRequest request);

    FeatDto createFeat(UUID ownerId, CreateHomebrewFeatRequest request);

    ItemDto createItem(UUID ownerId, CreateHomebrewItemRequest request);

    ArchetypeDto createArchetype(UUID ownerId, CreateHomebrewArchetypeRequest request);

    LineageDto createLineage(UUID ownerId, CreateHomebrewLineageRequest request);

    SubclassDto createSubclass(UUID ownerId, CreateHomebrewSubclassRequest request);
}
