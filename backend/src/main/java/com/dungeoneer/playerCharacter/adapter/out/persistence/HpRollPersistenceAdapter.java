package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.domain.model.HpRoll;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class HpRollPersistenceAdapter implements HpRollRepositoryPort {

    private final SpringDataHpRollRepository repository;

    public HpRollPersistenceAdapter(SpringDataHpRollRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<HpRoll> findUnusedByUserIdAndArchetypeIdAndTargetLevel(UUID userId, UUID archetypeId, int targetLevel) {
        return repository.findFirstByUserIdAndArchetypeIdAndTargetLevelAndUsedFalse(userId, archetypeId, targetLevel)
                .map(HpRollJpaEntity::toDomain);
    }

    @Override
    public HpRoll save(HpRoll roll) {
        HpRollJpaEntity entity = HpRollJpaEntity.fromDomain(roll);
        HpRollJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void markAsUsed(UUID id) {
        repository.findById(id).ifPresent(entity -> {
            entity.setUsed(true);
            repository.save(entity);
        });
    }

    @Override
    public Optional<HpRoll> findById(UUID id) {
        return repository.findById(id).map(HpRollJpaEntity::toDomain);
    }
}
