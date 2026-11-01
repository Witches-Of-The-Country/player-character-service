package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewArchetypeRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewItemRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewLineageRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSpellRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSubclassRequest;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.dto.SubclassDto;
import com.dungeoneer.playerCharacter.application.port.in.CreateHomebrewUseCase;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.FeatType;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.Spell;
import com.dungeoneer.playerCharacter.domain.model.SpellPreparationType;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import com.dungeoneer.playerCharacter.domain.model.Subclass;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional
public class HomebrewService implements CreateHomebrewUseCase {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private final ReferenceDataRepository repository;

    public HomebrewService(ReferenceDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public SpellDto createSpell(UUID ownerId, CreateHomebrewSpellRequest request) {
        if (ownerId == null) {
            throw new CharacterValidationException("Owner id is required for homebrew creation");
        }
        if (request == null) {
            throw new CharacterValidationException("Spell creation payload cannot be null");
        }

        UUID id = UUID.randomUUID();
        String slug = toSlug(request.name());
        String nameEn = (request.nameEn() != null && !request.nameEn().isBlank()) ? request.nameEn() : slug;

        Spell spell = new Spell(
                id,
                Ruleset.HOMEBREW,
                slug,
                nameEn,
                request.name(),
                request.level(),
                request.school(),
                request.castingTime(),
                request.rangeArea(),
                request.components(),
                request.duration(),
                request.description(),
                "HOMEBREW",
                ownerId,
                request.mechanicalProperties()
        );

        Spell saved = repository.saveSpell(spell);
        return SpellDto.fromDomain(saved);
    }

    @Override
    public FeatDto createFeat(UUID ownerId, CreateHomebrewFeatRequest request) {
        if (ownerId == null) {
            throw new CharacterValidationException("Owner id is required for homebrew creation");
        }
        if (request == null) {
            throw new CharacterValidationException("Feat creation payload cannot be null");
        }

        UUID id = UUID.randomUUID();
        String slug = toSlug(request.name());
        String nameEn = (request.nameEn() != null && !request.nameEn().isBlank()) ? request.nameEn() : slug;

        Feat feat = new Feat(
                id,
                Ruleset.HOMEBREW,
                slug,
                nameEn,
                request.name(),
                request.prerequisites(),
                request.description(),
                "HOMEBREW",
                ownerId,
                request.featType() != null ? request.featType() : FeatType.GENERAL,
                request.mechanicalEffects() != null ? request.mechanicalEffects() : MechanicalEffects.empty()
        );

        Feat saved = repository.saveFeat(feat);
        return FeatDto.fromDomain(saved);
    }

    @Override
    public ItemDto createItem(UUID ownerId, CreateHomebrewItemRequest request) {
        if (ownerId == null) {
            throw new CharacterValidationException("Owner id is required for homebrew creation");
        }
        if (request == null) {
            throw new CharacterValidationException("Item creation payload cannot be null");
        }

        UUID id = UUID.randomUUID();
        String slug = toSlug(request.name());
        String nameEn = (request.nameEn() != null && !request.nameEn().isBlank()) ? request.nameEn() : slug;
        String rarity = (request.rarity() != null && !request.rarity().isBlank()) ? request.rarity().toUpperCase() : "COMMON";

        Item item = new Item(
                id,
                Ruleset.HOMEBREW,
                slug,
                nameEn,
                request.name(),
                request.itemCategory().toUpperCase(),
                request.itemType(),
                rarity,
                request.isAttunement(),
                request.cost(),
                request.weight(),
                request.description(),
                "HOMEBREW",
                ownerId,
                request.resolveMechanicalProperties()
        );

        Item saved = repository.saveItem(item);
        return ItemDto.fromDomain(saved);
    }

    @Override
    public ArchetypeDto createArchetype(UUID ownerId, CreateHomebrewArchetypeRequest request) {
        if (ownerId == null) {
            throw new CharacterValidationException("Owner id is required for homebrew creation");
        }
        if (request == null) {
            throw new CharacterValidationException("Archetype creation payload cannot be null");
        }

        HitDiceEnum hitDice;
        try {
            hitDice = HitDiceEnum.valueOf(request.hitDice().trim().toUpperCase());
        } catch (Exception e) {
            hitDice = HitDiceEnum.D8;
        }

        SpellcastingType spellcastingType;
        try {
            spellcastingType = SpellcastingType.valueOf(request.spellcastingType().trim().toUpperCase());
        } catch (Exception e) {
            spellcastingType = SpellcastingType.NONE;
        }

        UUID id = UUID.randomUUID();

        int subclassLevel = (request.subclassLevel() != null && request.subclassLevel() >= 1 && request.subclassLevel() <= 20)
                ? request.subclassLevel()
                : 3;

        SpellPreparationType prepType = SpellPreparationType.NONE;
        if (request.spellPreparationType() != null && !request.spellPreparationType().isBlank()) {
            try {
                prepType = SpellPreparationType.valueOf(request.spellPreparationType().trim().toUpperCase());
            } catch (Exception ignored) {
            }
        }

        Archetype archetype = new Archetype(
                id,
                request.name(),
                hitDice,
                request.primaryAbility(),
                request.savingThrowProficiencies(),
                request.armorProficiencies() != null ? request.armorProficiencies() : Collections.emptyList(),
                request.weaponProficiencies() != null ? request.weaponProficiencies() : Collections.emptyList(),
                spellcastingType,
                request.spellcastingAbility(),
                Ruleset.HOMEBREW,
                "HOMEBREW",
                ownerId,
                subclassLevel,
                prepType
        );

        Archetype saved = repository.saveArchetype(archetype);

        if (request.progressionFeats() != null) {
            for (var entry : request.progressionFeats()) {
                int unlockLevel = Math.max(1, Math.min(20, entry.unlockLevel()));
                UUID targetFeatId = entry.featId();
                if (targetFeatId == null && entry.name() != null && !entry.name().isBlank()) {
                    UUID newFeatId = UUID.randomUUID();
                    String slug = toSlug(entry.name());
                    Feat inlineFeat = new Feat(
                            newFeatId,
                            Ruleset.HOMEBREW,
                            slug,
                            slug,
                            entry.name().trim(),
                            null,
                            entry.description() != null && !entry.description().isBlank() ? entry.description().trim() : entry.name().trim(),
                            "HOMEBREW",
                            ownerId,
                            FeatType.CLASS,
                            entry.mechanicalEffects() != null ? entry.mechanicalEffects() : MechanicalEffects.empty()
                    );
                    Feat savedFeat = repository.saveFeat(inlineFeat);
                    targetFeatId = savedFeat.getId();
                }
                if (targetFeatId != null) {
                    repository.saveArchetypeFeatLink(saved.getId(), targetFeatId, unlockLevel);
                }
            }
        }

        return ArchetypeDto.fromDomain(saved);
    }

    @Override
    public LineageDto createLineage(UUID ownerId, CreateHomebrewLineageRequest request) {
        if (ownerId == null) {
            throw new CharacterValidationException("Owner id is required for homebrew creation");
        }
        if (request == null) {
            throw new CharacterValidationException("Lineage creation payload cannot be null");
        }

        UUID id = UUID.randomUUID();
        String size = (request.size() != null && !request.size().isBlank()) ? request.size().toUpperCase() : "MEDIUM";

        Lineage lineage = new Lineage(
                id,
                request.name(),
                request.description(),
                request.speed() > 0 ? request.speed() : 30,
                size,
                request.abilityBonuses() != null ? request.abilityBonuses() : Collections.emptyMap(),
                request.traitsSummary() != null ? request.traitsSummary() : Collections.emptyList(),
                Ruleset.HOMEBREW,
                "HOMEBREW",
                ownerId
        );

        Lineage saved = repository.saveLineage(lineage);

        if (request.inlineFeats() != null) {
            for (var entry : request.inlineFeats()) {
                int unlockLevel = Math.max(1, Math.min(20, entry.unlockLevel()));
                UUID targetFeatId = entry.featId();
                if (targetFeatId == null && entry.name() != null && !entry.name().isBlank()) {
                    UUID newFeatId = UUID.randomUUID();
                    String featSlug = toSlug(entry.name());
                    Feat inlineFeat = new Feat(
                            newFeatId,
                            Ruleset.HOMEBREW,
                            featSlug,
                            featSlug,
                            entry.name().trim(),
                            null,
                            entry.description() != null && !entry.description().isBlank() ? entry.description().trim() : entry.name().trim(),
                            "HOMEBREW",
                            ownerId,
                            FeatType.LINEAGE,
                            entry.mechanicalEffects() != null ? entry.mechanicalEffects() : MechanicalEffects.empty()
                    );
                    Feat savedFeat = repository.saveFeat(inlineFeat);
                    targetFeatId = savedFeat.getId();
                }
                if (targetFeatId != null) {
                    repository.saveLineageFeatLink(saved.getId(), targetFeatId, unlockLevel);
                }
            }
        }

        return LineageDto.fromDomain(saved);
    }

    @Override
    public SubclassDto createSubclass(UUID ownerId, CreateHomebrewSubclassRequest request) {
        if (ownerId == null) {
            throw new CharacterValidationException("Owner id is required for homebrew creation");
        }
        if (request == null) {
            throw new CharacterValidationException("Subclass payload cannot be null");
        }
        if (request.archetypeId() == null) {
            throw new CharacterValidationException("Archetype id is required");
        }

        Archetype archetype = repository.findArchetypeById(request.archetypeId())
                .orElseThrow(() -> new CharacterValidationException("Archetype not found: " + request.archetypeId()));

        UUID id = UUID.randomUUID();
        String slug = toSlug(request.name());
        String nameEn = (request.nameEn() != null && !request.nameEn().isBlank()) ? request.nameEn() : slug;

        Subclass subclass = new Subclass(
                id,
                archetype.getId(),
                Ruleset.HOMEBREW,
                slug,
                nameEn,
                request.name(),
                request.description(),
                "HOMEBREW",
                ownerId
        );

        Subclass saved = repository.saveSubclass(subclass);

        if (request.progressionFeats() != null) {
            for (var entry : request.progressionFeats()) {
                int unlockLevel = Math.max(1, Math.min(20, entry.unlockLevel()));
                UUID targetFeatId = entry.featId();
                if (targetFeatId == null && entry.name() != null && !entry.name().isBlank()) {
                    UUID newFeatId = UUID.randomUUID();
                    String featSlug = toSlug(entry.name());
                    Feat inlineFeat = new Feat(
                            newFeatId,
                            Ruleset.HOMEBREW,
                            featSlug,
                            featSlug,
                            entry.name().trim(),
                            null,
                            entry.description() != null && !entry.description().isBlank() ? entry.description().trim() : entry.name().trim(),
                            "HOMEBREW",
                            ownerId,
                            FeatType.SUBCLASS,
                            entry.mechanicalEffects() != null ? entry.mechanicalEffects() : MechanicalEffects.empty()
                    );
                    Feat savedFeat = repository.saveFeat(inlineFeat);
                    targetFeatId = savedFeat.getId();
                }
                if (targetFeatId != null) {
                    repository.saveSubclassFeatLink(saved.getId(), targetFeatId, unlockLevel);
                }
            }
        }

        return SubclassDto.fromDomain(saved);
    }

    private String toSlug(String input) {
        if (input == null) return UUID.randomUUID().toString();
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH) + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
