package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.CharacterNoteRepositoryPort;
import com.dungeoneer.playerCharacter.domain.model.CharacterNote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CharacterNotePersistenceAdapter implements CharacterNoteRepositoryPort {

    private final SpringDataCharacterNoteRepository repository;

    public CharacterNotePersistenceAdapter(SpringDataCharacterNoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public CharacterNote save(CharacterNote note) {
        CharacterNoteJpaEntity entity = CharacterNoteJpaEntity.fromDomain(note);
        CharacterNoteJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<CharacterNote> findByIdAndCharacterId(UUID id, UUID characterId) {
        return repository.findByIdAndCharacterId(id, characterId)
                .map(CharacterNoteJpaEntity::toDomain);
    }

    @Override
    public List<CharacterNote> findAllByCharacterIdOrderByCreatedAtDesc(UUID characterId) {
        return repository.findAllByCharacterIdOrderByCreatedAtDesc(characterId)
                .stream()
                .map(CharacterNoteJpaEntity::toDomain)
                .toList();
    }
}
