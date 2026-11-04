package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.AbilityRollRepositoryPort;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AbilityRollPersistenceAdapter implements AbilityRollRepositoryPort {

    private final SpringDataAbilityRollRepository repository;

    public AbilityRollPersistenceAdapter(SpringDataAbilityRollRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AbilityRoll> findUnusedByUserId(UUID userId) {
        return repository.findFirstByUserIdAndUsedFalse(userId).map(AbilityRollJpaEntity::toDomain);
    }

    @Override
    public AbilityRoll save(AbilityRoll roll) {
        AbilityRollJpaEntity entity = AbilityRollJpaEntity.fromDomain(roll);
        AbilityRollJpaEntity saved = repository.save(entity);
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
    public Optional<AbilityRoll> findById(UUID id) {
        return repository.findById(id).map(AbilityRollJpaEntity::toDomain);
    }
}
