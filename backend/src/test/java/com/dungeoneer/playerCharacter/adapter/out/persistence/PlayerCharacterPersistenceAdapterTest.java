package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.TestcontainersConfiguration;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, PlayerCharacterPersistenceAdapter.class})
class PlayerCharacterPersistenceAdapterTest {

    @Autowired
    private PlayerCharacterPersistenceAdapter adapter;

    private static final UUID DWARF_LINEAGE = UUID.fromString("21000000-0000-0000-0000-000000000003");
    private static final UUID SOLDIER_BACKGROUND = UUID.fromString("22000000-0000-0000-0000-000000000006");
    private static final UUID FIGHTER_ARCHETYPE = UUID.fromString("20000000-0000-0000-0000-000000000005");

    @Test
    @DisplayName("TP-03: Full persistence lifecycle: save, findByIdAndUserId, list, and deleteByIdAndUserId")
    void shouldPerformFullCharacterPersistenceLifecycle() {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        AbilityScores scores = new AbilityScores(16, 14, 15, 10, 12, 8);

        ClassProgression progression = new ClassProgression(
                UUID.randomUUID(),
                characterId,
                FIGHTER_ARCHETYPE,
                null,
                3
        );

        PlayerCharacter character = new PlayerCharacter(
                characterId,
                "Gimli",
                3,
                28,
                28,
                0,
                16,
                2,
                25,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                DWARF_LINEAGE,
                SOLDIER_BACKGROUND,
                userId,
                scores,
                "Brave warrior of the Lonely Mountain",
                HpCalculationMethod.MAX,
                true,
                List.of(progression),
                Instant.now(),
                Instant.now()
        );

        PlayerCharacter saved = adapter.save(character);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(characterId);
        assertThat(saved.getName()).isEqualTo("Gimli");
        assertThat(saved.getNotes()).isEqualTo("Brave warrior of the Lonely Mountain");
        assertThat(saved.getHpCalculationMethod()).isEqualTo(HpCalculationMethod.MAX);
        assertThat(saved.isHasPendingLevelChoices()).isTrue();
        assertThat(saved.getClassProgressions()).hasSize(1);
        assertThat(saved.getClassProgressions().get(0).getArchetypeId()).isEqualTo(FIGHTER_ARCHETYPE);

        // findByIdAndUserId matching
        Optional<PlayerCharacter> found = adapter.findByIdAndUserId(characterId, userId);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Gimli");
        assertThat(found.get().getClassProgressions()).hasSize(1);

        // TE-02: findByIdAndUserId with another user returns empty (IDOR protection)
        UUID otherUser = UUID.randomUUID();
        Optional<PlayerCharacter> notFound = adapter.findByIdAndUserId(characterId, otherUser);
        assertThat(notFound).isEmpty();

        // existsByIdAndUserId
        assertThat(adapter.existsByIdAndUserId(characterId, userId)).isTrue();
        assertThat(adapter.existsByIdAndUserId(characterId, otherUser)).isFalse();

        // findAllByUserIdOrderByUpdatedAtDesc
        List<PlayerCharacter> list = adapter.findAllByUserIdOrderByUpdatedAtDesc(userId);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(characterId);

        // deleteByIdAndUserId
        adapter.deleteByIdAndUserId(characterId, userId);
        assertThat(adapter.findById(characterId)).isEmpty();
    }
}
