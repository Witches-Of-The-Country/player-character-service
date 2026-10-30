package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferenceDataRepository {

    List<Archetype> findArchetypesByRuleset(Ruleset ruleset);

    List<Lineage> findLineagesByRuleset(Ruleset ruleset);

    List<Background> findBackgroundsByRuleset(Ruleset ruleset);

    Optional<Archetype> findArchetypeById(UUID id);

    Optional<Lineage> findLineageById(UUID id);

    Optional<Background> findBackgroundById(UUID id);
}
