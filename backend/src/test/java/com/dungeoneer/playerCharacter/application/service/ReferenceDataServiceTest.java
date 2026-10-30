package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
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
}
