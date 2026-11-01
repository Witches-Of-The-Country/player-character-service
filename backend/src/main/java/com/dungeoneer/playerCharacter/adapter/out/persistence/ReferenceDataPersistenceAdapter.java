package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Spell;
import com.dungeoneer.playerCharacter.domain.model.Subclass;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReferenceDataPersistenceAdapter implements ReferenceDataRepository {

    private final SpringDataArchetypeRepository archetypeRepository;
    private final SpringDataLineageRepository lineageRepository;
    private final SpringDataBackgroundRepository backgroundRepository;
    private final SpringDataSpellRepository spellRepository;
    private final SpringDataFeatRepository featRepository;
    private final SpringDataItemRepository itemRepository;
    private final SpringDataSubclassRepository subclassRepository;
    private final SpringDataArchetypeFeatRepository archetypeFeatRepository;
    private final SpringDataSubclassFeatRepository subclassFeatRepository;
    private final SpringDataLineageFeatRepository lineageFeatRepository;

    public ReferenceDataPersistenceAdapter(
            SpringDataArchetypeRepository archetypeRepository,
            SpringDataLineageRepository lineageRepository,
            SpringDataBackgroundRepository backgroundRepository,
            SpringDataSpellRepository spellRepository,
            SpringDataFeatRepository featRepository,
            SpringDataItemRepository itemRepository,
            SpringDataSubclassRepository subclassRepository,
            SpringDataArchetypeFeatRepository archetypeFeatRepository,
            SpringDataSubclassFeatRepository subclassFeatRepository,
            SpringDataLineageFeatRepository lineageFeatRepository
    ) {
        this.archetypeRepository = archetypeRepository;
        this.lineageRepository = lineageRepository;
        this.backgroundRepository = backgroundRepository;
        this.spellRepository = spellRepository;
        this.featRepository = featRepository;
        this.itemRepository = itemRepository;
        this.subclassRepository = subclassRepository;
        this.archetypeFeatRepository = archetypeFeatRepository;
        this.subclassFeatRepository = subclassFeatRepository;
        this.lineageFeatRepository = lineageFeatRepository;
    }

    @Override
    public List<Archetype> findArchetypesByRuleset(Ruleset ruleset) {
        List<ArchetypeJpaEntity> entities = (ruleset == null)
                ? archetypeRepository.findAll()
                : archetypeRepository.findByRuleset(ruleset);
        return entities.stream().map(ArchetypeJpaEntity::toDomain).toList();
    }

    @Override
    public List<Lineage> findLineagesByRuleset(Ruleset ruleset) {
        List<LineageJpaEntity> entities = (ruleset == null)
                ? lineageRepository.findAll()
                : lineageRepository.findByRuleset(ruleset);
        return entities.stream().map(LineageJpaEntity::toDomain).toList();
    }

    @Override
    public List<Background> findBackgroundsByRuleset(Ruleset ruleset) {
        List<BackgroundJpaEntity> entities = (ruleset == null)
                ? backgroundRepository.findAll()
                : backgroundRepository.findByRuleset(ruleset);
        return entities.stream().map(BackgroundJpaEntity::toDomain).toList();
    }

    @Override
    public Optional<Archetype> findArchetypeById(UUID id) {
        return archetypeRepository.findById(id).map(ArchetypeJpaEntity::toDomain);
    }

    @Override
    public Optional<Lineage> findLineageById(UUID id) {
        return lineageRepository.findById(id).map(LineageJpaEntity::toDomain);
    }

    @Override
    public Optional<Background> findBackgroundById(UUID id) {
        return backgroundRepository.findById(id).map(BackgroundJpaEntity::toDomain);
    }

    @Override
    public Optional<Item> findItemById(UUID id) {
        return itemRepository.findById(id).map(ItemJpaEntity::toDomain);
    }

    @Override
    public Optional<Feat> findFeatById(UUID id) {
        return featRepository.findById(id).map(FeatJpaEntity::toDomain);
    }

    @Override
    public Optional<Subclass> findSubclassById(UUID id) {
        return subclassRepository.findById(id).map(SubclassJpaEntity::toDomain);
    }

    @Override
    public List<Subclass> findSubclassesByArchetypeId(UUID archetypeId, Ruleset ruleset, UUID ownerId) {
        return subclassRepository.findAccessibleSubclasses(archetypeId, ruleset, ownerId).stream()
                .map(SubclassJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Feat> findArchetypeFeats(UUID archetypeId, int unlockLevel) {
        List<ArchetypeFeatJpaEntity> links = archetypeFeatRepository.findByArchetypeIdAndUnlockLevel(archetypeId, unlockLevel);
        List<Feat> result = new ArrayList<>();
        for (ArchetypeFeatJpaEntity link : links) {
            featRepository.findById(link.getFeatId()).ifPresent(f -> result.add(f.toDomain()));
        }
        return result;
    }

    @Override
    public List<Feat> findSubclassFeats(UUID subclassId, int unlockLevel) {
        List<SubclassFeatJpaEntity> links = subclassFeatRepository.findBySubclassIdAndUnlockLevel(subclassId, unlockLevel);
        List<Feat> result = new ArrayList<>();
        for (SubclassFeatJpaEntity link : links) {
            featRepository.findById(link.getFeatId()).ifPresent(f -> result.add(f.toDomain()));
        }
        return result;
    }

    @Override
    public List<Feat> findAllFeatsForArchetype(UUID archetypeId) {
        List<ArchetypeFeatJpaEntity> links = archetypeFeatRepository.findByArchetypeIdOrderByUnlockLevelAsc(archetypeId);
        List<Feat> result = new ArrayList<>();
        for (ArchetypeFeatJpaEntity link : links) {
            featRepository.findById(link.getFeatId()).ifPresent(f -> result.add(f.toDomain()));
        }
        return result;
    }

    @Override
    public List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = ruleset != null ? ruleset.name() : null;
        return spellRepository.findSpellsWithCursor(rulesetStr, level, school, q, afterName, afterId, limit, ownerId);
    }

    @Override
    public List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, List<String> featTypes, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = ruleset != null ? ruleset.name() : null;
        String featTypesStr = (featTypes != null && !featTypes.isEmpty()) ? String.join(",", featTypes) : null;
        return featRepository.findFeatsWithCursor(rulesetStr, featTypesStr, q, afterName, afterId, limit, ownerId);
    }

    @Override
    public List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = ruleset != null ? ruleset.name() : null;
        return itemRepository.findItemsWithCursor(rulesetStr, category, rarity, q, afterName, afterId, limit, ownerId)
                .stream().map(ItemJpaEntity::toDomain).toList();
    }

    @Override
    public List<Archetype> findArchetypesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = ruleset != null ? ruleset.name() : null;
        return archetypeRepository.findArchetypesWithCursor(rulesetStr, q, afterName, afterId, limit, ownerId)
                .stream().map(ArchetypeJpaEntity::toDomain).toList();
    }

    @Override
    public List<Lineage> findLineagesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = ruleset != null ? ruleset.name() : null;
        return lineageRepository.findLineagesWithCursor(rulesetStr, q, afterName, afterId, limit, ownerId)
                .stream().map(LineageJpaEntity::toDomain).toList();
    }

    @Override
    public Spell saveSpell(Spell spell) {
        SpellJpaEntity entity = new SpellJpaEntity();
        entity.setId(spell.getId());
        entity.setRuleset(spell.getRuleset());
        entity.setSlug(spell.getSlug());
        entity.setNameEn(spell.getNameEn());
        entity.setName(spell.getName());
        entity.setLevel(spell.getLevel());
        entity.setSchool(spell.getSchool());
        entity.setCastingTime(spell.getCastingTime());
        entity.setRangeArea(spell.getRangeArea());
        entity.setComponents(spell.getComponents());
        entity.setDuration(spell.getDuration());
        entity.setDescription(spell.getDescription());
        entity.setSource(spell.getSource());
        entity.setOwnerId(spell.getOwnerId());
        entity.setMechanicalProperties(spell.getMechanicalProperties());

        SpellJpaEntity saved = spellRepository.save(entity);
        return new Spell(
                saved.getId(),
                saved.getRuleset(),
                saved.getSlug(),
                saved.getNameEn(),
                saved.getName(),
                saved.getLevel(),
                saved.getSchool(),
                saved.getCastingTime(),
                saved.getRangeArea(),
                saved.getComponents(),
                saved.getDuration(),
                saved.getDescription(),
                saved.getSource(),
                saved.getOwnerId(),
                saved.getMechanicalProperties()
        );
    }

    @Override
    public Feat saveFeat(Feat feat) {
        FeatJpaEntity entity = FeatJpaEntity.fromDomain(feat);
        FeatJpaEntity saved = featRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Item saveItem(Item item) {
        ItemJpaEntity entity = ItemJpaEntity.fromDomain(item);
        ItemJpaEntity saved = itemRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Archetype saveArchetype(Archetype archetype) {
        ArchetypeJpaEntity entity = ArchetypeJpaEntity.fromDomain(archetype);
        ArchetypeJpaEntity saved = archetypeRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Lineage saveLineage(Lineage lineage) {
        LineageJpaEntity entity = LineageJpaEntity.fromDomain(lineage);
        LineageJpaEntity saved = lineageRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Subclass saveSubclass(Subclass subclass) {
        SubclassJpaEntity entity = SubclassJpaEntity.fromDomain(subclass);
        SubclassJpaEntity saved = subclassRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void saveArchetypeFeatLink(UUID archetypeId, UUID featId, int unlockLevel) {
        ArchetypeFeatJpaEntity link = new ArchetypeFeatJpaEntity(UUID.randomUUID(), archetypeId, featId, unlockLevel);
        archetypeFeatRepository.save(link);
    }

    @Override
    public void saveSubclassFeatLink(UUID subclassId, UUID featId, int unlockLevel) {
        SubclassFeatJpaEntity link = new SubclassFeatJpaEntity(UUID.randomUUID(), subclassId, featId, unlockLevel);
        subclassFeatRepository.save(link);
    }

    @Override
    public void saveLineageFeatLink(UUID lineageId, UUID featId, int unlockLevel) {
        LineageFeatJpaEntity link = new LineageFeatJpaEntity(lineageId, featId, unlockLevel);
        lineageFeatRepository.save(link);
    }

    @Override
    public List<Feat> findLineageFeats(UUID lineageId, int unlockLevel) {
        List<LineageFeatJpaEntity> links = lineageFeatRepository.findByLineageIdAndUnlockLevel(lineageId, unlockLevel);
        List<Feat> result = new ArrayList<>();
        for (LineageFeatJpaEntity link : links) {
            featRepository.findById(link.getFeatId()).ifPresent(f -> result.add(f.toDomain()));
        }
        return result;
    }

    @Override
    public List<Feat> findAllFeatsForLineage(UUID lineageId) {
        List<LineageFeatJpaEntity> links = lineageFeatRepository.findByLineageId(lineageId);
        List<Feat> result = new ArrayList<>();
        for (LineageFeatJpaEntity link : links) {
            featRepository.findById(link.getFeatId()).ifPresent(f -> result.add(f.toDomain()));
        }
        return result;
    }
}
