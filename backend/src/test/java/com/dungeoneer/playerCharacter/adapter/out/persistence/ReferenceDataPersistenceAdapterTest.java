package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.TestcontainersConfiguration;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, ReferenceDataPersistenceAdapter.class})
class ReferenceDataPersistenceAdapterTest {

    @Autowired
    private ReferenceDataPersistenceAdapter adapter;

    @Test
    @DisplayName("should query archetypes seeded by Flyway for SRD_2014 and SRD_2024")
    void shouldFindArchetypesByRuleset() {
        List<Archetype> srd2014Classes = adapter.findArchetypesByRuleset(Ruleset.SRD_2014);
        assertThat(srd2014Classes).hasSize(13);
        assertThat(srd2014Classes).allMatch(a -> a.getRuleset() == Ruleset.SRD_2014);
        assertThat(srd2014Classes).extracting(Archetype::getName).contains("Fighter", "Wizard", "Rogue");

        List<Archetype> srd2024Classes = adapter.findArchetypesByRuleset(Ruleset.SRD_2024);
        assertThat(srd2024Classes).hasSize(13);
        assertThat(srd2024Classes).allMatch(a -> a.getRuleset() == Ruleset.SRD_2024);

        List<Archetype> all = adapter.findArchetypesByRuleset(null);
        assertThat(all).hasSize(26);
    }

    @Test
    @DisplayName("should query lineages seeded by Flyway for SRD_2014 and SRD_2024")
    void shouldFindLineagesByRuleset() {
        List<Lineage> srd2014Lineages = adapter.findLineagesByRuleset(Ruleset.SRD_2014);
        assertThat(srd2014Lineages).hasSize(9);
        assertThat(srd2014Lineages).allMatch(l -> l.getRuleset() == Ruleset.SRD_2014);

        List<Lineage> srd2024Lineages = adapter.findLineagesByRuleset(Ruleset.SRD_2024);
        assertThat(srd2024Lineages).hasSize(9);
        assertThat(srd2024Lineages).allMatch(l -> l.getRuleset() == Ruleset.SRD_2024);

        List<Lineage> all = adapter.findLineagesByRuleset(null);
        assertThat(all).hasSize(18);
    }

    @Test
    @DisplayName("should query backgrounds seeded by Flyway for SRD_2014 and SRD_2024")
    void shouldFindBackgroundsByRuleset() {
        List<Background> srd2014Backgrounds = adapter.findBackgroundsByRuleset(Ruleset.SRD_2014);
        assertThat(srd2014Backgrounds).hasSize(6);
        assertThat(srd2014Backgrounds).allMatch(b -> b.getRuleset() == Ruleset.SRD_2014);

        List<Background> srd2024Backgrounds = adapter.findBackgroundsByRuleset(Ruleset.SRD_2024);
        assertThat(srd2024Backgrounds).hasSize(6);
        assertThat(srd2024Backgrounds).allMatch(b -> b.getRuleset() == Ruleset.SRD_2024);

        List<Background> all = adapter.findBackgroundsByRuleset(null);
        assertThat(all).hasSize(12);
    }

    @Test
    @DisplayName("should query cursor paginated spells and search via FTS")
    void shouldFindSpellsWithCursorAndFts() {
        var spells = adapter.findSpellsWithCursor(null, null, null, null, null, null, 10);
        assertThat(spells).isNotEmpty();

        var searchSpells = adapter.findSpellsWithCursor(null, null, null, "Fogo", null, null, 10);
        assertThat(searchSpells).isNotEmpty();
        assertThat(searchSpells).allMatch(s -> s.getName().contains("Fogo") || s.getNameEn().contains("Fire"));

        var classes = adapter.findArchetypesWithCursor(null, null, null, null, 10);
        assertThat(classes).isNotEmpty();

        var lineages = adapter.findLineagesWithCursor(null, null, null, null, 10);
        assertThat(lineages).isNotEmpty();

        var feats = adapter.findFeatsWithCursor(null, null, null, null, 10);
        assertThat(feats).isNotEmpty();

        var items = adapter.findItemsWithCursor(null, null, null, null, null, null, 10);
        assertThat(items).isNotEmpty();
    }
}
