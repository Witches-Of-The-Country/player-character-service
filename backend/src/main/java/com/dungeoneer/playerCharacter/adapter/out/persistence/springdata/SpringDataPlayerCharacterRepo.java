package com.dungeoneer.playerCharacter.adapter.out.persistence.springdata;

import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlayerCharacterRepo extends JpaRepository<PlayerCharacter, Integer> {
}
