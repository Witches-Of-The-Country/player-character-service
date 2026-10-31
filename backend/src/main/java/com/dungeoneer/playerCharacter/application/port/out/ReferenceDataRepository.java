package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity;
import com.dungeoneer.playerCharacter.adapter.out.persistence.SpellJpaEntity;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Item;
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

    List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit);

    List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit);

    List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit);

    List<Archetype> findArchetypesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit);

    List<Lineage> findLineagesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit);
}
