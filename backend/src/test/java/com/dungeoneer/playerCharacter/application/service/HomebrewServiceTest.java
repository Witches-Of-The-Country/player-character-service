package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.adapter.out.persistence.ItemJpaEntity;
import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewArchetypeRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewItemRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewLineageRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSpellRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSubclassRequest;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.HomebrewDamageRollRequest;
import com.dungeoneer.playerCharacter.application.dto.HomebrewItemMechanicsRequest;
import com.dungeoneer.playerCharacter.application.dto.HomebrewProgressionFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.dto.SubclassDto;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.ItemMechanics;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Spell;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import com.dungeoneer.playerCharacter.domain.model.Subclass;
import com.dungeoneer.playerCharacter.domain.model.WeaponMechanics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("createArchetype should map and save a valid HOMEBREW archetype with hybrid progression feats")
    void shouldCreateArchetypeSuccessfully() {
        UUID existingFeatId = UUID.randomUUID();
        CreateHomebrewArchetypeRequest request = new CreateHomebrewArchetypeRequest(
                "Feiticeiro das Sombras",
                "D6",
                "CHARISMA",
                List.of("CONSTITUTION", "CHARISMA"),
                List.of("NONE"),
                List.of("DAGGERS"),
                "FULL",
                "CHARISMA",
                1,
                List.of(
                        new HomebrewProgressionFeatRequest(1, null, "Manto Umbral", "Coberto por sombras", MechanicalEffects.empty()),
                        new HomebrewProgressionFeatRequest(2, existingFeatId, null, null, null)
                )
        );

        when(repository.saveArchetype(any(Archetype.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.saveFeat(any(Feat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArchetypeDto result = homebrewService.createArchetype(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Feiticeiro das Sombras");
        assertThat(result.ruleset()).isEqualTo(Ruleset.HOMEBREW);
        assertThat(result.subclassLevel()).isEqualTo(1);

        verify(repository).saveArchetypeFeatLink(eq(result.id()), any(UUID.class), eq(1));
        verify(repository).saveArchetypeFeatLink(eq(result.id()), eq(existingFeatId), eq(2));
    }

    @Test
    @DisplayName("createSubclass should map and save a valid HOMEBREW subclass with progression feats")
    void shouldCreateSubclassSuccessfully() {
        UUID archetypeId = UUID.randomUUID();
        Archetype parent = new Archetype(
                archetypeId, "Fighter", HitDiceEnum.D10, "STR",
                List.of("STR"), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null, 3
        );
        when(repository.findArchetypeById(archetypeId)).thenReturn(Optional.of(parent));
        when(repository.saveSubclass(any(Subclass.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.saveFeat(any(Feat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateHomebrewSubclassRequest request = new CreateHomebrewSubclassRequest(
                archetypeId,
                "Cavaleiro Rúnico",
                "Rune Knight",
                "Guerreiro que grava runas gigantes.",
                List.of(new HomebrewProgressionFeatRequest(3, null, "Escultor de Runas", "Inscreve 2 runas.", MechanicalEffects.empty()))
        );

        SubclassDto result = homebrewService.createSubclass(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Cavaleiro Rúnico");
        verify(repository).saveSubclassFeatLink(eq(result.id()), any(UUID.class), eq(3));
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

    @Test
    @DisplayName("createItem with HomebrewItemMechanicsRequest should correctly map to WeaponMechanics and persist")
    void shouldCreateItemWithWeaponMechanics() {
        HomebrewItemMechanicsRequest mechanicsRequest = new HomebrewItemMechanicsRequest(
                "WEAPON",
                List.of(new HomebrewDamageRollRequest("1d8", "SLASHING")),
                "1d10",
                1,
                List.of("VERSATILE"),
                null, null, null, null, null, null, null
        );

        CreateHomebrewItemRequest request = new CreateHomebrewItemRequest(
                "Espada Flamejante",
                "Flame Blade",
                "WEAPON",
                "Espada Longa",
                "RARE",
                true,
                "1500 po",
                "1.5 kg",
                "Uma espada longa envolvida em chamas.",
                mechanicsRequest
        );

        when(repository.saveItem(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = homebrewService.createItem(ownerId, request);

        assertThat(result).isNotNull();
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(repository).saveItem(captor.capture());
        Item saved = captor.getValue();
        assertThat(saved.getMechanicalProperties()).isInstanceOf(WeaponMechanics.class);
        WeaponMechanics wm = (WeaponMechanics) saved.getMechanicalProperties();
        assertThat(wm.damageRolls()).hasSize(1);
        assertThat(wm.damageRolls().get(0).diceFormula()).isEqualTo("1d8");
        assertThat(wm.magicBonus()).isEqualTo(1);
        assertThat(wm.versatileDice()).isEqualTo("1d10");

        // Verify JSONB lossless round-trip
        String json = ItemJpaEntity.serializeMechanics(wm);
        assertThat(json).contains("\"type\":\"WEAPON\"");
        ItemMechanics deserialized = ItemJpaEntity.deserializeMechanics(json);
        assertThat(deserialized).isInstanceOf(WeaponMechanics.class);
    }

    @Test
    @DisplayName("ItemJpaEntity should deserialize legacy JSON without type discriminator")
    void shouldDeserializeLegacyItemWithoutType() {
        String legacyJson = "{\"damageRolls\":[{\"diceFormula\":\"1d6\",\"damageType\":\"PIERCING\"}],\"versatileDice\":null,\"magicBonus\":0,\"properties\":[\"FINESSE\"]}";
        ItemMechanics deserialized = ItemJpaEntity.deserializeMechanics(legacyJson);
        assertThat(deserialized).isInstanceOf(WeaponMechanics.class);
        WeaponMechanics wm = (WeaponMechanics) deserialized;
        assertThat(wm.damageRolls().get(0).diceFormula()).isEqualTo("1d6");
    }

    @Test
    @DisplayName("createSpell should persist structured mechanicalProperties")
    void shouldCreateSpellWithMechanicalProperties() {
        String spellMechanicsJson = "{\"damageRolls\":[{\"diceFormula\":\"8d6\",\"damageType\":\"FIRE\",\"conditional\":false}],\"components\":{\"v\":true,\"s\":true,\"m\":true,\"materialText\":\"enxofre\"}}";
        CreateHomebrewSpellRequest request = new CreateHomebrewSpellRequest(
                "Bola de Fogo",
                "Fireball",
                3,
                "Evocação",
                "1 ação",
                "45 metros",
                "V, S, M",
                "Instantânea",
                "Uma explosão brilhante.",
                spellMechanicsJson
        );

        when(repository.saveSpell(any(Spell.class))).thenAnswer(inv -> inv.getArgument(0));

        SpellDto result = homebrewService.createSpell(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.mechanicalProperties()).isEqualTo(spellMechanicsJson);
    }

    @Test
    @DisplayName("createArchetype should persist spellPreparationType")
    void shouldCreateArchetypeWithPreparationType() {
        CreateHomebrewArchetypeRequest request = new CreateHomebrewArchetypeRequest(
                "Clérigo Solar",
                "D8",
                "WISDOM",
                List.of("WISDOM", "CHARISMA"),
                List.of("MEDIUM"),
                List.of("SIMPLE"),
                "FULL",
                "WISDOM",
                3,
                List.of(),
                "PREPARED"
        );

        when(repository.saveArchetype(any(Archetype.class))).thenAnswer(inv -> inv.getArgument(0));

        ArchetypeDto result = homebrewService.createArchetype(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.spellPreparationType()).isEqualTo("PREPARED");
    }

    @Test
    @DisplayName("createLineage should create inline feats and link them")
    void shouldCreateLineageWithInlineFeats() {
        CreateHomebrewLineageRequest request = new CreateHomebrewLineageRequest(
                "Elfo Astral",
                "Viajantes do espaço selvagem.",
                30,
                "MEDIUM",
                Map.of("DEXTERITY", 2),
                List.of("Visão no Escuro"),
                List.of(new HomebrewProgressionFeatRequest(1, null, "Passo Astral", "Teletransporte bônus", MechanicalEffects.empty()))
        );

        when(repository.saveLineage(any(Lineage.class))).thenAnswer(inv -> inv.getArgument(0));
        when(repository.saveFeat(any(Feat.class))).thenAnswer(inv -> inv.getArgument(0));

        LineageDto result = homebrewService.createLineage(ownerId, request);

        assertThat(result).isNotNull();
        verify(repository).saveFeat(any(Feat.class));
        verify(repository).saveLineageFeatLink(eq(result.id()), any(UUID.class), eq(1));
    }

    @Test
    @DisplayName("Spec 006: updateSpell should successfully update HOMEBREW spell")
    void shouldUpdateHomebrewSpellSuccessfully() {
        UUID spellId = UUID.randomUUID();
        Spell existing = new Spell(
                spellId, Ruleset.HOMEBREW, "raio-antigo", "Old Ray", "Raio Antigo",
                1, "Evocação", "1 ação", "18m", "V, S", "Instantânea", "Desc", "HOMEBREW", ownerId, null
        );
        when(repository.findSpellById(spellId)).thenReturn(Optional.of(existing));
        when(repository.saveSpell(any(Spell.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateHomebrewSpellRequest updateRequest = new CreateHomebrewSpellRequest(
                "Raio Novo", "New Ray", 2, "Evocação", "1 ação bônus", "30m", "V, S", "Concentração", "Nova Desc"
        );

        SpellDto updated = homebrewService.updateSpell(spellId, ownerId, updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Raio Novo");
        assertThat(updated.level()).isEqualTo(2);
        verify(repository).saveSpell(any(Spell.class));
    }

    @Test
    @DisplayName("Spec 006: updateSpell should throw CharacterValidationException when attempting to update official SRD spell")
    void shouldRejectUpdatingOfficialSrdSpell() {
        UUID spellId = UUID.randomUUID();
        Spell srdSpell = new Spell(
                spellId, Ruleset.SRD_2024, "fireball", "Fireball", "Bola de Fogo",
                3, "Evocação", "1 ação", "45m", "V, S, M", "Instantânea", "Explosão de fogo", "SRD", null, null
        );
        when(repository.findSpellById(spellId)).thenReturn(Optional.of(srdSpell));

        CreateHomebrewSpellRequest updateRequest = new CreateHomebrewSpellRequest(
                "Bola de Fogo Modificada", "Fireball Modified", 3, "Evocação", "1 ação", "45m", "V", "Instantânea", "Desc"
        );

        assertThatThrownBy(() -> homebrewService.updateSpell(spellId, ownerId, updateRequest))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Official SRD spells cannot be modified");
    }

    @Test
    @DisplayName("Spec 006: updateItem should successfully update HOMEBREW item")
    void shouldUpdateHomebrewItemSuccessfully() {
        UUID itemId = UUID.randomUUID();
        Item existing = new Item(
                itemId, Ruleset.HOMEBREW, "adaga-sombria", "Shadow Dagger", "Adaga Sombria",
                "WEAPON", "Adaga", "UNCOMMON", false, "200 po", "0.5 kg", "Desc", "HOMEBREW", ownerId, null
        );
        when(repository.findItemById(itemId)).thenReturn(Optional.of(existing));
        when(repository.saveItem(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateHomebrewItemRequest updateRequest = new CreateHomebrewItemRequest(
                "Adaga das Sombras", "Shadow Dagger", "WEAPON", "Adaga", "RARE", true, "500 po", "0.5 kg", "Nova Desc", null
        );

        ItemDto updated = homebrewService.updateItem(itemId, ownerId, updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Adaga das Sombras");
        assertThat(updated.rarity()).isEqualTo("RARE");
        verify(repository).saveItem(any(Item.class));
    }

    @Test
    @DisplayName("Spec 006: updateItem should reject official SRD item modification")
    void shouldRejectUpdatingOfficialSrdItem() {
        UUID itemId = UUID.randomUUID();
        Item srdItem = new Item(
                itemId, Ruleset.SRD_2024, "longsword", "Longsword", "Espada Longa",
                "WEAPON", "Espada", "COMMON", false, "15 po", "1.5 kg", "Desc", "SRD", null, null
        );
        when(repository.findItemById(itemId)).thenReturn(Optional.of(srdItem));

        CreateHomebrewItemRequest updateRequest = new CreateHomebrewItemRequest(
                "Espada Super Longa", "Super Longsword", "WEAPON", "Espada", "COMMON", false, "15 po", "1.5 kg", "Desc", null
        );

        assertThatThrownBy(() -> homebrewService.updateItem(itemId, ownerId, updateRequest))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Official SRD items cannot be modified");
    }

    @Test
    @DisplayName("TP-03: createFeat with custom charge configuration successfully creates and persists homebrew feat")
    void shouldCreateFeatWithChargeConfiguration() {
        when(repository.saveFeat(any(Feat.class))).thenAnswer(inv -> inv.getArgument(0));

        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, null,
                null, false, null, List.of(), null,
                com.dungeoneer.playerCharacter.domain.model.ChargeType.ABILITY_MODIFIER, "CHA", null,
                com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.FIXED, 1,
                com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.ALL, null
        );

        CreateHomebrewFeatRequest request = new CreateHomebrewFeatRequest(
                "Inspiração Rúnica",
                "Runic Inspiration",
                null,
                "Concede dados de inspiração.",
                com.dungeoneer.playerCharacter.domain.model.FeatType.GENERAL,
                effects
        );

        FeatDto result = homebrewService.createFeat(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Inspiração Rúnica");
        assertThat(result.mechanicalEffects()).isNotNull();
        assertThat(result.mechanicalEffects().maxChargesType()).isEqualTo(com.dungeoneer.playerCharacter.domain.model.ChargeType.ABILITY_MODIFIER);
        assertThat(result.mechanicalEffects().chargeAbility()).isEqualTo("CHA");
        assertThat(result.mechanicalEffects().shortRestRecoveryType()).isEqualTo(com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.FIXED);
        assertThat(result.mechanicalEffects().shortRestRecoveryFixed()).isEqualTo(1);
        assertThat(result.mechanicalEffects().longRestRecoveryType()).isEqualTo(com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.ALL);
    }

    @Test
    @DisplayName("TE-03: createFeat should reject ABILITY_MODIFIER charge configuration when chargeAbility is missing")
    void shouldRejectFeatWhenAbilityModifierHasNoAbility() {
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, null,
                null, false, null, List.of(), null,
                com.dungeoneer.playerCharacter.domain.model.ChargeType.ABILITY_MODIFIER, null, null,
                null, null, null, null
        );

        CreateHomebrewFeatRequest request = new CreateHomebrewFeatRequest(
                "Inspiração Inválida", null, null, "Desc", null, effects
        );

        assertThatThrownBy(() -> homebrewService.createFeat(ownerId, request))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("O atributo da fórmula de cargas é obrigatório");
    }

    @Test
    @DisplayName("TE-03: createFeat should reject FIXED charge configuration when fixedMaxCharges is not positive")
    void shouldRejectFeatWhenFixedMaxChargesIsNotPositive() {
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, null,
                null, false, null, List.of(), null,
                com.dungeoneer.playerCharacter.domain.model.ChargeType.FIXED, null, 0,
                null, null, null, null
        );

        CreateHomebrewFeatRequest request = new CreateHomebrewFeatRequest(
                "Carga Fixa Zero", null, null, "Desc", null, effects
        );

        assertThatThrownBy(() -> homebrewService.createFeat(ownerId, request))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("O número de cargas fixas deve ser maior que zero");
    }

    @Test
    @DisplayName("TE-03: createFeat should reject FIXED recovery configuration when recovery amount is not positive")
    void shouldRejectFeatWhenFixedRecoveryIsNotPositive() {
        MechanicalEffects effects = new MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, null,
                null, false, null, List.of(), null,
                com.dungeoneer.playerCharacter.domain.model.ChargeType.FIXED, null, 3,
                com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.FIXED, 0,
                null, null
        );

        CreateHomebrewFeatRequest request = new CreateHomebrewFeatRequest(
                "Recuperação Fixa Zero", null, null, "Desc", null, effects
        );

        assertThatThrownBy(() -> homebrewService.createFeat(ownerId, request))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("recuperadas no descanso curto deve ser maior que zero");
    }
}
