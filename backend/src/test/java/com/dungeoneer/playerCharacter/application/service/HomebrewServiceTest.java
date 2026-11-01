package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewArchetypeRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewItemRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewLineageRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSpellRequest;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomebrewServiceTest {

    @Mock
    private ReferenceDataRepository repository;

    private HomebrewService homebrewService;

    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        homebrewService = new HomebrewService(repository);
    }

    @Test
    @DisplayName("createSpell should map and save a valid HOMEBREW spell")
    void shouldCreateSpellSuccessfully() {
        CreateHomebrewSpellRequest request = new CreateHomebrewSpellRequest(
                "Raio Purpúreo",
                "Purple Ray",
                3,
                "Evocação",
                "1 ação",
                "18 metros",
                "V, S",
                "Instantânea",
                "Dispara um feixe de energia púrpura."
        );

        when(repository.saveSpell(any(Spell.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SpellDto result = homebrewService.createSpell(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Raio Purpúreo");
        assertThat(result.ruleset()).isEqualTo("HOMEBREW");
        assertThat(result.level()).isEqualTo(3);
        assertThat(result.school()).isEqualTo("Evocação");
        assertThat(result.source()).isEqualTo("HOMEBREW");

        ArgumentCaptor<Spell> captor = ArgumentCaptor.forClass(Spell.class);
        verify(repository).saveSpell(captor.capture());
        Spell saved = captor.getValue();
        assertThat(saved.getOwnerId()).isEqualTo(ownerId);
        assertThat(saved.getRuleset()).isEqualTo(Ruleset.HOMEBREW);
    }

    @Test
    @DisplayName("createSpell should reject null ownerId")
    void shouldThrowWhenOwnerIdIsNullForSpell() {
        CreateHomebrewSpellRequest request = new CreateHomebrewSpellRequest(
                "Raio", null, 1, "Evocação", null, null, null, null, "Desc"
        );
        assertThatThrownBy(() -> homebrewService.createSpell(null, request))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Owner id is required");
    }

    @Test
    @DisplayName("createFeat should map and save a valid HOMEBREW feat")
    void shouldCreateFeatSuccessfully() {
        CreateHomebrewFeatRequest request = new CreateHomebrewFeatRequest(
                "Mente Blindada",
                "Shielded Mind",
                "Sabedoria 13+",
                "Vantagem em testes de resistência contra efeitos mentais."
        );

        when(repository.saveFeat(any(Feat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeatDto result = homebrewService.createFeat(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Mente Blindada");
        assertThat(result.ruleset()).isEqualTo("HOMEBREW");

        ArgumentCaptor<Feat> captor = ArgumentCaptor.forClass(Feat.class);
        verify(repository).saveFeat(captor.capture());
        assertThat(captor.getValue().getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("createItem should map and save a valid HOMEBREW item")
    void shouldCreateItemSuccessfully() {
        CreateHomebrewItemRequest request = new CreateHomebrewItemRequest(
                "Adaga Esmeralda",
                "Emerald Dagger",
                "WEAPON",
                "Arma Simples",
                "RARE",
                true,
                "500 po",
                "0.5 kg",
                "Uma lâmina esculpida em esmeralda pura."
        );

        when(repository.saveItem(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = homebrewService.createItem(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Adaga Esmeralda");
        assertThat(result.itemCategory()).isEqualTo("WEAPON");
        assertThat(result.rarity()).isEqualTo("RARE");
        assertThat(result.attunement()).isTrue();

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(repository).saveItem(captor.capture());
        assertThat(captor.getValue().getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("createArchetype should map and save a valid HOMEBREW archetype")
    void shouldCreateArchetypeSuccessfully() {
        CreateHomebrewArchetypeRequest request = new CreateHomebrewArchetypeRequest(
                "Feiticeiro das Sombras",
                "D6",
                "CHARISMA",
                List.of("CONSTITUTION", "CHARISMA"),
                List.of("NONE"),
                List.of("DAGGERS"),
                "FULL",
                "CHARISMA"
        );

        when(repository.saveArchetype(any(Archetype.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArchetypeDto result = homebrewService.createArchetype(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Feiticeiro das Sombras");
        assertThat(result.ruleset()).isEqualTo(Ruleset.HOMEBREW);

        ArgumentCaptor<Archetype> captor = ArgumentCaptor.forClass(Archetype.class);
        verify(repository).saveArchetype(captor.capture());
        assertThat(captor.getValue().getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("createLineage should map and save a valid HOMEBREW lineage")
    void shouldCreateLineageSuccessfully() {
        CreateHomebrewLineageRequest request = new CreateHomebrewLineageRequest(
                "Draconato Glacial",
                "Descendentes de dragões brancos ancestrais.",
                30,
                "MEDIUM",
                java.util.Map.of("CONSTITUTION", 2, "STRENGTH", 1),
                List.of("Resistência a Frio", "Sopro Congelante")
        );

        when(repository.saveLineage(any(Lineage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LineageDto result = homebrewService.createLineage(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Draconato Glacial");
        assertThat(result.ruleset()).isEqualTo(Ruleset.HOMEBREW);

        ArgumentCaptor<Lineage> captor = ArgumentCaptor.forClass(Lineage.class);
        verify(repository).saveLineage(captor.capture());
        assertThat(captor.getValue().getOwnerId()).isEqualTo(ownerId);
    }
}
