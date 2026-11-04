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
import com.dungeoneer.playerCharacter.domain.model.Subclass;

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

    Optional<Spell> findSpellById(UUID id);

    Optional<Item> findItemById(UUID id);

    Optional<Feat> findFeatById(UUID id);

    Optional<Subclass> findSubclassById(UUID id);

    default List<Subclass> findSubclassesByArchetypeId(UUID archetypeId) {
        return findSubclassesByArchetypeId(archetypeId, null, null);
    }

    List<Subclass> findSubclassesByArchetypeId(UUID archetypeId, Ruleset ruleset, UUID ownerId);

    List<Feat> findArchetypeFeats(UUID archetypeId, int unlockLevel);

    List<Feat> findSubclassFeats(UUID subclassId, int unlockLevel);

    default List<Feat> findProgressionFeats(UUID archetypeId, int unlockLevel) {
        return findArchetypeFeats(archetypeId, unlockLevel);
    }

    default List<Feat> findSubclassProgressionFeats(UUID subclassId, int unlockLevel) {
        return findSubclassFeats(subclassId, unlockLevel);
    }

    List<Feat> findAllFeatsForArchetype(UUID archetypeId);

    default List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit) {
        return findSpellsWithCursor(ruleset, level != null ? List.of(level) : null, school != null ? List.of(school) : null, q, afterName, afterId, limit, null);
    }

    List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, List<Integer> levels, List<String> schools, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    default List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        return findFeatsWithCursor(ruleset, q, afterName, afterId, limit, null);
    }

    default List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        return findFeatsWithCursor(ruleset, null, q, afterName, afterId, limit, ownerId);
    }

    List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, List<String> featTypes, String q, String afterName, UUID afterId, int limit, UUID ownerId);

    default List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit) {
        return findItemsWithCursor(ruleset, category != null ? List.of(category) : null, rarity != null ? List.of(rarity) : null, q, afterName, afterId, limit, null);
    }

    List<Item> findItemsWithCursor(Ruleset ruleset, List<String> categories, List<String> rarities, String q, String afterName, UUID afterId, int limit, UUID ownerId);

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

    Subclass saveSubclass(Subclass subclass);

    default void saveArchetypeFeatLink(UUID archetypeId, UUID featId, int unlockLevel) {
    }

    default void saveSubclassFeatLink(UUID subclassId, UUID featId, int unlockLevel) {
    }

    default void saveLineageFeatLink(UUID lineageId, UUID featId, int unlockLevel) {
    }

    default List<Feat> findLineageFeats(UUID lineageId, int unlockLevel) {
        return List.of();
    }

    default List<Feat> findAllFeatsForLineage(UUID lineageId) {
        return List.of();
    }
}
