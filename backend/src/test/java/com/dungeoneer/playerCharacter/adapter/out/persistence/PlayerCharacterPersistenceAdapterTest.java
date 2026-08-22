package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.TestcontainersConfiguration;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, PlayerCharacterPersistenceAdapter.class})
class PlayerCharacterPersistenceAdapterTest {

    @Autowired
    private PlayerCharacterPersistenceAdapter adapter;

    @Autowired
    private SpringDataPlayerCharacterRepository springDataRepository;

    @Test
    @DisplayName("should persist player character and nested ability scores, then retrieve by id")
    void shouldPersistAndRetrievePlayerCharacter() {
        AbilityScores scores = new AbilityScores(16, 14, 15, 10, 12, 8);
        PlayerCharacter character = PlayerCharacter.createNew(
                "Gimli",
                1,
                14,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                scores
        );

        PlayerCharacter saved = adapter.save(character);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(character.getId());
        assertThat(saved.getName()).isEqualTo("Gimli");
        assertThat(saved.getAbilityScores()).isNotNull();
        assertThat(saved.getAbilityScores().getStrength()).isEqualTo(16);

        Optional<PlayerCharacter> found = adapter.findById(character.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Gimli");
        assertThat(found.get().getHitPointsMax()).isEqualTo(14);
        assertThat(found.get().getRuleset()).isEqualTo(Ruleset.SRD_2024);
        assertThat(found.get().getAbilityScores()).isNotNull();
        assertThat(found.get().getAbilityScores().getStrength()).isEqualTo(16);
        assertThat(found.get().getAbilityScores().getConstitution()).isEqualTo(15);
    }
}
