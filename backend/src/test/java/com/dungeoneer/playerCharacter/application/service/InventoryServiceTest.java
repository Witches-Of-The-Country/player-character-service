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
    @DisplayName("Should preserve untouched denominations including electrum when purchasing item")
    void shouldPreserveUntouchedDenominationsWhenPurchasingItem() {
        // Character has 100 CP, 20 SP, 10 EP, 100 GP, 2 PP
        PlayerCharacter mixedPouchCharacter = character.withCurrencyPouch(new CurrencyPouch(100, 20, 10, 100, 2));
        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(mixedPouchCharacter));
        when(referenceDataRepository.findItemById(itemId)).thenReturn(Optional.of(potionItem)); // costs 50 gp
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of());
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        CharacterSheetResult result = inventoryService.addItem(
                characterId,
                userId,
                new AddCharacterItemCommand(itemId, 1, true)
        );

        assertThat(result).isNotNull();
        verify(characterRepository).save(argThat(c -> {
            CurrencyPouch p = c.getCurrencyPouch();
            return p.cp() == 100
                    && p.sp() == 20
                    && p.ep() == 10
                    && p.gp() == 50
                    && p.pp() == 2;
        }));
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

    @Test
    @DisplayName("Should auto-unequip previous armor in CHEST when equipping a second armor")
    void shouldAutoUnequipPreviousArmorInChestWhenEquippingSecondArmor() {
        UUID armor1Id = UUID.randomUUID();
        UUID armor2Id = UUID.randomUUID();
        Item armor1 = new Item(armor1Id, Ruleset.SRD_2024, "leather-armor", "Leather Armor", "Armadura de Couro", "ARMOR", "Armor", "Common", false, "10 gp", "10 lb", "Light Armor", "SRD", null, new ArmorMechanics(ArmorCategory.LIGHT, 11, 0, 0, false));
        Item armor2 = new Item(armor2Id, Ruleset.SRD_2024, "chain-mail", "Chain Mail", "Cota de Malha", "ARMOR", "Armor", "Common", false, "75 gp", "55 lb", "Heavy Armor", "SRD", null, new ArmorMechanics(ArmorCategory.HEAVY, 16, 0, 13, true));

        UUID charItem1Id = UUID.randomUUID();
        UUID charItem2Id = UUID.randomUUID();
        CharacterItem charItem1 = new CharacterItem(charItem1Id, characterId, armor1Id, 1, true, false, EquipmentSlot.CHEST);
        CharacterItem charItem2 = new CharacterItem(charItem2Id, characterId, armor2Id, 1, false, false, null);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charItem2Id, characterId)).thenReturn(Optional.of(charItem2));
        when(referenceDataRepository.findItemById(armor2Id)).thenReturn(Optional.of(armor2));

        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of(charItem1, charItem2));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        inventoryService.updateItem(characterId, userId, charItem2Id, new UpdateCharacterItemCommand(true, null, false, 1));

        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charItem1Id) && !ci.isEquipped() && ci.getEquippedSlot() == null));
        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charItem2Id) && ci.isEquipped() && ci.getEquippedSlot() == EquipmentSlot.CHEST));
    }

    @Test
    @DisplayName("Should auto-unequip shield in LEFT_HAND when equipping a TWO_HANDED weapon")
    void shouldAutoUnequipShieldWhenEquippingTwoHandedWeapon() {
        UUID shieldItemId = UUID.randomUUID();
        UUID greatswordItemId = UUID.randomUUID();
        Item shield = new Item(shieldItemId, Ruleset.SRD_2024, "shield", "Shield", "Escudo", "ARMOR", "Shield", "Common", false, "10 gp", "6 lb", "Shield", "SRD", null, new ArmorMechanics(ArmorCategory.SHIELD, 2, 0, 0, false));
        Item greatsword = new Item(greatswordItemId, Ruleset.SRD_2024, "greatsword", "Greatsword", "Espada Grande", "WEAPON", "Weapon", "Common", false, "50 gp", "6 lb", "Two-handed weapon", "SRD", null, new WeaponMechanics(List.of(new DamageRoll("2d6", DamageType.SLASHING)), null, 0, List.of(WeaponProperty.TWO_HANDED, WeaponProperty.HEAVY)));

        UUID charShieldId = UUID.randomUUID();
        UUID charWepId = UUID.randomUUID();
        CharacterItem charShield = new CharacterItem(charShieldId, characterId, shieldItemId, 1, true, false, EquipmentSlot.LEFT_HAND);
        CharacterItem charWep = new CharacterItem(charWepId, characterId, greatswordItemId, 1, false, false, null);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charWepId, characterId)).thenReturn(Optional.of(charWep));
        when(referenceDataRepository.findItemById(greatswordItemId)).thenReturn(Optional.of(greatsword));
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of(charShield, charWep));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        inventoryService.updateItem(characterId, userId, charWepId, new UpdateCharacterItemCommand(true, null, false, 1));

        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charShieldId) && !ci.isEquipped()));
        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charWepId) && ci.isEquipped() && ci.getEquippedSlot() == EquipmentSlot.RIGHT_HAND));
    }

    @Test
    @DisplayName("Should auto-unequip TWO_HANDED weapon when equipping a shield in LEFT_HAND")
    void shouldAutoUnequipTwoHandedWeaponWhenEquippingShieldInLeftHand() {
        UUID shieldItemId = UUID.randomUUID();
        UUID greatswordItemId = UUID.randomUUID();
        Item shield = new Item(shieldItemId, Ruleset.SRD_2024, "shield", "Shield", "Escudo", "ARMOR", "Shield", "Common", false, "10 gp", "6 lb", "Shield", "SRD", null, new ArmorMechanics(ArmorCategory.SHIELD, 2, 0, 0, false));
        Item greatsword = new Item(greatswordItemId, Ruleset.SRD_2024, "greatsword", "Greatsword", "Espada Grande", "WEAPON", "Weapon", "Common", false, "50 gp", "6 lb", "Two-handed weapon", "SRD", null, new WeaponMechanics(List.of(new DamageRoll("2d6", DamageType.SLASHING)), null, 0, List.of(WeaponProperty.TWO_HANDED, WeaponProperty.HEAVY)));

        UUID charShieldId = UUID.randomUUID();
        UUID charWepId = UUID.randomUUID();
        CharacterItem charShield = new CharacterItem(charShieldId, characterId, shieldItemId, 1, false, false, null);
        CharacterItem charWep = new CharacterItem(charWepId, characterId, greatswordItemId, 1, true, false, EquipmentSlot.RIGHT_HAND);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charShieldId, characterId)).thenReturn(Optional.of(charShield));
        when(referenceDataRepository.findItemById(shieldItemId)).thenReturn(Optional.of(shield));
        when(referenceDataRepository.findItemById(greatswordItemId)).thenReturn(Optional.of(greatsword));
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of(charShield, charWep));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        inventoryService.updateItem(characterId, userId, charShieldId, new UpdateCharacterItemCommand(true, EquipmentSlot.LEFT_HAND, false, 1));

        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charWepId) && !ci.isEquipped()));
        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charShieldId) && ci.isEquipped() && ci.getEquippedSlot() == EquipmentSlot.LEFT_HAND));
    }

    @Test
    @DisplayName("Should reject equipping armor in non-CHEST slot")
    void shouldRejectArmorInNonChestSlot() {
        UUID armorId = UUID.randomUUID();
        Item armor = new Item(armorId, Ruleset.SRD_2024, "chain-mail", "Chain Mail", "Cota de Malha", "ARMOR", "Armor", "Common", false, "75 gp", "55 lb", "Heavy Armor", "SRD", null, new ArmorMechanics(ArmorCategory.HEAVY, 16, 0, 13, true));
        UUID charItemId = UUID.randomUUID();
        CharacterItem charItem = new CharacterItem(charItemId, characterId, armorId, 1, false, false, null);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charItemId, characterId)).thenReturn(Optional.of(charItem));
        when(referenceDataRepository.findItemById(armorId)).thenReturn(Optional.of(armor));

        assertThatThrownBy(() -> inventoryService.updateItem(
                characterId,
                userId,
                charItemId,
                new UpdateCharacterItemCommand(true, EquipmentSlot.GLOVES, false, 1)
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Armor can only be equipped in CHEST slot");
    }

    @Test
    @DisplayName("Should infer and equip belt in TRINKET slot")
    void shouldInferAndEquipBeltInTrinketSlot() {
        UUID beltId = UUID.randomUUID();
        Item belt = new Item(beltId, Ruleset.SRD_2024, "belt-of-giant-strength", "Belt of Giant Strength", "Cinto de Força do Gigante", "WONDROUS", "Wondrous Item", "Rare", true, "5000 gp", "1 lb", "Belt", "SRD", null, null);
        UUID charItemId = UUID.randomUUID();
        CharacterItem charItem = new CharacterItem(charItemId, characterId, beltId, 1, false, false, null);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charItemId, characterId)).thenReturn(Optional.of(charItem));
        when(referenceDataRepository.findItemById(beltId)).thenReturn(Optional.of(belt));
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of(charItem));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        inventoryService.updateItem(characterId, userId, charItemId, new UpdateCharacterItemCommand(true, null, false, 1));

        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charItemId) && ci.isEquipped() && ci.getEquippedSlot() == EquipmentSlot.TRINKET));
    }

    @Test
    @DisplayName("Should auto-unequip previous occupant of TRINKET slot when equipping another trinket")
    void shouldAutoUnequipPreviousTrinketWhenEquippingSecondTrinket() {
        UUID belt1Id = UUID.randomUUID();
        UUID charmId = UUID.randomUUID();
        Item belt = new Item(belt1Id, Ruleset.SRD_2024, "belt-of-giant-strength", "Belt of Giant Strength", "Cinto de Força do Gigante", "WONDROUS", "Wondrous Item", "Rare", true, "5000 gp", "1 lb", "Belt", "SRD", null, null);
        Item charm = new Item(charmId, Ruleset.SRD_2024, "luck-charm", "Luck Charm", "Amuleto da Sorte", "WONDROUS", "Wondrous Item", "Uncommon", false, "500 gp", "0.5 lb", "Charm", "SRD", null, null);

        UUID charBeltId = UUID.randomUUID();
        UUID charCharmId = UUID.randomUUID();
        CharacterItem charBelt = new CharacterItem(charBeltId, characterId, belt1Id, 1, true, false, EquipmentSlot.TRINKET);
        CharacterItem charCharm = new CharacterItem(charCharmId, characterId, charmId, 1, false, false, null);

        when(characterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(characterRepository.findCharacterItemByIdAndCharacterId(charCharmId, characterId)).thenReturn(Optional.of(charCharm));
        when(referenceDataRepository.findItemById(charmId)).thenReturn(Optional.of(charm));
        when(characterRepository.findItemsByCharacterId(characterId)).thenReturn(List.of(charBelt, charCharm));
        when(sheetHydrator.hydrate(any(PlayerCharacter.class))).thenReturn(mock(CharacterSheetResult.class));

        inventoryService.updateItem(characterId, userId, charCharmId, new UpdateCharacterItemCommand(true, EquipmentSlot.TRINKET, false, 1));

        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charBeltId) && !ci.isEquipped()));
        verify(characterRepository).saveCharacterItem(argThat(ci -> ci.getId().equals(charCharmId) && ci.isEquipped() && ci.getEquippedSlot() == EquipmentSlot.TRINKET));
    }
}
