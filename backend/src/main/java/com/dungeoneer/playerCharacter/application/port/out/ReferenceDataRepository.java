package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity;
import com.dungeoneer.playerCharacter.adapter.out.persistence.SpellJpaEntity;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Spell;

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

    default List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit) {
        return findSpellsWithCursor(ruleset, level, school, q, afterName, afterId, limit, null);
    }

    List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    default List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        return findFeatsWithCursor(ruleset, q, afterName, afterId, limit, null);
    }

    List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    default List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit) {
        return findItemsWithCursor(ruleset, category, rarity, q, afterName, afterId, limit, null);
    }

    List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    default List<Archetype> findArchetypesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        return findArchetypesWithCursor(ruleset, q, afterName, afterId, limit, null);
    }

    List<Archetype> findArchetypesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    default List<Lineage> findLineagesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        return findLineagesWithCursor(ruleset, q, afterName, afterId, limit, null);
    }

    List<Lineage> findLineagesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    Spell saveSpell(Spell spell);

    Feat saveFeat(Feat feat);

    Item saveItem(Item item);

    Archetype saveArchetype(Archetype archetype);

    Lineage saveLineage(Lineage lineage);
}
