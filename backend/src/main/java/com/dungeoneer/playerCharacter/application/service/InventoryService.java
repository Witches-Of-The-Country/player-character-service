package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.*;
import com.dungeoneer.playerCharacter.application.port.in.ManageInventoryUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UseConsumableUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional
public class InventoryService implements ManageInventoryUseCase, UseConsumableUseCase {

    private static final int MAX_ATTUNEMENT_SLOTS = 3;
    private static final Random RANDOM = new SecureRandom();

    private final PlayerCharacterRepositoryPort characterRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final CharacterSheetHydrator sheetHydrator;

    public InventoryService(
            PlayerCharacterRepositoryPort characterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator sheetHydrator
    ) {
        this.characterRepository = characterRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.sheetHydrator = sheetHydrator;
    }

    @Override
    public CharacterSheetResult addItem(UUID characterId, UUID userId, AddCharacterItemCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);
        Item item = referenceDataRepository.findItemById(command.itemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + command.itemId()));

        if (Boolean.TRUE.equals(command.purchaseWithCurrency())) {
            CurrencyPouch costPouch = CurrencyPouch.parseCostString(item.getCost());
            long totalCostCopper = costPouch.toTotalCopper() * Math.max(1, command.quantity());
            if (totalCostCopper > 0) {
                CurrencyPouch currentPouch = character.getCurrencyPouch();
                if (!currentPouch.canAfford(totalCostCopper)) {
                    throw new IllegalStateException("Insufficient funds to purchase " + item.getName() + ": required " + totalCostCopper + " cp, but character only has " + currentPouch.toTotalCopper() + " cp");
                }
                CurrencyPouch updatedPouch = currentPouch.deductWithConversion(totalCostCopper);
                character = character.withCurrencyPouch(updatedPouch);
                characterRepository.save(character);
            }
        }

        // Check if item already exists in character's inventory
        List<CharacterItem> existingItems = characterRepository.findItemsByCharacterId(characterId);
        Optional<CharacterItem> existing = existingItems.stream()
                .filter(ci -> ci.getItemId().equals(command.itemId()))
                .findFirst();

        if (existing.isPresent()) {
            CharacterItem current = existing.get();
            int newQuantity = current.getQuantity() + Math.max(1, command.quantity());
            characterRepository.saveCharacterItem(current.withQuantity(newQuantity));
        } else {
            CharacterItem newItem = new CharacterItem(
                    UUID.randomUUID(),
                    characterId,
                    command.itemId(),
                    Math.max(1, command.quantity()),
                    false,
                    false
            );
            characterRepository.saveCharacterItem(newItem);
        }

        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    @Override
    public CharacterSheetResult updateCurrency(UUID characterId, UUID userId, UpdateCurrencyRequest request) {
        PlayerCharacter character = getCharacter(characterId, userId);
        CurrencyPouch current = character.getCurrencyPouch();
        int cp = request.cp() != null ? request.cp() : current.cp();
        int sp = request.sp() != null ? request.sp() : current.sp();
        int ep = request.ep() != null ? request.ep() : current.ep();
        int gp = request.gp() != null ? request.gp() : current.gp();
        int pp = request.pp() != null ? request.pp() : current.pp();

        CurrencyPouch updatedPouch = new CurrencyPouch(cp, sp, ep, gp, pp);
        if (Boolean.TRUE.equals(request.consolidate())) {
            updatedPouch = updatedPouch.consolidate();
        }

        character = character.withCurrencyPouch(updatedPouch);
        characterRepository.save(character);

        return sheetHydrator.hydrate(character);
    }

    @Override
    public CharacterSheetResult updateItem(UUID characterId, UUID userId, UUID characterItemId, UpdateCharacterItemCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);
        CharacterItem characterItem = characterRepository.findCharacterItemByIdAndCharacterId(characterItemId, characterId)
                .orElseThrow(() -> new NoSuchElementException("Item not found in inventory: " + characterItemId));

        Item item = referenceDataRepository.findItemById(characterItem.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item reference not found: " + characterItem.getItemId()));

        int newQuantity = command.quantity() != null ? command.quantity() : characterItem.getQuantity();
        if (newQuantity <= 0) {
            characterRepository.deleteCharacterItem(characterItemId, characterId);
            PlayerCharacter fresh = getCharacter(characterId, userId);
            return sheetHydrator.hydrate(fresh);
        }

        boolean newEquipped = command.equipped() != null ? command.equipped() : characterItem.isEquipped();
        boolean newAttuned = command.attuned() != null ? command.attuned() : characterItem.isAttuned();

        if (newAttuned && !characterItem.isAttuned()) {
            if (!item.isAttunement()) {
                throw new IllegalArgumentException("Item " + item.getName() + " does not require or allow attunement");
            }
            long currentlyAttunedCount = characterRepository.findItemsByCharacterId(characterId).stream()
                    .filter(CharacterItem::isAttuned)
                    .count();
            if (currentlyAttunedCount >= MAX_ATTUNEMENT_SLOTS) {
                throw new IllegalStateException("Maximum attunement slots (3) reached");
            }
        }

        EquipmentSlot targetSlot = null;
        if (newEquipped) {
            List<CharacterItem> existingItems = characterRepository.findItemsByCharacterId(characterId);
            targetSlot = resolveSlotForItem(item, command.equippedSlot(), existingItems);
            validateSlotCompatibility(item, targetSlot);

            Set<UUID> unequippedIds = new HashSet<>();

            // 1. Auto-unequip any other item currently in targetSlot
            for (CharacterItem other : existingItems) {
                if (!other.getId().equals(characterItemId) && other.isEquipped() && targetSlot.equals(other.getEquippedSlot())) {
                    characterRepository.saveCharacterItem(other.withEquipped(false, null));
                    unequippedIds.add(other.getId());
                }
            }

            // 2. If equipping body armor, ensure no other body armor is equipped
            if (isBodyArmor(item)) {
                for (CharacterItem other : existingItems) {
                    if (!other.getId().equals(characterItemId) && !unequippedIds.contains(other.getId()) && other.isEquipped()) {
                        Item otherRef = referenceDataRepository.findItemById(other.getItemId()).orElse(null);
                        if (otherRef != null && isBodyArmor(otherRef)) {
                            characterRepository.saveCharacterItem(other.withEquipped(false, null));
                            unequippedIds.add(other.getId());
                        }
                    }
                }
            }

            // 3. Two-handed weapon checks
            if (isTwoHandedWeapon(item)) {
                for (CharacterItem other : existingItems) {
                    if (!other.getId().equals(characterItemId) && !unequippedIds.contains(other.getId()) && other.isEquipped() && EquipmentSlot.LEFT_HAND.equals(other.getEquippedSlot())) {
                        characterRepository.saveCharacterItem(other.withEquipped(false, null));
                        unequippedIds.add(other.getId());
                    }
                }
            } else if (targetSlot == EquipmentSlot.LEFT_HAND) {
                for (CharacterItem other : existingItems) {
                    if (!other.getId().equals(characterItemId) && !unequippedIds.contains(other.getId()) && other.isEquipped() && EquipmentSlot.RIGHT_HAND.equals(other.getEquippedSlot())) {
                        Item otherRef = referenceDataRepository.findItemById(other.getItemId()).orElse(null);
                        if (otherRef != null && isTwoHandedWeapon(otherRef)) {
                            characterRepository.saveCharacterItem(other.withEquipped(false, null));
                            unequippedIds.add(other.getId());
                        }
                    }
                }
            }
        }

        CharacterItem updated = characterItem
                .withQuantity(newQuantity)
                .withEquipped(newEquipped, newEquipped ? targetSlot : null)
                .withAttuned(newAttuned);

        characterRepository.saveCharacterItem(updated);

        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    @Override
    public CharacterSheetResult removeItem(UUID characterId, UUID userId, UUID characterItemId) {
        PlayerCharacter character = getCharacter(characterId, userId);
        characterRepository.deleteCharacterItem(characterItemId, characterId);
        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    @Override
    public UseConsumableResult useConsumable(UUID characterId, UUID userId, UUID characterItemId) {
        PlayerCharacter character = getCharacter(characterId, userId);
        CharacterItem characterItem = characterRepository.findCharacterItemByIdAndCharacterId(characterItemId, characterId)
                .orElseThrow(() -> new NoSuchElementException("Item not found in inventory: " + characterItemId));

        Item item = referenceDataRepository.findItemById(characterItem.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item reference not found: " + characterItem.getItemId()));

        if (!(item.getMechanicalProperties() instanceof ConsumableMechanics consumable)) {
            throw new IllegalArgumentException("Item " + item.getName() + " is not a consumable item");
        }

        int rolledAmount = 0;
        if (consumable.diceFormula() != null && !consumable.diceFormula().isBlank()) {
            String formula = consumable.diceFormula().trim();
            // Simple dice formula parser like "2d4+2" or "1d8"
            int dIndex = formula.indexOf('d');
            if (dIndex > 0) {
                int count = Integer.parseInt(formula.substring(0, dIndex));
                int plusIndex = formula.indexOf('+');
                int minusIndex = formula.indexOf('-');
                int dieSides;
                int bonus = 0;
                if (plusIndex > 0) {
                    dieSides = Integer.parseInt(formula.substring(dIndex + 1, plusIndex));
                    bonus = Integer.parseInt(formula.substring(plusIndex + 1));
                } else if (minusIndex > 0) {
                    dieSides = Integer.parseInt(formula.substring(dIndex + 1, minusIndex));
                    bonus = -Integer.parseInt(formula.substring(minusIndex + 1));
                } else {
                    dieSides = Integer.parseInt(formula.substring(dIndex + 1));
                }
                for (int i = 0; i < count; i++) {
                    rolledAmount += RANDOM.nextInt(dieSides) + 1;
                }
                rolledAmount += bonus;
            }
        }
        int totalEffectAmount = Math.max(0, rolledAmount);

        PlayerCharacter characterToSave = character;
        String logMessage;

        if (consumable.effectType() == ConsumableEffectType.HEAL_HP) {
            int newHp = Math.min(character.getHitPointsMax(), character.getHitPointsCurrent() + totalEffectAmount);
            characterToSave = character.withCurrentHp(newHp);
            logMessage = "Recuperou " + totalEffectAmount + " pontos de vida.";
        } else if (consumable.effectType() == ConsumableEffectType.GRANT_TEMP_HP) {
            int newTempHp = Math.max(character.getHitPointsTemp(), totalEffectAmount);
            characterToSave = character.withTempHp(newTempHp);
            logMessage = "Ganhou " + totalEffectAmount + " pontos de vida temporários.";
        } else {
            logMessage = "Consumível " + item.getName() + " utilizado: " + consumable.effectDescription();
        }

        // Save character if HP changed
        characterRepository.save(characterToSave);

        // Decrement quantity or delete
        int remainingQuantity = characterItem.getQuantity() - 1;
        if (remainingQuantity <= 0) {
            characterRepository.deleteCharacterItem(characterItemId, characterId);
        } else {
            characterRepository.saveCharacterItem(characterItem.withQuantity(remainingQuantity));
        }

        PlayerCharacter fresh = getCharacter(characterId, userId);
        CharacterSheetResult updatedSheet = sheetHydrator.hydrate(fresh);

        return new UseConsumableResult(
                characterId,
                characterItemId,
                item.getName(),
                consumable.effectType().name(),
                consumable.diceFormula(),
                totalEffectAmount,
                characterToSave.getHitPointsCurrent(),
                characterToSave.getHitPointsMax(),
                characterToSave.getHitPointsTemp(),
                Math.max(0, remainingQuantity)
        );
    }

    private PlayerCharacter getCharacter(UUID characterId, UUID userId) {
        return characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + characterId));
    }

    private EquipmentSlot resolveSlotForItem(Item item, EquipmentSlot requestedSlot, List<CharacterItem> existingItems) {
        if (requestedSlot != null) {
            return requestedSlot;
        }
        if (isShield(item)) {
            return EquipmentSlot.LEFT_HAND;
        }
        if (isBodyArmor(item)) {
            return EquipmentSlot.CHEST;
        }
        if (item.getMechanicalProperties() instanceof WeaponMechanics) {
            return EquipmentSlot.RIGHT_HAND;
        }
        if (isRing(item)) {
            boolean ring1Taken = existingItems.stream()
                    .anyMatch(ci -> ci.isEquipped() && EquipmentSlot.RING_1.equals(ci.getEquippedSlot()));
            if (!ring1Taken) {
                return EquipmentSlot.RING_1;
            }
            return EquipmentSlot.RING_2;
        }

        String name = item.getName() != null ? item.getName().toLowerCase() : "";
        if (name.contains("bota") || name.contains("boot")) return EquipmentSlot.BOOTS;
        if (name.contains("luva") || name.contains("manopla") || name.contains("gauntlet") || name.contains("bracer")) return EquipmentSlot.GLOVES;
        if (name.contains("capacete") || name.contains("tiara") || name.contains("helm") || name.contains("diadema") || name.contains("headband")) return EquipmentSlot.HELMET;
        if (name.contains("calça") || name.contains("calca") || name.contains("perneira")) return EquipmentSlot.PANTS;
        if (name.contains("capa") || name.contains("manto") || name.contains("cloak") || name.contains("cape")) return EquipmentSlot.CLOAK;
        if (name.contains("colar") || name.contains("amuleto") || name.contains("medalhão") || name.contains("medalhao") || name.contains("amulet") || name.contains("necklace") || name.contains("periapt")) return EquipmentSlot.NECKLACE;
        if (name.contains("cinto") || name.contains("cinturão") || name.contains("cinturao") || name.contains("faixa") || name.contains("belt") || name.contains("girdle") || name.contains("charm") || name.contains("talismã") || name.contains("talisma") || name.contains("talisman") || name.contains("trinket") || name.contains("berloque")) return EquipmentSlot.TRINKET;

        return EquipmentSlot.CHEST;
    }

    private void validateSlotCompatibility(Item item, EquipmentSlot slot) {
        if (isBodyArmor(item) && slot != EquipmentSlot.CHEST) {
            throw new IllegalArgumentException("Armor can only be equipped in CHEST slot");
        }
        if (isRing(item) && slot != EquipmentSlot.RING_1 && slot != EquipmentSlot.RING_2) {
            throw new IllegalArgumentException("Ring can only be equipped in RING_1 or RING_2 slot");
        }
        if (isShield(item) && slot != EquipmentSlot.LEFT_HAND && slot != EquipmentSlot.RIGHT_HAND) {
            throw new IllegalArgumentException("Shield can only be equipped in hand slots");
        }
        if (item.getMechanicalProperties() instanceof WeaponMechanics wep) {
            if (slot != EquipmentSlot.RIGHT_HAND && slot != EquipmentSlot.LEFT_HAND) {
                throw new IllegalArgumentException("Weapon can only be equipped in hand slots");
            }
            if (wep.properties().contains(WeaponProperty.TWO_HANDED) && slot != EquipmentSlot.RIGHT_HAND) {
                throw new IllegalArgumentException("Two-handed weapon must be equipped in RIGHT_HAND");
            }
        }
    }

    private boolean isBodyArmor(Item item) {
        if (item.getMechanicalProperties() instanceof ArmorMechanics arm) {
            return arm.armorCategory() != ArmorCategory.SHIELD;
        }
        String cat = item.getItemCategory() != null ? item.getItemCategory().toUpperCase() : "";
        return cat.equals("ARMOR") && !isShield(item);
    }

    private boolean isShield(Item item) {
        if (item.getMechanicalProperties() instanceof ArmorMechanics arm) {
            return arm.armorCategory() == ArmorCategory.SHIELD;
        }
        String name = item.getName() != null ? item.getName().toLowerCase() : "";
        String type = item.getItemType() != null ? item.getItemType().toLowerCase() : "";
        return name.contains("escudo") || name.contains("shield") || type.contains("escudo");
    }

    private boolean isTwoHandedWeapon(Item item) {
        if (item.getMechanicalProperties() instanceof WeaponMechanics wep) {
            return wep.properties().contains(WeaponProperty.TWO_HANDED);
        }
        return false;
    }

    private boolean isRing(Item item) {
        String cat = item.getItemCategory() != null ? item.getItemCategory().toUpperCase() : "";
        String name = item.getName() != null ? item.getName().toLowerCase() : "";
        return cat.equals("RING") || name.contains("anel") || name.contains("ring");
    }
}