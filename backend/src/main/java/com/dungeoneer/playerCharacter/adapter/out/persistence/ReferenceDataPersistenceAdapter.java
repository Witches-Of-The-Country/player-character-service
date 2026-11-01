package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Spell;
import org.springframework.stereotype.Component;

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

    public ReferenceDataPersistenceAdapter(
            SpringDataArchetypeRepository archetypeRepository,
            SpringDataLineageRepository lineageRepository,
            SpringDataBackgroundRepository backgroundRepository,
            SpringDataSpellRepository spellRepository,
            SpringDataFeatRepository featRepository,
            SpringDataItemRepository itemRepository
    ) {
        this.archetypeRepository = archetypeRepository;
        this.lineageRepository = lineageRepository;
        this.backgroundRepository = backgroundRepository;
        this.spellRepository = spellRepository;
        this.featRepository = featRepository;
        this.itemRepository = itemRepository;
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
    public List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return spellRepository.findSpellsWithCursor(rulesetStr, level, school, q, afterName, afterId, limit, ownerId);
    }

    @Override
    public List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return featRepository.findFeatsWithCursor(rulesetStr, q, afterName, afterId, limit, ownerId);
    }

    @Override
    public List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return itemRepository.findItemsWithCursor(rulesetStr, category, rarity, q, afterName, afterId, limit, ownerId)
                .stream()
                .map(e -> new Item(
                        e.getId(),
                        e.getRuleset(),
                        e.getSlug(),
                        e.getNameEn(),
                        e.getName(),
                        e.getItemCategory(),
                        e.getItemType(),
                        e.getRarity(),
                        e.isAttunement(),
                        e.getCost(),
                        e.getWeight(),
                        e.getDescription(),
                        e.getSource(),
                        e.getOwnerId()
                ))
                .toList();
    }

    @Override
    public List<Archetype> findArchetypesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return archetypeRepository.findArchetypesWithCursor(rulesetStr, q, afterName, afterId, limit, ownerId)
                .stream()
                .map(ArchetypeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Lineage> findLineagesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit, UUID ownerId) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return lineageRepository.findLineagesWithCursor(rulesetStr, q, afterName, afterId, limit, ownerId)
                .stream()
                .map(LineageJpaEntity::toDomain)
                .toList();
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
                saved.getOwnerId()
        );
    }

    @Override
    public Feat saveFeat(Feat feat) {
        FeatJpaEntity entity = new FeatJpaEntity();
        entity.setId(feat.getId());
        entity.setRuleset(feat.getRuleset());
        entity.setSlug(feat.getSlug());
        entity.setNameEn(feat.getNameEn());
        entity.setName(feat.getName());
        entity.setPrerequisites(feat.getPrerequisites());
        entity.setDescription(feat.getDescription());
        entity.setSource(feat.getSource());
        entity.setOwnerId(feat.getOwnerId());

        FeatJpaEntity saved = featRepository.save(entity);
        return new Feat(
                saved.getId(),
                saved.getRuleset(),
                saved.getSlug(),
                saved.getNameEn(),
                saved.getName(),
                saved.getPrerequisites(),
                saved.getDescription(),
                saved.getSource(),
                saved.getOwnerId()
        );
    }

    @Override
    public Item saveItem(Item item) {
        ItemJpaEntity entity = new ItemJpaEntity();
        entity.setId(item.getId());
        entity.setRuleset(item.getRuleset());
        entity.setSlug(item.getSlug());
        entity.setNameEn(item.getNameEn());
        entity.setName(item.getName());
        entity.setItemCategory(item.getItemCategory());
        entity.setItemType(item.getItemType());
        entity.setRarity(item.getRarity());
        entity.setAttunement(item.isAttunement());
        entity.setCost(item.getCost());
        entity.setWeight(item.getWeight());
        entity.setDescription(item.getDescription());
        entity.setSource(item.getSource());
        entity.setOwnerId(item.getOwnerId());

        ItemJpaEntity saved = itemRepository.save(entity);
        return new Item(
                saved.getId(),
                saved.getRuleset(),
                saved.getSlug(),
                saved.getNameEn(),
                saved.getName(),
                saved.getItemCategory(),
                saved.getItemType(),
                saved.getRarity(),
                saved.isAttunement(),
                saved.getCost(),
                saved.getWeight(),
                saved.getDescription(),
                saved.getSource(),
                saved.getOwnerId()
        );
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
}
