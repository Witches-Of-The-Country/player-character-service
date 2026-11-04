package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.RestCommand;
import com.dungeoneer.playerCharacter.application.port.in.RestUseCase;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.RestValidationException;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.RestType;
import com.dungeoneer.playerCharacter.domain.model.RulesEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class RestService implements RestUseCase {

    @FunctionalInterface
    public interface DiceRoller {
        int roll(int sides);
    }

    private final PlayerCharacterRepositoryPort characterRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final CharacterSheetHydrator sheetHydrator;
    private final DiceRoller diceRoller;

    public RestService(
            PlayerCharacterRepositoryPort characterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator sheetHydrator
    ) {
        this(characterRepository, referenceDataRepository, sheetHydrator, sides -> ThreadLocalRandom.current().nextInt(1, Math.max(1, sides) + 1));
    }

    public RestService(
            PlayerCharacterRepositoryPort characterRepository,
            ReferenceDataRepository referenceDataRepository,
            CharacterSheetHydrator sheetHydrator,
            DiceRoller diceRoller
    ) {
        this.characterRepository = characterRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.sheetHydrator = sheetHydrator;
        this.diceRoller = diceRoller != null ? diceRoller : sides -> ThreadLocalRandom.current().nextInt(1, Math.max(1, sides) + 1);
    }

    @Override
    public CharacterSheetResult takeRest(UUID characterId, UUID userId, RestCommand command) {
        PlayerCharacter character = getCharacter(characterId, userId);

        if (character.getHitPointsCurrent() <= 0) {
            throw new CharacterValidationException("Dead or unconscious characters (0 HP) cannot take a rest.");
        }

        RestType type = command.restType();
        RulesEngine rulesEngine = RulesEngine.forRuleset(character.getRuleset());
        int proficiencyBonus = rulesEngine.calculateProficiencyBonus(character.getLevel());

        PlayerCharacter characterToSave = character;

        if (type == RestType.LONG) {
            // Long Rest:
            // 1. Restore current HP to max HP (PRESERVING temporary hit points)
            characterToSave = characterToSave.withCurrentHp(character.getHitPointsMax());

            // 2. Hit Dice recovery: half of total character level rounded down, min 1
            int diceToRecover = rulesEngine.calculateHitDiceRecovery(character.getLevel());
            List<ClassProgression> updatedProgressions = new ArrayList<>();
            for (ClassProgression cp : character.getClassProgressions()) {
                if (diceToRecover <= 0 || cp.getHitDiceSpent() <= 0) {
                    updatedProgressions.add(cp);
                } else {
                    int recoverFromThis = Math.min(diceToRecover, cp.getHitDiceSpent());
                    updatedProgressions.add(cp.recoverHitDice(recoverFromThis));
                    diceToRecover -= recoverFromThis;
                }
            }
            characterToSave = characterToSave.withClassProgressions(updatedProgressions);

            // 3. Exhaustion recovery: decrement by 1, minimum 0
            int newExhaustion = rulesEngine.calculateExhaustionRecovery(character.getExhaustion());
            characterToSave = characterToSave.withExhaustion(newExhaustion);

            // 4. Recover feat charges configured for long rest
            recoverFeats(characterId, character, RestType.LONG, proficiencyBonus);

            // 5. Restore spell slots: all restored (when modeled in entity)

        } else if (type == RestType.SHORT) {
            // Short Rest:
            int conModifier = character.getAbilityScores() != null ? character.getAbilityScores().getConstitutionModifier() : 0;
            int totalHealed = 0;

            if (command.hitDiceSpends() != null && !command.hitDiceSpends().isEmpty()) {
                Map<UUID, ClassProgression> progressionMap = new LinkedHashMap<>();
                for (ClassProgression cp : character.getClassProgressions()) {
                    progressionMap.put(cp.getId(), cp);
                }

                for (RestCommand.HitDiceSpendRequest spendReq : command.hitDiceSpends()) {
                    ClassProgression cp = progressionMap.get(spendReq.classProgressionId());
                    if (cp == null) {
                        throw new RestValidationException("Class progression not found: " + spendReq.classProgressionId());
                    }
                    if (spendReq.count() > cp.getAvailableHitDice()) {
                        throw new RestValidationException(
                                "Cannot spend " + spendReq.count() + " Hit Dice. Only " + cp.getAvailableHitDice() + " available for class progression " + cp.getId()
                        );
                    }

                    int dieValue = 8;
                    if (referenceDataRepository != null) {
                        Archetype archetype = referenceDataRepository.findArchetypeById(cp.getArchetypeId()).orElse(null);
                        if (archetype != null && archetype.getHitDice() != null) {
                            dieValue = archetype.getHitDice().getDieValue();
                        }
                    }

                    for (int i = 0; i < spendReq.count(); i++) {
                        int roll = diceRoller.roll(dieValue);
                        int healedThisDie = rulesEngine.calculateHitDieHealing(roll, conModifier);
                        totalHealed += healedThisDie;
                    }

                    progressionMap.put(cp.getId(), cp.spendHitDice(spendReq.count()));
                }

                characterToSave = characterToSave.withClassProgressions(new ArrayList<>(progressionMap.values()));
            } else if (command.hpHealed() != null && command.hpHealed() > 0) {
                totalHealed = command.hpHealed();
            }

            int newHp = Math.min(character.getHitPointsMax(), character.getHitPointsCurrent() + totalHealed);
            characterToSave = characterToSave.withCurrentHp(newHp);

            // Recover feat charges configured for short rest
            recoverFeats(characterId, character, RestType.SHORT, proficiencyBonus);
        }

        characterRepository.save(characterToSave);

        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    @Override
    public CharacterSheetResult useFeat(UUID characterId, UUID userId, UUID characterFeatId) {
        PlayerCharacter character = getCharacter(characterId, userId);
        CharacterFeat feat = characterRepository.findFeatsByCharacterId(characterId).stream()
                .filter(cf -> cf.getId().equals(characterFeatId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Character feat not found: " + characterFeatId));

        if (feat.getMaxCharges() != null && feat.getMaxCharges() > 0) {
            if (feat.getCurrentCharges() == null || feat.getCurrentCharges() <= 0) {
                throw new IllegalStateException("No charges remaining for feat");
            }
            characterRepository.saveCharacterFeat(feat.withCurrentCharges(feat.getCurrentCharges() - 1));
        }

        PlayerCharacter fresh = getCharacter(characterId, userId);
        return sheetHydrator.hydrate(fresh);
    }

    private void recoverFeats(UUID characterId, PlayerCharacter character, RestType restType, int proficiencyBonus) {
        List<CharacterFeat> feats = characterRepository.findFeatsByCharacterId(characterId);
        for (CharacterFeat feat : feats) {
            if (feat.getMaxCharges() != null && feat.getMaxCharges() > 0) {
                Feat featDef = referenceDataRepository != null
                        ? referenceDataRepository.findFeatById(feat.getFeatId()).orElse(null)
                        : null;
                MechanicalEffects effects = featDef != null ? featDef.getMechanicalEffects() : null;
                CharacterFeat recovered = feat.recoverCharges(
                        restType,
                        effects,
                        character.getAbilityScores(),
                        proficiencyBonus,
                        character.getLevel()
                );
                characterRepository.saveCharacterFeat(recovered);
            }
        }
    }

    private PlayerCharacter getCharacter(UUID characterId, UUID userId) {
        return characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + characterId));
    }
}
