package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.CharacterItem;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerCharacterPersistenceAdapter implements PlayerCharacterRepositoryPort {

    private final SpringDataPlayerCharacterRepository repository;
    private final SpringDataCharacterItemRepository characterItemRepository;
    private final SpringDataCharacterFeatRepository characterFeatRepository;

    public PlayerCharacterPersistenceAdapter(
            SpringDataPlayerCharacterRepository repository,
            SpringDataCharacterItemRepository characterItemRepository,
            SpringDataCharacterFeatRepository characterFeatRepository
    ) {
        this.repository = repository;
        this.characterItemRepository = characterItemRepository;
        this.characterFeatRepository = characterFeatRepository;
    }

    @Override
    public PlayerCharacter save(PlayerCharacter character) {
        PlayerCharacterJpaEntity entity = PlayerCharacterJpaEntity.fromDomain(character);
        PlayerCharacterJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<PlayerCharacter> findById(UUID id) {
        return repository.findById(id)
                .map(entity -> {
                    List<CharacterItem> items = findItemsByCharacterId(id);
                    List<CharacterFeat> feats = findFeatsByCharacterId(id);
                    PlayerCharacter domain = entity.toDomain();
                    return domain.withItems(items).withFeats(feats);
                });
    }

    @Override
    public Optional<PlayerCharacter> findByIdAndUserId(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId)
                .map(entity -> {
                    List<CharacterItem> items = findItemsByCharacterId(id);
                    List<CharacterFeat> feats = findFeatsByCharacterId(id);
                    PlayerCharacter domain = entity.toDomain();
                    return domain.withItems(items).withFeats(feats);
                });
    }

    @Override
    public List<PlayerCharacter> findAllByUserIdOrderByUpdatedAtDesc(UUID userId) {
        return repository.findAllByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(PlayerCharacterJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteByIdAndUserId(UUID id, UUID userId) {
        characterItemRepository.deleteByCharacterId(id);
        characterFeatRepository.deleteByCharacterId(id);
        repository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return repository.existsByIdAndUserId(id, userId);
    }

    @Override
    public List<CharacterItem> findItemsByCharacterId(UUID characterId) {
        return characterItemRepository.findByCharacterId(characterId).stream()
                .map(CharacterItemJpaEntity::toDomain)
                .toList();
    }

    @Override
    public CharacterItem saveCharacterItem(CharacterItem item) {
        PlayerCharacterJpaEntity charRef = repository.getReferenceById(item.getCharacterId());
        CharacterItemJpaEntity entity = CharacterItemJpaEntity.fromDomain(item, charRef);
        CharacterItemJpaEntity saved = characterItemRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<CharacterItem> findCharacterItemByIdAndCharacterId(UUID itemId, UUID characterId) {
        return characterItemRepository.findByIdAndCharacterId(itemId, characterId)
                .map(CharacterItemJpaEntity::toDomain);
    }

    @Override
    public void deleteCharacterItem(UUID itemId, UUID characterId) {
        Optional<CharacterItemJpaEntity> byId = characterItemRepository.findByIdAndCharacterId(itemId, characterId);
        if (byId.isEmpty()) {
            List<CharacterItemJpaEntity> byItem = characterItemRepository.findByCharacterIdAndItemId(characterId, itemId);
            if (!byItem.isEmpty()) {
                byId = Optional.of(byItem.get(0));
            }
        }
        byId.ifPresent(ci -> {
            UUID actualId = ci.getId();
            characterItemRepository.delete(ci);
            characterItemRepository.flush();
            repository.findById(characterId).ifPresent(pc -> {
                if (pc.getCharacterItems() != null) {
                    pc.getCharacterItems().removeIf(item -> item.getId().equals(actualId));
                }
            });
        });
    }

    @Override
    public List<CharacterFeat> findFeatsByCharacterId(UUID characterId) {
        return characterFeatRepository.findByCharacterId(characterId).stream()
                .map(CharacterFeatJpaEntity::toDomain)
                .toList();
    }

    @Override
    public CharacterFeat saveCharacterFeat(CharacterFeat feat) {
        PlayerCharacterJpaEntity charRef = repository.getReferenceById(feat.getCharacterId());
        CharacterFeatJpaEntity entity = CharacterFeatJpaEntity.fromDomain(feat, charRef);
        CharacterFeatJpaEntity saved = characterFeatRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<CharacterFeat> findCharacterFeatByIdAndCharacterId(UUID featId, UUID characterId) {
        return characterFeatRepository.findByIdAndCharacterId(featId, characterId)
                .map(CharacterFeatJpaEntity::toDomain);
    }
}
