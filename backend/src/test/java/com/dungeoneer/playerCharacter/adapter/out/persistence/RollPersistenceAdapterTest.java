package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.TestcontainersConfiguration;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import com.dungeoneer.playerCharacter.domain.model.HpRoll;
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
@Import({TestcontainersConfiguration.class, AbilityRollPersistenceAdapter.class, HpRollPersistenceAdapter.class})
class RollPersistenceAdapterTest {

    @Autowired
    private AbilityRollPersistenceAdapter abilityRollAdapter;

    @Autowired
    private HpRollPersistenceAdapter hpRollAdapter;

    private static final UUID FIGHTER_ARCHETYPE = UUID.fromString("20000000-0000-0000-0000-000000000005");

    @Test
    @DisplayName("TP-02: AbilityRoll persistence, unused lookup and atomic markAsUsed transition")
    void shouldPersistAndConsumeAbilityRoll() {
        UUID userId = UUID.randomUUID();
        AbilityRoll roll = AbilityRoll.createNew(userId, List.of(16, 14, 13, 12, 10, 8), "[[4,4,4,4]]");

        AbilityRoll saved = abilityRollAdapter.save(roll);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isUsed()).isFalse();

        // Query unused roll
        Optional<AbilityRoll> unused = abilityRollAdapter.findUnusedByUserId(userId);
        assertThat(unused).isPresent();
        assertThat(unused.get().getId()).isEqualTo(saved.getId());
        assertThat(unused.get().getRolledScores()).containsExactly(16, 14, 13, 12, 10, 8);

        // Mark as used
        abilityRollAdapter.markAsUsed(saved.getId());

        // Subsequent unused lookup returns empty
        Optional<AbilityRoll> consumed = abilityRollAdapter.findUnusedByUserId(userId);
        assertThat(consumed).isEmpty();
    }

    @Test
    @DisplayName("TP-02: HpRoll persistence, unused lookup and atomic markAsUsed transition")
    void shouldPersistAndConsumeHpRoll() {
        UUID userId = UUID.randomUUID();
        HpRoll hpRoll = HpRoll.createNew(userId, FIGHTER_ARCHETYPE, 3, List.of(8, 7));

        HpRoll saved = hpRollAdapter.save(hpRoll);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isUsed()).isFalse();

        // Query unused HP roll
        Optional<HpRoll> unused = hpRollAdapter.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, FIGHTER_ARCHETYPE, 3);
        assertThat(unused).isPresent();
        assertThat(unused.get().getId()).isEqualTo(saved.getId());
        assertThat(unused.get().getRolledHpPerLevel()).containsExactly(8, 7);

        // Mark as used
        hpRollAdapter.markAsUsed(saved.getId());

        // Subsequent unused lookup returns empty
        Optional<HpRoll> consumed = hpRollAdapter.findUnusedByUserIdAndArchetypeIdAndTargetLevel(userId, FIGHTER_ARCHETYPE, 3);
        assertThat(consumed).isEmpty();
    }
}
