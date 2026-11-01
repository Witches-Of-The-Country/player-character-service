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

        CharacterItem updated = characterItem
                .withQuantity(newQuantity)
                .withEquipped(newEquipped)
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
}