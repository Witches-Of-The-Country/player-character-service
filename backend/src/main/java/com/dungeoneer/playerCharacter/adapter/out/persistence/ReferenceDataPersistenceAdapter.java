package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
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
    public List<SpellJpaEntity> findSpellsWithCursor(Ruleset ruleset, Integer level, String school, String q, String afterName, UUID afterId, int limit) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return spellRepository.findSpellsWithCursor(rulesetStr, level, school, q, afterName, afterId, limit);
    }

    @Override
    public List<FeatJpaEntity> findFeatsWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return featRepository.findFeatsWithCursor(rulesetStr, q, afterName, afterId, limit);
    }

    @Override
    public List<Item> findItemsWithCursor(Ruleset ruleset, String category, String rarity, String q, String afterName, UUID afterId, int limit) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return itemRepository.findItemsWithCursor(rulesetStr, category, rarity, q, afterName, afterId, limit)
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
    public List<Archetype> findArchetypesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return archetypeRepository.findArchetypesWithCursor(rulesetStr, q, afterName, afterId, limit)
                .stream()
                .map(ArchetypeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Lineage> findLineagesWithCursor(Ruleset ruleset, String q, String afterName, UUID afterId, int limit) {
        String rulesetStr = (ruleset != null) ? ruleset.name() : null;
        return lineageRepository.findLineagesWithCursor(rulesetStr, q, afterName, afterId, limit)
                .stream()
                .map(LineageJpaEntity::toDomain)
                .toList();
    }
}
