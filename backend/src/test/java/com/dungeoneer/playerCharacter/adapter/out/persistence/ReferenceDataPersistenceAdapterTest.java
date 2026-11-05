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

        var classes = adapter.findArchetypesWithCursor(null, null, null, null, 10);
        assertThat(classes).isNotEmpty();

        var lineages = adapter.findLineagesWithCursor(null, null, null, null, 10);
        assertThat(lineages).isNotEmpty();

        var feats = adapter.findFeatsWithCursor(null, null, null, null, 10);
        assertThat(feats).isNotEmpty();

        var items = adapter.findItemsWithCursor(null, null, null, null, null, null, 10);
        assertThat(items).isNotEmpty();
    }

    @Test
    @DisplayName("should query items filtered by canonical categories and rarities")
    void shouldFindItemsByCategoryAndRarity() {
        var potions = adapter.findItemsWithCursor(null, List.of("POTION"), null, null, null, null, 10, null);
        assertThat(potions).isNotEmpty();
        assertThat(potions).allMatch(i -> "POTION".equalsIgnoreCase(i.getItemCategory()) || i.getItemType().toLowerCase().contains("potion") || i.getItemType().toLowerCase().contains("poção"));

        var wondrous = adapter.findItemsWithCursor(null, List.of("WONDROUS"), null, null, null, null, 10, null);
        assertThat(wondrous).isNotEmpty();
        assertThat(wondrous).allMatch(i -> "WONDROUS".equalsIgnoreCase(i.getItemCategory()) || i.getItemType().toLowerCase().contains("wondrous") || i.getItemType().toLowerCase().contains("maravilhoso"));

        var commonItems = adapter.findItemsWithCursor(null, null, List.of("COMMON"), null, null, null, 20, null);
        assertThat(commonItems).isNotEmpty();
        assertThat(commonItems).allMatch(i -> i.getRarity() == null || List.of("NONE", "COMMON", "COMUM").contains(i.getRarity().toUpperCase()));
    }

    @Test
    @DisplayName("Spec 005 - TR-01 & TR-02: should match partial prefix, unaccented, English name and whitespace boundary across compendiums")
    void shouldFindItemsAndReferenceDataWithPrefixAndUnaccentedQueries() {
        // TR-01: Partial prefix "esp" matching "Espada"
        var espItems = adapter.findItemsWithCursor(null, null, null, "esp", null, null, 10, null);
        assertThat(espItems).isNotEmpty();
        assertThat(espItems).anyMatch(i -> i.getName().toLowerCase().contains("espada"));

        // TR-01: Partial prefix "arm" matching "Armadura"
        var armItems = adapter.findItemsWithCursor(null, null, null, "arm", null, null, 10, null);
        assertThat(armItems).isNotEmpty();
        assertThat(armItems).anyMatch(i -> i.getName().toLowerCase().contains("armadura"));

        // TR-02: Unaccented query "pocao" matching accented "Poção"
        var pocaoItems = adapter.findItemsWithCursor(null, null, null, "pocao", null, null, 10, null);
        assertThat(pocaoItems).isNotEmpty();
        assertThat(pocaoItems).anyMatch(i -> i.getName().toLowerCase().contains("poção"));

        // Criterion 3: English name "longsword" matching "Espada Longa" (name_en = "Longsword")
        var englishItems = adapter.findItemsWithCursor(null, null, null, "longsword", null, null, 10, null);
        assertThat(englishItems).isNotEmpty();
        assertThat(englishItems).anyMatch(i -> "longsword".equalsIgnoreCase(i.getNameEn()) || i.getName().toLowerCase().contains("espada longa"));

        // TE-01: Empty string or whitespace queries should return results without errors
        var emptyItems = adapter.findItemsWithCursor(null, null, null, "", null, null, 10, null);
        assertThat(emptyItems).isNotEmpty();

        var whitespaceItems = adapter.findItemsWithCursor(null, null, null, "   ", null, null, 10, null);
        assertThat(whitespaceItems).isNotEmpty();

        // Spells: unaccented "bencao" matching "Bênção"
        var bencaoSpells = adapter.findSpellsWithCursor(null, null, null, "bencao", null, null, 10, null);
        assertThat(bencaoSpells).isNotEmpty();
        assertThat(bencaoSpells).anyMatch(s -> s.getName().toLowerCase().contains("bênção"));

        // Spells: partial prefix "cur" matching "Curar"
        var curSpells = adapter.findSpellsWithCursor(null, null, null, "cur", null, null, 10, null);
        assertThat(curSpells).isNotEmpty();
        assertThat(curSpells).anyMatch(s -> s.getName().toLowerCase().contains("curar"));

        // Archetypes: unaccented "barb" matching "Bárbaro"
        var barbClasses = adapter.findArchetypesWithCursor(null, "barb", null, null, 10, null);
        assertThat(barbClasses).isNotEmpty();
        assertThat(barbClasses).anyMatch(a -> a.getName().toLowerCase().contains("bárbaro") || a.getName().toLowerCase().contains("barbarian"));

        // Lineages: unaccented "ana" matching "Anão"
        var anaoLineages = adapter.findLineagesWithCursor(null, "ana", null, null, 10, null);
        assertThat(anaoLineages).isNotEmpty();
        assertThat(anaoLineages).anyMatch(l -> l.getName().toLowerCase().contains("anão") || l.getName().toLowerCase().contains("dwarf"));
    }
}
