package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.*;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort characterRepository;

    @Mock
    private ReferenceDataRepository referenceDataRepository;

    @Mock
    private CharacterSheetHydrator sheetHydrator;

    private InventoryService inventoryService;

    private UUID characterId;
    private UUID userId;
    private UUID itemId;
    private PlayerCharacter character;
    private Item potionItem;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(characterRepository, referenceDataRepository, sheetHydrator);

        characterId = UUID.randomUUID();
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        AbilityScores scores = new AbilityScores(10, 10, 10, 10, 10, 10);
        character = new PlayerCharacter(
                characterId,
                "Hero",
                1,
                5,
                10,
                0,
                10,
                0,
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                UUID.randomUUID(),
                UUID.randomUUID(),
                userId,
                scores,
                "",
                HpCalculationMethod.MAX,
                false,
                List.of(),
                List.of(),
                0,
                null,
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );

        potionItem = new Item(
                itemId,
                Ruleset.SRD_2024,
                "potion-of-healing",
                "Potion of Healing",
                "Poção de Cura",
                "Consumable",
                "Potion",
                "Common",
                false,
                "50 gp",
                "0.5 lb",
                "Heals 2d4+2 HP",
                "SRD",
                null,
                new ConsumableMechanics(ConsumableEffectType.HEAL_HP, "2d4+2", "Heals 2d4+2 HP")
        );
    }

    @Test
    @DisplayName("Should add new item to character inventory")
    void shouldAddItem() {
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(referenceDataRepository.findItemById(itemId)).thenReturn(Optional.of(potionItem));
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of());
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        CharacterSheetResult result = inventoryService.addItem(characterId, userId, new AddCharacterItemCommand(itemId, 2));

        assertThat(result).isNotNull();
        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getItemId().equals(itemId) && ci.getQuantity() == 2));
    }

    @Test
    @DisplayName("Should use consumable healing potion and recover HP")
    void shouldUseConsumablePotion() {
        UUID charItemId = UUID.randomUUID();
        CharacterItem charItem = new CharacterItem(charItemId, characterId, itemId, 1, false, false);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charItemId, characterId)).thenReturn(Optional.of(charItem));
        when(referenceDataRepository.findItemById(itemId)).thenReturn(Optional.of(potionItem));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        UseConsumableResult result = inventoryService.useConsumable(characterId, userId, charItemId);

        assertThat(result).isNotNull();
        assertThat(result.itemName()).isEqualTo("Poção de Cura");
        assertThat(result.rollTotal()).isBetween(4, 10); // 2d4+2 = 4 to 10
        assertThat(result.currentHp()).isGreaterThan(5); // Started at 5 HP
        assertThat(result.remainingQuantity()).isEqualTo(0);

        verify(characterRepository).deleteCharacterItem(charItemId, characterId);
        verify(characterRepository).save(any(PlayerCharacter.class));
    }

    @Test
    @DisplayName("Should purchase item with currency and deduct coins")
    void shouldPurchaseItemWithCurrency() {
        PlayerCharacter wealthyCharacter = character.withCurrencyPouch(new CurrencyPouch(0, 0, 0, 100, 0)); // 100 gp
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(wealthyCharacter));
        when(referenceDataRepository.findItemById(itemId)).thenReturn(Optional.of(potionItem)); // costs 50 gp
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of());
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        CharacterSheetResult result = inventoryService.addItem(
                characterId,
                userId,
                new AddCharacterItemCommand(itemId, 1, true)
        );

        assertThat(result).isNotNull();
        verify(characterRepository).save(argThat(c -> c.getCurrencyPouch().toTotalCopper() == 5000L)); // 100 gp - 50 gp = 50 gp = 5000 cp
        verify(characterRepository).saveCharacterItem(any(CharacterItem.class));
    }

    @Test
    @DisplayName("Should fail when purchasing item without sufficient funds")
    void shouldFailWhenPurchasingItemWithoutFunds() {
        PlayerCharacter poorCharacter = character.withCurrencyPouch(new CurrencyPouch(0, 0, 0, 1, 0)); // 1 gp
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(poorCharacter));
        when(referenceDataRepository.findItemById(itemId)).thenReturn(Optional.of(potionItem)); // costs 50 gp

        assertThatThrownBy(() -> inventoryService.addItem(
                characterId,
                userId,
                new AddCharacterItemCommand(itemId, 1, true)
        )).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Insufficient funds");
    }

    @Test
    @DisplayName("Should update currency pouch and consolidate when requested")
    void shouldUpdateCurrencyPouch() {
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        UpdateCurrencyRequest request = new UpdateCurrencyRequest(150, 20, 0, 5, 0, true);
        CharacterSheetResult result = inventoryService.updateCurrency(characterId, userId, request);

        assertThat(result).isNotNull();
        // 150 cp + 20 sp (200 cp) + 5 gp (500 cp) = 850 cp -> consolidated: 8 gp, 5 sp
        verify(characterRepository).save(argThat(c -> c.getCurrencyPouch().toTotalCopper() == 850L));
    }
}