package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterActionDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterClassSummaryDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterFeatDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterItemDto;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CurrencyPouchDto;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.ArmorCategory;
import com.dungeoneer.playerCharacter.domain.model.ArmorMechanics;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.CharacterItem;
import com.dungeoneer.playerCharacter.domain.model.DamageRoll;
import com.dungeoneer.playerCharacter.domain.model.DamageType;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.RulesEngine;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Subclass;
import com.dungeoneer.playerCharacter.domain.model.Srd2014RulesEngine;
import com.dungeoneer.playerCharacter.domain.model.Srd2024RulesEngine;
import com.dungeoneer.playerCharacter.domain.model.WeaponMechanics;
import com.dungeoneer.playerCharacter.domain.model.WeaponProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class CharacterSheetHydrator {

    private final ReferenceDataRepository referenceDataRepository;

    public CharacterSheetHydrator(ReferenceDataRepository referenceDataRepository) {
        this.referenceDataRepository = referenceDataRepository;
    }

    public CharacterSheetResult hydrate(PlayerCharacter character) {
        Archetype archetype = null;
        if (!character.getClassProgressions().isEmpty()) {
            UUID archetypeId = character.getClassProgressions().get(0).getArchetypeId();
            archetype = referenceDataRepository.findArchetypeById(archetypeId).orElse(null);
        }

        Lineage lineage = referenceDataRepository.findLineageById(character.getLineageId()).orElse(null);
        Background background = referenceDataRepository.findBackgroundById(character.getBackgroundId()).orElse(null);

        Map<UUID, Item> itemMap = new LinkedHashMap<>();
        for (CharacterItem ci : character.getItems()) {
            referenceDataRepository.findItemById(ci.getItemId()).ifPresent(it -> itemMap.put(ci.getItemId(), it));
        }

        Map<UUID, Feat> featMap = new LinkedHashMap<>();
        for (CharacterFeat cf : character.getFeats()) {
            referenceDataRepository.findFeatById(cf.getFeatId()).ifPresent(f -> featMap.put(cf.getFeatId(), f));
        }

        List<CharacterClassSummaryDto> classes = new ArrayList<>();
        for (com.dungeoneer.playerCharacter.domain.model.ClassProgression cp : character.getClassProgressions()) {
            String cName = referenceDataRepository.findArchetypeById(cp.getArchetypeId())
                    .map(Archetype::getName)
                    .orElse("Unknown");
            String scName = null;
            if (cp.getSubclassId() != null) {
                scName = referenceDataRepository.findSubclassById(cp.getSubclassId())
                        .map(Subclass::getName)
                        .orElse(null);
            }
            classes.add(new CharacterClassSummaryDto(cName, scName, cp.getClassLevel()));
        }

        return toSheetResult(character, archetype, lineage, background, itemMap, featMap, classes);
    }

    private static final Map<String, String> SKILL_TO_ABILITY = new LinkedHashMap<>();

    static {
        SKILL_TO_ABILITY.put("Athletics", "STRENGTH");
        SKILL_TO_ABILITY.put("Acrobatics", "DEXTERITY");
        SKILL_TO_ABILITY.put("Sleight of Hand", "DEXTERITY");
        SKILL_TO_ABILITY.put("Stealth", "DEXTERITY");
        SKILL_TO_ABILITY.put("Arcana", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("History", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Investigation", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Nature", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Religion", "INTELLIGENCE");
        SKILL_TO_ABILITY.put("Animal Handling", "WISDOM");
        SKILL_TO_ABILITY.put("Insight", "WISDOM");
        SKILL_TO_ABILITY.put("Medicine", "WISDOM");
        SKILL_TO_ABILITY.put("Perception", "WISDOM");
        SKILL_TO_ABILITY.put("Survival", "WISDOM");
        SKILL_TO_ABILITY.put("Deception", "CHARISMA");
        SKILL_TO_ABILITY.put("Intimidation", "CHARISMA");
        SKILL_TO_ABILITY.put("Performance", "CHARISMA");
        SKILL_TO_ABILITY.put("Persuasion", "CHARISMA");
    }



    public static CharacterSheetResult toSheetResult(
            PlayerCharacter character,
            Archetype archetype,
            Lineage lineage,
            Background background
    ) {
        return toSheetResult(character, archetype, lineage, background, Collections.emptyMap(), Collections.emptyMap());
    }

    public static CharacterSheetResult toSheetResult(
            PlayerCharacter character,
            Archetype archetype,
            Lineage lineage,
            Background background,
            Map<UUID, Item> itemMap,
            Map<UUID, Feat> featMap
    ) {
        return toSheetResult(character, archetype, lineage, background, itemMap, featMap, Collections.emptyList());
    }

    public static CharacterSheetResult toSheetResult(
            PlayerCharacter character,
            Archetype archetype,
            Lineage lineage,
            Background background,
            Map<UUID, Item> itemMap,
            Map<UUID, Feat> featMap,
            List<CharacterClassSummaryDto> classes
    ) {
        Map<String, Integer> savingThrows = calculateSavingThrows(character, archetype);
        Map<String, Integer> skills = calculateSkills(character, background);

        List<CharacterItemDto> inventory = new ArrayList<>();
        List<CharacterActionDto> actions = new ArrayList<>();
        ArmorMechanics equippedArmor = null;
        ArmorMechanics equippedShield = null;
        int passiveAcBonus = 0;

        for (CharacterItem charItem : character.getItems()) {
            Item item = itemMap.get(charItem.getItemId());
            inventory.add(CharacterItemDto.fromDomain(charItem, item));

            if (item != null && charItem.isEquipped()) {
                if (item.getMechanicalProperties() instanceof ArmorMechanics arm) {
                    if (arm.armorCategory() == ArmorCategory.SHIELD) {
                        equippedShield = arm;
                    } else {
                        equippedArmor = arm;
                    }
                } else if (item.getMechanicalProperties() instanceof WeaponMechanics wep) {
                    actions.add(buildWeaponAction(character, item, wep));
                }
            }
        }

        List<CharacterFeatDto> feats = new ArrayList<>();
        boolean hasBarbUnarmored = false;
        boolean hasMonkUnarmored = false;

        for (CharacterFeat charFeat : character.getFeats()) {
            Feat feat = featMap.get(charFeat.getFeatId());
            feats.add(CharacterFeatDto.fromDomain(charFeat, feat));

            if (feat != null) {
                String normalized = feat.getName().toUpperCase();
                if (normalized.contains("DEFESA SEM ARMADURA") || normalized.contains("UNARMORED DEFENSE")) {
                    if (archetype != null && "BARBARIAN".equalsIgnoreCase(archetype.getName())) {
                        hasBarbUnarmored = true;
                    } else if (archetype != null && "MONK".equalsIgnoreCase(archetype.getName())) {
                        hasMonkUnarmored = true;
                    }
                }
                Integer currentCharges = charFeat.getCurrentCharges();
                Integer maxCharges = charFeat.getMaxCharges();

                if (maxCharges == null && feat.getMechanicalEffects() != null) {
                    if (feat.getMechanicalEffects().usesPerRest() != null) {
                        maxCharges = feat.getMechanicalEffects().usesPerRest();
                    }
                }

                if (normalized.contains("FÔLEGO") || normalized.contains("FOLEGO") || normalized.contains("SECOND WIND")) {
                    if (maxCharges == null || maxCharges == 0) {
                        maxCharges = (character.getRuleset() == Ruleset.SRD_2024) ? 2 : 1;
                    }
                }

                if (currentCharges == null && maxCharges != null) {
                    currentCharges = maxCharges;
                }

                if (normalized.contains("ATAQUE DESCUIDADO") || normalized.contains("RECKLESS ATTACK")) {
                    int strMod = character.getAbilityScores() != null ? character.getAbilityScores().getStrengthModifier() : 0;
                    int attackBonus = strMod + character.getProficiencyBonus();
                    actions.add(new CharacterActionDto(
                            charFeat.getId(),
                            feat.getName(),
                            "SPECIAL",
                            "FEAT",
                            attackBonus,
                            List.of(new DamageRoll("2d20kh1", DamageType.FORCE)),
                            null,
                            feat.getDescription(),
                            currentCharges,
                            maxCharges
                    ));
                    continue;
                }
                if (feat.getMechanicalEffects() != null) {
                    passiveAcBonus += feat.getMechanicalEffects().acBonus();
                    if (feat.getMechanicalEffects().actionName() != null && !feat.getMechanicalEffects().actionName().isBlank()) {
                        String formula = feat.getMechanicalEffects().diceFormula();
                        boolean isHealing = normalized.contains("FÔLEGO")
                                || normalized.contains("FOLEGO")
                                || normalized.contains("WIND")
                                || normalized.contains("CURA")
                                || (feat.getDescription() != null && (
                                        feat.getDescription().toLowerCase().contains("recupera")
                                        || feat.getDescription().toLowerCase().contains("cura")
                                        || feat.getDescription().toLowerCase().contains("pv")));

                        if (normalized.contains("FÔLEGO") || normalized.contains("FOLEGO") || normalized.contains("SECOND WIND")) {
                            int fighterLevel = 1;
                            if (classes != null && !classes.isEmpty()) {
                                for (CharacterClassSummaryDto cls : classes) {
                                    String cName = cls.className() != null ? cls.className().toUpperCase() : "";
                                    if (cName.contains("FIGHTER") || cName.contains("GUERREIRO")) {
                                        fighterLevel = cls.classLevel();
                                        break;
                                    }
                                }
                            } else if (archetype != null && (archetype.getName().toUpperCase().contains("FIGHTER") || archetype.getName().toUpperCase().contains("GUERREIRO"))) {
                                fighterLevel = character.getLevel();
                            }
                            formula = "1d10+" + fighterLevel;
                            isHealing = true;
                        }

                        List<DamageRoll> damageRolls = List.of();
                        if (formula != null && !formula.isBlank()) {
                            DamageType dt = isHealing ? DamageType.HEALING : DamageType.FORCE;
                            damageRolls = List.of(new DamageRoll(formula, dt));
                        }

                        actions.add(new CharacterActionDto(
                                charFeat.getId(),
                                feat.getMechanicalEffects().actionName(),
                                feat.getMechanicalEffects().actionType() != null ? feat.getMechanicalEffects().actionType() : "ACTION",
                                "FEAT",
                                null,
                                damageRolls,
                                null,
                                feat.getDescription(),
                                currentCharges,
                                maxCharges
                        ));
                    }
                }
            }
        }

        RulesEngine engine = character.getRuleset() == Ruleset.SRD_2024 ? new Srd2024RulesEngine() : new Srd2014RulesEngine();
        int calculatedAc = engine.calculateArmorClass(
                character.getAbilityScores().getDexterityModifier(),
                equippedArmor,
                equippedShield,
                passiveAcBonus,
                hasBarbUnarmored,
                character.getAbilityScores().getConstitutionModifier(),
                hasMonkUnarmored,
                character.getAbilityScores().getWisdomModifier()
        );

        return new CharacterSheetResult(
                character.getId(),
                character.getUserId(),
                character.getName(),
                character.getLevel(),
                character.getHitPointsCurrent(),
                character.getHitPointsMax(),
                character.getHitPointsTemp(),
                calculatedAc,
                character.getInitiativeBonus(),
                character.getSpeed(),
                character.getProficiencyBonus(),
                character.getRuleset(),
                character.getAbilityScoreMethod(),
                character.getHpCalculationMethod(),
                character.isHasPendingLevelChoices(),
                character.getNotes(),
                character.getAbilityScores(),
                lineage,
                background,
                archetype,
                character.getClassProgressions(),
                savingThrows,
                skills,
                character.getExperiencePoints(),
                character.getCampaignId(),
                inventory,
                feats,
                actions,
                CurrencyPouchDto.fromDomain(character.getCurrencyPouch()),
                character.getAvatarUrl(),
                classes,
                character.getCreatedAt(),
                character.getUpdatedAt()
        );
    }

    private static CharacterActionDto buildWeaponAction(PlayerCharacter character, Item item, WeaponMechanics wep) {
        boolean isAmmunition = wep.properties() != null && wep.properties().contains(WeaponProperty.AMMUNITION);
        boolean isFinesse = wep.properties() != null && wep.properties().contains(WeaponProperty.FINESSE);
        int strMod = character.getAbilityScores() != null ? character.getAbilityScores().getStrengthModifier() : 0;
        int dexMod = character.getAbilityScores() != null ? character.getAbilityScores().getDexterityModifier() : 0;
        int attackMod = isAmmunition ? dexMod : (isFinesse ? Math.max(strMod, dexMod) : strMod);
        int attackBonus = attackMod + character.getProficiencyBonus() + wep.magicBonus();
        int damageBonus = attackMod + wep.magicBonus();
        String bonusSuffix = damageBonus > 0 ? "+" + damageBonus : (damageBonus < 0 ? String.valueOf(damageBonus) : "");

        List<DamageRoll> adjustedRolls = new ArrayList<>();
        if (wep.damageRolls() != null && !wep.damageRolls().isEmpty()) {
            boolean isFirst = true;
            for (DamageRoll roll : wep.damageRolls()) {
                if (isFirst) {
                    String baseFormula = roll.diceFormula();
                    String pureDice = baseFormula.replaceAll("([+-]\\d+)$", "").trim();
                    String fullFormula = pureDice + bonusSuffix;
                    adjustedRolls.add(new DamageRoll(fullFormula, roll.damageType(), roll.conditional(), roll.conditionNote()));
                    isFirst = false;
                } else {
                    adjustedRolls.add(roll);
                }
            }
        }

        String adjustedVersatile = wep.versatileDice();
        if (adjustedVersatile != null && !adjustedVersatile.isBlank()) {
            String pureVersatile = adjustedVersatile.replaceAll("([+-]\\d+)$", "").trim();
            adjustedVersatile = pureVersatile + bonusSuffix;
        }

        return new CharacterActionDto(
                item.getId(),
                item.getName(),
                "ACTION",
                "WEAPON",
                attackBonus,
                adjustedRolls,
                adjustedVersatile,
                item.getDescription(),
                null,
                null
        );
    }

    private static Map<String, Integer> calculateSavingThrows(PlayerCharacter character, Archetype archetype) {
        Map<String, Integer> result = new LinkedHashMap<>();
        AbilityScores scores = character.getAbilityScores();
        int prof = character.getProficiencyBonus();

        Set<String> proficientAbilities = new HashSet<>();
        if (archetype != null && archetype.getSavingThrowProficiencies() != null) {
            proficientAbilities.addAll(
                    archetype.getSavingThrowProficiencies().stream()
                            .map(Srd2014RulesEngine::normalizeAbility)
                            .collect(Collectors.toSet())
            );
        }

        result.put("STRENGTH", scores.getStrengthModifier() + (proficientAbilities.contains("STRENGTH") ? prof : 0));
        result.put("DEXTERITY", scores.getDexterityModifier() + (proficientAbilities.contains("DEXTERITY") ? prof : 0));
        result.put("CONSTITUTION", scores.getConstitutionModifier() + (proficientAbilities.contains("CONSTITUTION") ? prof : 0));
        result.put("INTELLIGENCE", scores.getIntelligenceModifier() + (proficientAbilities.contains("INTELLIGENCE") ? prof : 0));
        result.put("WISDOM", scores.getWisdomModifier() + (proficientAbilities.contains("WISDOM") ? prof : 0));
        result.put("CHARISMA", scores.getCharismaModifier() + (proficientAbilities.contains("CHARISMA") ? prof : 0));

        return result;
    }

    private static Map<String, Integer> calculateSkills(PlayerCharacter character, Background background) {
        Map<String, Integer> result = new LinkedHashMap<>();
        AbilityScores scores = character.getAbilityScores();
        int prof = character.getProficiencyBonus();

        Set<String> characterSkills = new HashSet<>();
        if (character.getSkillProficiencies() != null) {
            characterSkills.addAll(character.getSkillProficiencies());
        }
        if (background != null && background.getSkillProficiencies() != null) {
            characterSkills.addAll(background.getSkillProficiencies());
        }

        for (Map.Entry<String, String> entry : SKILL_TO_ABILITY.entrySet()) {
            String skill = entry.getKey();
            String ability = entry.getValue();
            int abilityMod = getAbilityModifier(scores, ability);
            boolean isProf = characterSkills.contains(skill);
            result.put(skill, abilityMod + (isProf ? prof : 0));
        }

        return result;
    }

    private static int getAbilityModifier(AbilityScores scores, String ability) {
        return switch (ability) {
            case "STRENGTH" -> scores.getStrengthModifier();
            case "DEXTERITY" -> scores.getDexterityModifier();
            case "CONSTITUTION" -> scores.getConstitutionModifier();
            case "INTELLIGENCE" -> scores.getIntelligenceModifier();
            case "WISDOM" -> scores.getWisdomModifier();
            case "CHARISMA" -> scores.getCharismaModifier();
            default -> 0;
        };
    }
}
