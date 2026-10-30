package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;

import java.util.List;

public interface ReferenceDataRepository {

    List<Archetype> findArchetypesByRuleset(Ruleset ruleset);

    List<Lineage> findLineagesByRuleset(Ruleset ruleset);

    List<Background> findBackgroundsByRuleset(Ruleset ruleset);
}
