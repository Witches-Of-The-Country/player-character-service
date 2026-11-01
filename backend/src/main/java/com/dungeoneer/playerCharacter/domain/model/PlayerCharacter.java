package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerCharacter {

    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 20;
    public static final int DEFAULT_SPEED = 30;
    public static final int BASE_ARMOR_CLASS = 10;
    public static final int MAX_ATTUNED_ITEMS = 3;

    private final UUID id;
    private final String name;
    private final int level;
    private final int hitPointsCurrent;
    private final int hitPointsMax;
    private final int hitPointsTemp;
    private final int armorClass;
    private final int initiativeBonus;
    private final int speed;
    private final int proficiencyBonus;
    private final Ruleset ruleset;
    private final AbilityScoreMethod abilityScoreMethod;
    private final UUID lineageId;
    private final UUID backgroundId;
    private final UUID userId;
    private final AbilityScores abilityScores;
    @Deprecated
    private final String notes;
    private final HpCalculationMethod hpCalculationMethod;
    private final boolean hasPendingLevelChoices;
    private final List<ClassProgression> classProgressions;
    private final List<String> skillProficiencies;
    private final int experiencePoints;
    private final UUID campaignId;
    private final List<CharacterItem> items;
    private final List<CharacterFeat> feats;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PlayerCharacter(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            String notes,
            HpCalculationMethod hpCalculationMethod,
            boolean hasPendingLevelChoices,
            List<ClassProgression> classProgressions,
            List<String> skillProficiencies,
            int experiencePoints,
            UUID campaignId,
            List<CharacterItem> items,
            List<CharacterFeat> feats,
            Instant createdAt,
            Instant updatedAt
    ) {
        validateName(name);
        validateLevel(level);
        validateHitPoints(hitPointsCurrent, hitPointsMax, hitPointsTemp);

        if (ruleset == null) {
            throw new CharacterValidationException("Ruleset cannot be null.");
        }
        if (abilityScoreMethod == null) {
            throw new CharacterValidationException("AbilityScoreMethod cannot be null.");
        }
        if (lineageId == null) {
            throw new CharacterValidationException("LineageId cannot be null.");
        }
        if (backgroundId == null) {
            throw new CharacterValidationException("BackgroundId cannot be null.");
        }
        if (abilityScores == null) {
            throw new CharacterValidationException("AbilityScores cannot be null.");
        }
        if (experiencePoints < 0) {
            throw new CharacterValidationException("Experience points cannot be negative. Provided: " + experiencePoints);
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.name = name.trim();
        this.level = level;
        this.hitPointsCurrent = hitPointsCurrent;
        this.hitPointsMax = hitPointsMax;
        this.hitPointsTemp = hitPointsTemp;
        this.armorClass = armorClass;
        this.initiativeBonus = initiativeBonus;
        this.speed = speed > 0 ? speed : DEFAULT_SPEED;
        this.proficiencyBonus = proficiencyBonus;
        this.ruleset = ruleset;
        this.abilityScoreMethod = abilityScoreMethod;
        this.lineageId = lineageId;
        this.backgroundId = backgroundId;
        this.userId = userId;
        this.abilityScores = abilityScores;
        this.notes = notes != null ? notes : "";
        this.hpCalculationMethod = hpCalculationMethod != null ? hpCalculationMethod : HpCalculationMethod.MAX;
        this.hasPendingLevelChoices = hasPendingLevelChoices;
        this.classProgressions = classProgressions != null
                ? Collections.unmodifiableList(new ArrayList<>(classProgressions))
                : Collections.emptyList();
        this.skillProficiencies = skillProficiencies != null
                ? Collections.unmodifiableList(new ArrayList<>(skillProficiencies))
                : Collections.emptyList();
        this.experiencePoints = experiencePoints;
        this.campaignId = campaignId;
        this.items = items != null
                ? Collections.unmodifiableList(new ArrayList<>(items))
                : Collections.emptyList();
        this.feats = feats != null
                ? Collections.unmodifiableList(new ArrayList<>(feats))
                : Collections.emptyList();
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    // Overload for backwards compatibility with earlier constructors
    public PlayerCharacter(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            String notes,
            HpCalculationMethod hpCalculationMethod,
            boolean hasPendingLevelChoices,
            List<ClassProgression> classProgressions,
            List<String> skillProficiencies,
            Instant createdAt,
            Instant updatedAt
    ) {
        this(
                id,
                name,
                level,
                hitPointsCurrent,
                hitPointsMax,
                hitPointsTemp,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                notes,
                hpCalculationMethod,
                hasPendingLevelChoices,
                classProgressions,
                skillProficiencies,
                0,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                createdAt,
                updatedAt
        );
    }

    public PlayerCharacter(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            String notes,
            HpCalculationMethod hpCalculationMethod,
            boolean hasPendingLevelChoices,
            List<ClassProgression> classProgressions,
            Instant createdAt,
            Instant updatedAt
    ) {
        this(
                id,
                name,
                level,
                hitPointsCurrent,
                hitPointsMax,
                hitPointsTemp,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                notes,
                hpCalculationMethod,
                hasPendingLevelChoices,
                classProgressions,
                Collections.emptyList(),
                createdAt,
                updatedAt
        );
    }

    public PlayerCharacter(
            UUID id,
            String name,
            int level,
            int hitPointsCurrent,
            int hitPointsMax,
            int hitPointsTemp,
            int armorClass,
            int initiativeBonus,
            int speed,
            int proficiencyBonus,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            Instant createdAt,
            Instant updatedAt
    ) {
        this(
                id,
                name,
                level,
                hitPointsCurrent,
                hitPointsMax,
                hitPointsTemp,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                "",
                HpCalculationMethod.MAX,
                level > 1,
                Collections.emptyList(),
                Collections.emptyList(),
                createdAt,
                updatedAt
        );
    }

    public static PlayerCharacter createNew(
            String name,
            int level,
            int hitPointsMax,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores,
            String notes,
            HpCalculationMethod hpCalculationMethod,
            List<ClassProgression> classProgressions,
            List<String> skillProficiencies
    ) {
        if (abilityScores == null) {
            throw new CharacterValidationException("AbilityScores cannot be null.");
        }

        int proficiencyBonus = calculateProficiencyBonus(level);
        int dexModifier = abilityScores.getDexterityModifier();
        int armorClass = BASE_ARMOR_CLASS + dexModifier;
        int initiativeBonus = dexModifier;
        int speed = DEFAULT_SPEED;
        Instant now = Instant.now();
        boolean hasPendingChoices = level > 1;

        return new PlayerCharacter(
                UUID.randomUUID(),
                name,
                level,
                hitPointsMax,
                hitPointsMax,
                0,
                armorClass,
                initiativeBonus,
                speed,
                proficiencyBonus,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                notes != null ? notes : "",
                hpCalculationMethod != null ? hpCalculationMethod : HpCalculationMethod.MAX,
                hasPendingChoices,
                classProgressions,
                skillProficiencies != null ? skillProficiencies : Collections.emptyList(),
                0,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                now,
                now
        );
    }

    public static PlayerCharacter createNew(
            String name,
            int level,
            int hitPointsMax,
            Ruleset ruleset,
            AbilityScoreMethod abilityScoreMethod,
            UUID lineageId,
            UUID backgroundId,
            UUID userId,
            AbilityScores abilityScores
    ) {
        return createNew(
                name,
                level,
                hitPointsMax,
                ruleset,
                abilityScoreMethod,
                lineageId,
                backgroundId,
                userId,
                abilityScores,
                "",
                HpCalculationMethod.MAX,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public PlayerCharacter updateSheet(
            String name,
            int hitPointsCurrent,
            int hitPointsTemp,
            String notes
    ) {
        validateName(name);
        validateHitPoints(hitPointsCurrent, this.hitPointsMax, hitPointsTemp);

        return new PlayerCharacter(
                this.id,
                name,
                this.level,
                hitPointsCurrent,
                this.hitPointsMax,
                hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                notes != null ? notes : "",
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints,
                this.campaignId,
                this.items,
                this.feats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter addExperience(int amount) {
        if (this.campaignId != null) {
            throw new CharacterValidationException("Cannot manually add XP when character is assigned to a campaign.");
        }
        if (amount <= 0) {
            throw new CharacterValidationException("XP amount must be greater than zero. Provided: " + amount);
        }
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                this.hitPointsCurrent,
                this.hitPointsMax,
                this.hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                this.notes,
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints + amount,
                this.campaignId,
                this.items,
                this.feats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter withLevelUp(
            int newTotalLevel,
            int newHpMax,
            AbilityScores newScores,
            List<ClassProgression> newProgressions,
            List<CharacterFeat> newFeats,
            int newXp,
            int newArmorClass
    ) {
        validateLevel(newTotalLevel);
        int newProficiency = calculateProficiencyBonus(newTotalLevel);
        int hpGain = newHpMax - this.hitPointsMax;
        int newCurrentHp = this.hitPointsCurrent + Math.max(0, hpGain);

        return new PlayerCharacter(
                this.id,
                this.name,
                newTotalLevel,
                newCurrentHp,
                newHpMax,
                this.hitPointsTemp,
                newArmorClass,
                newScores.getDexterityModifier(),
                this.speed,
                newProficiency,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                newScores,
                this.notes,
                this.hpCalculationMethod,
                false,
                newProgressions,
                this.skillProficiencies,
                Math.max(this.experiencePoints, newXp),
                this.campaignId,
                this.items,
                newFeats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter withArmorClass(int newArmorClass) {
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                this.hitPointsCurrent,
                this.hitPointsMax,
                this.hitPointsTemp,
                newArmorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                this.notes,
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints,
                this.campaignId,
                this.items,
                this.feats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter withItems(List<CharacterItem> newItems) {
        long attunedCount = newItems.stream().filter(CharacterItem::isAttuned).count();
        if (attunedCount > MAX_ATTUNED_ITEMS) {
            throw new CharacterValidationException("Cannot attune more than " + MAX_ATTUNED_ITEMS + " items. Attempted: " + attunedCount);
        }
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                this.hitPointsCurrent,
                this.hitPointsMax,
                this.hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                this.notes,
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints,
                this.campaignId,
                newItems,
                this.feats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter withFeats(List<CharacterFeat> newFeats) {
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                this.hitPointsCurrent,
                this.hitPointsMax,
                this.hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                this.notes,
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints,
                this.campaignId,
                this.items,
                newFeats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter withCurrentHp(int currentHp) {
        validateHitPoints(currentHp, this.hitPointsMax, this.hitPointsTemp);
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                currentHp,
                this.hitPointsMax,
                this.hitPointsTemp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                this.notes,
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints,
                this.campaignId,
                this.items,
                this.feats,
                this.createdAt,
                Instant.now()
        );
    }

    public PlayerCharacter withTempHp(int tempHp) {
        validateHitPoints(this.hitPointsCurrent, this.hitPointsMax, tempHp);
        return new PlayerCharacter(
                this.id,
                this.name,
                this.level,
                this.hitPointsCurrent,
                this.hitPointsMax,
                tempHp,
                this.armorClass,
                this.initiativeBonus,
                this.speed,
                this.proficiencyBonus,
                this.ruleset,
                this.abilityScoreMethod,
                this.lineageId,
                this.backgroundId,
                this.userId,
                this.abilityScores,
                this.notes,
                this.hpCalculationMethod,
                this.hasPendingLevelChoices,
                this.classProgressions,
                this.skillProficiencies,
                this.experiencePoints,
                this.campaignId,
                this.items,
                this.feats,
                this.createdAt,
                Instant.now()
        );
    }

    public static int calculateProficiencyBonus(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new CharacterValidationException("Level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + ". Provided: " + level);
        }
        return (level - 1) / 4 + 2;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CharacterValidationException("Character name cannot be null or empty.");
        }
        if (name.trim().length() > 100) {
            throw new CharacterValidationException("Character name length cannot exceed 100 characters.");
        }
    }

    private void validateLevel(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new CharacterValidationException("Level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + ". Provided: " + level);
        }
    }

    private void validateHitPoints(int current, int max, int temp) {
        if (max <= 0) {
            throw new CharacterValidationException("Max hit points must be greater than zero. Provided: " + max);
        }
        if (current < 0 || current > max) {
            throw new CharacterValidationException("Current hit points must be between 0 and max HP (" + max + "). Provided: " + current);
        }
        if (temp < 0) {
            throw new CharacterValidationException("Temporary hit points cannot be negative. Provided: " + temp);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHitPointsCurrent() {
        return hitPointsCurrent;
    }

    public int getHitPointsMax() {
        return hitPointsMax;
    }

    public int getHitPointsTemp() {
        return hitPointsTemp;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public int getInitiativeBonus() {
        return initiativeBonus;
    }

    public int getSpeed() {
        return speed;
    }

    public int getProficiencyBonus() {
        return proficiencyBonus;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public AbilityScoreMethod getAbilityScoreMethod() {
        return abilityScoreMethod;
    }

    public UUID getLineageId() {
        return lineageId;
    }

    public UUID getBackgroundId() {
        return backgroundId;
    }

    public UUID getUserId() {
        return userId;
    }

    public AbilityScores getAbilityScores() {
        return abilityScores;
    }

    @Deprecated
    public String getNotes() {
        return notes;
    }

    public HpCalculationMethod getHpCalculationMethod() {
        return hpCalculationMethod;
    }

    public boolean isHasPendingLevelChoices() {
        return hasPendingLevelChoices;
    }

    public List<ClassProgression> getClassProgressions() {
        return classProgressions;
    }

    public List<String> getSkillProficiencies() {
        return skillProficiencies;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public UUID getCampaignId() {
        return campaignId;
    }

    public List<CharacterItem> getItems() {
        return items;
    }

    public List<CharacterFeat> getFeats() {
        return feats;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerCharacter that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PlayerCharacter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", HP=" + hitPointsCurrent + "/" + hitPointsMax +
                ", AC=" + armorClass +
                ", XP=" + experiencePoints +
                ", ruleset=" + ruleset +
                '}';
    }
}
