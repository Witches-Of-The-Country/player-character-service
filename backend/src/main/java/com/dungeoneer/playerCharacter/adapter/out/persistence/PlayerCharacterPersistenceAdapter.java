package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepository;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerCharacterPersistenceAdapter implements PlayerCharacterRepository {

    private final SpringDataPlayerCharacterRepository repository;

    public PlayerCharacterPersistenceAdapter(SpringDataPlayerCharacterRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlayerCharacter save(PlayerCharacter character) {
        PlayerCharacterJpaEntity jpaEntity = PlayerCharacterJpaEntity.fromDomain(character);
        PlayerCharacterJpaEntity savedEntity = repository.save(jpaEntity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<PlayerCharacter> findById(UUID id) {
        return repository.findById(id).map(PlayerCharacterJpaEntity::toDomain);
    }
}
