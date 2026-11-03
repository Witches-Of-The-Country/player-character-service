package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceTest {

    @Mock
    private ReferenceDataRepository repository;

    private ReferenceDataService service;

    @BeforeEach
    void setUp() {
        service = new ReferenceDataService(repository);
    }

    @Test
    @DisplayName("should return mapped ArchetypeDto list when getClasses is invoked")
    void shouldReturnClasses() {
        Archetype archetype = new Archetype(
                UUID.randomUUID(),
                "Paladin",
                HitDiceEnum.D10,
                "STRENGTH",
                List.of("WISDOM", "CHARISMA"),
                List.of("ALL_ARMOR"),
                List.of("MARTIAL"),
                SpellcastingType.HALF,
                "CHARISMA",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        when(repository.findArchetypesByRuleset(Ruleset.SRD_2024))
                .thenReturn(List.of(archetype));

        List<ArchetypeDto> result = service.getClasses(Ruleset.SRD_2024);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Paladin");
        assertThat(result.get(0).hitDice()).isEqualTo("d10");
        assertThat(result.get(0).spellcastingType()).isEqualTo(SpellcastingType.HALF);
        verify(repository).findArchetypesByRuleset(Ruleset.SRD_2024);
    }

    @Test
    @DisplayName("should return mapped LineageDto list when getLineages is invoked")
    void shouldReturnLineages() {
        Lineage lineage = new Lineage(
                UUID.randomUUID(),
                "Human",
                "Versatile",
                30,
                "MEDIUM",
                Map.of(),
                List.of("Resourceful"),
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        when(repository.findLineagesByRuleset(Ruleset.SRD_2024))
                .thenReturn(List.of(lineage));

        List<LineageDto> result = service.getLineages(Ruleset.SRD_2024);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Human");
        assertThat(result.get(0).speed()).isEqualTo(30);
        verify(repository).findLineagesByRuleset(Ruleset.SRD_2024);
    }

    @Test
    @DisplayName("should return mapped BackgroundDto list when getBackgrounds is invoked")
    void shouldReturnBackgrounds() {
        Background background = new Background(
                UUID.randomUUID(),
                "Sage",
                "Scholar",
                List.of("ARCANA", "HISTORY"),
                List.of(),
                List.of("TWO_OF_CHOICE"),
                List.of("CONSTITUTION", "INTELLIGENCE", "WISDOM"),
                "Magic Initiate (Wizard)",
                "Cantrips",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        when(repository.findBackgroundsByRuleset(Ruleset.SRD_2024))
                .thenReturn(List.of(background));

        List<BackgroundDto> result = service.getBackgrounds(Ruleset.SRD_2024);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Sage");
        assertThat(result.get(0).originFeatName()).isEqualTo("Magic Initiate (Wizard)");
        verify(repository).findBackgroundsByRuleset(Ruleset.SRD_2024);
    }

    @Test
    @DisplayName("should filter feats by featTypes list when provided")
    void shouldFilterFeatsByFeatTypes() {
        com.dungeoneer.playerCharacter.domain.model.Feat domainFeat = new com.dungeoneer.playerCharacter.domain.model.Feat(
                UUID.randomUUID(),
                Ruleset.SRD_2024,
                "alerta",
                "Alert",
                "Alerta",
                null,
                "Ganha iniciativa",
                "SRD",
                null,
                com.dungeoneer.playerCharacter.domain.model.FeatType.ORIGIN,
                com.dungeoneer.playerCharacter.domain.model.MechanicalEffects.empty()
        );
        com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity featEntity =
                com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity.fromDomain(domainFeat);

        when(repository.findFeatsWithCursor(
                org.mockito.ArgumentMatchers.eq(Ruleset.SRD_2024),
                org.mockito.ArgumentMatchers.eq(List.of("ORIGIN", "GENERAL")),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq(21),
                org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(List.of(featEntity));

        var response = service.getFeats(null, 20, null, Ruleset.SRD_2024, List.of("origin", "GENERAL "), null);

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).name()).isEqualTo("Alerta");
        assertThat(response.items().get(0).featType()).isEqualTo(com.dungeoneer.playerCharacter.domain.model.FeatType.ORIGIN);
    }

    @Test
    @DisplayName("should filter items by categories when provided")
    void shouldFilterItemsByCategories() {
        Item item = new Item(
                UUID.randomUUID(),
                Ruleset.SRD_2024,
                "potion-of-healing",
                "Potion of Healing",
                "Poção de Cura",
                "POTION",
                "Potion",
                "COMMON",
                false,
                "50 gp",
                "0.5 lb",
                "Recupera 2d4+2 PV",
                "SRD",
                null,
                new com.dungeoneer.playerCharacter.domain.model.EmptyItemMechanics()
        );

        when(repository.findItemsWithCursor(
                Ruleset.SRD_2024,
                List.of("POTION", "WONDROUS"),
                null,
                null,
                null,
                null,
                11,
                null
        )).thenReturn(List.of(item));

        var response = service.getItems(null, 10, null, Ruleset.SRD_2024, List.of("POTION", "WONDROUS"), null, null);

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).name()).isEqualTo("Poção de Cura");
        assertThat(response.items().get(0).itemCategory()).isEqualTo("POTION");
        verify(repository).findItemsWithCursor(Ruleset.SRD_2024, List.of("POTION", "WONDROUS"), null, null, null, null, 11, null);
    }

    @Test
    @DisplayName("should filter items by rarities when provided")
    void shouldFilterItemsByRarities() {
        Item item = new Item(
                UUID.randomUUID(),
                Ruleset.SRD_2024,
                "longsword",
                "Longsword",
                "Espada Longa",
                "EQUIPMENT",
                "Weapon",
                "COMMON",
                false,
                "15 gp",
                "3 lb",
                "Arma marcial versátil",
                "SRD",
                null,
                new com.dungeoneer.playerCharacter.domain.model.EmptyItemMechanics()
        );

        when(repository.findItemsWithCursor(
                Ruleset.SRD_2024,
                null,
                List.of("COMMON", "RARE"),
                null,
                null,
                null,
                11,
                null
        )).thenReturn(List.of(item));

        var response = service.getItems(null, 10, null, Ruleset.SRD_2024, null, List.of("COMMON", "RARE"), null);

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).name()).isEqualTo("Espada Longa");
        assertThat(response.items().get(0).rarity()).isEqualTo("COMMON");
        verify(repository).findItemsWithCursor(Ruleset.SRD_2024, null, List.of("COMMON", "RARE"), null, null, null, 11, null);
    }
}
