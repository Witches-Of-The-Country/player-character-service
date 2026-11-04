package com.dungeoneer.playerCharacter.application.port.out;

import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.CharacterItem;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerCharacterRepositoryPort {

    PlayerCharacter save(PlayerCharacter character);

    Optional<PlayerCharacter> findById(UUID id);

    Optional<PlayerCharacter> findByIdAndUserId(UUID id, UUID userId);

    List<PlayerCharacter> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    List<CharacterItem> findItemsByCharacterId(UUID characterId);

    CharacterItem saveCharacterItem(CharacterItem item);

    Optional<CharacterItem> findCharacterItemByIdAndCharacterId(UUID itemId, UUID characterId);

    void deleteCharacterItem(UUID itemId, UUID characterId);

    List<CharacterFeat> findFeatsByCharacterId(UUID characterId);

    CharacterFeat saveCharacterFeat(CharacterFeat feat);

    Optional<CharacterFeat> findCharacterFeatByIdAndCharacterId(UUID featId, UUID characterId);
}
