package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReferenceDataPersistenceAdapter implements ReferenceDataRepository {

    private final SpringDataArchetypeRepository archetypeRepository;
    private final SpringDataLineageRepository lineageRepository;
    private final SpringDataBackgroundRepository backgroundRepository;

    public ReferenceDataPersistenceAdapter(
            SpringDataArchetypeRepository archetypeRepository,
            SpringDataLineageRepository lineageRepository,
            SpringDataBackgroundRepository backgroundRepository
    ) {
        this.archetypeRepository = archetypeRepository;
        this.lineageRepository = lineageRepository;
        this.backgroundRepository = backgroundRepository;
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
}
