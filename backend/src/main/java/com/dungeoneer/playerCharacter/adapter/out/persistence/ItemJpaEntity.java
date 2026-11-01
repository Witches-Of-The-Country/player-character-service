package com.dungeoneer.playerCharacter.adapter.out.persistence;

import com.dungeoneer.playerCharacter.domain.model.ArmorCategory;
import com.dungeoneer.playerCharacter.domain.model.ArmorMechanics;
import com.dungeoneer.playerCharacter.domain.model.ConsumableEffectType;
import com.dungeoneer.playerCharacter.domain.model.ConsumableMechanics;
import com.dungeoneer.playerCharacter.domain.model.DamageRoll;
import com.dungeoneer.playerCharacter.domain.model.DamageType;
import com.dungeoneer.playerCharacter.domain.model.EmptyItemMechanics;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.ItemMechanics;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.WeaponMechanics;
import com.dungeoneer.playerCharacter.domain.model.WeaponProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "items", schema = "player_character")
public class ItemJpaEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruleset", length = 20, nullable = false)
    private Ruleset ruleset;

    @Column(name = "slug", length = 150, nullable = false)
    private String slug;

    @Column(name = "name_en", length = 150, nullable = false)
    private String nameEn;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "item_category", length = 30, nullable = false)
    private String itemCategory;

    @Column(name = "item_type", length = 50, nullable = false)
    private String itemType;

    @Column(name = "rarity", length = 30, nullable = false)
    private String rarity = "NONE";

    @Column(name = "attunement", nullable = false)
    private boolean attunement = false;

    @Column(name = "cost", length = 50)
    private String cost;

    @Column(name = "weight", length = 30)
    private String weight;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "source", length = 20, nullable = false)
    private String source = "SRD";

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "mechanical_properties", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String mechanicalProperties = "{}";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ItemJpaEntity() {
    }

    public ItemJpaEntity(
            UUID id,
            Ruleset ruleset,
            String slug,
            String nameEn,
            String name,
            String itemCategory,
            String itemType,
            String rarity,
            boolean attunement,
            String cost,
            String weight,
            String description,
            String source,
            UUID ownerId,
            String mechanicalProperties
    ) {
        this.id = id;
        this.ruleset = ruleset;
        this.slug = slug;
        this.nameEn = nameEn;
        this.name = name;
        this.itemCategory = itemCategory;
        this.itemType = itemType;
        this.rarity = rarity != null ? rarity : "NONE";
        this.attunement = attunement;
        this.cost = cost;
        this.weight = weight;
        this.description = description;
        this.source = source != null ? source : "SRD";
        this.ownerId = ownerId;
        this.mechanicalProperties = mechanicalProperties != null ? mechanicalProperties : "{}";
    }

    public static ItemJpaEntity fromDomain(Item item) {
        String json = serializeMechanics(item.getMechanicalProperties());
        return new ItemJpaEntity(
                item.getId(),
                item.getRuleset(),
                item.getSlug(),
                item.getNameEn(),
                item.getName(),
                item.getItemCategory(),
                item.getItemType(),
                item.getRarity(),
                item.isAttunement(),
                item.getCost(),
                item.getWeight(),
                item.getDescription(),
                item.getSource(),
                item.getOwnerId(),
                json
        );
    }

    public Item toDomain() {
        ItemMechanics mechanics = deserializeMechanics(this.mechanicalProperties);
        return new Item(
                this.id,
                this.ruleset,
                this.slug,
                this.nameEn,
                this.name,
                this.itemCategory,
                this.itemType,
                this.rarity,
                this.attunement,
                this.cost,
                this.weight,
                this.description,
                this.source,
                this.ownerId,
                mechanics
        );
    }

    public static String serializeMechanics(ItemMechanics mechanics) {
        if (mechanics == null || mechanics instanceof EmptyItemMechanics) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(mechanics);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public static ItemMechanics deserializeMechanics(String json) {
        if (json == null || json.isBlank() || json.trim().equals("{}")) {
            return new EmptyItemMechanics();
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json);
            if (!node.has("type")) {
                return new EmptyItemMechanics();
            }
            String type = node.get("type").asText();
            if ("WEAPON".equalsIgnoreCase(type)) {
                List<DamageRoll> rolls = new ArrayList<>();
                if (node.has("damageRolls") && node.get("damageRolls").isArray()) {
                    for (JsonNode rollNode : node.get("damageRolls")) {
                        String formula = rollNode.has("diceFormula") ? rollNode.get("diceFormula").asText() : "1d4";
                        String dTypeStr = rollNode.has("damageType") ? rollNode.get("damageType").asText() : "SLASHING";
                        DamageType damageType;
                        try {
                            damageType = DamageType.valueOf(dTypeStr.toUpperCase());
                        } catch (Exception ex) {
                            damageType = DamageType.SLASHING;
                        }
                        rolls.add(new DamageRoll(formula, damageType));
                    }
                }
                if (rolls.isEmpty()) {
                    rolls.add(new DamageRoll("1d4", DamageType.BLUDGEONING));
                }
                String versatile = node.has("versatileDice") && !node.get("versatileDice").isNull()
                        ? node.get("versatileDice").asText()
                        : null;
                int magic = node.has("magicBonus") ? node.get("magicBonus").asInt(0) : 0;
                List<WeaponProperty> props = new ArrayList<>();
                if (node.has("properties") && node.get("properties").isArray()) {
                    for (JsonNode pNode : node.get("properties")) {
                        try {
                            props.add(WeaponProperty.valueOf(pNode.asText().toUpperCase()));
                        } catch (Exception ignored) {
                        }
                    }
                }
                return new WeaponMechanics(rolls, versatile, magic, props);
            } else if ("ARMOR".equalsIgnoreCase(type)) {
                String catStr = node.has("armorCategory") ? node.get("armorCategory").asText() : "LIGHT";
                ArmorCategory category;
                try {
                    category = ArmorCategory.valueOf(catStr.toUpperCase());
                } catch (Exception ex) {
                    category = ArmorCategory.LIGHT;
                }
                int baseAc = node.has("baseAc") ? node.get("baseAc").asInt(10) : 10;
                int magic = node.has("magicBonus") ? node.get("magicBonus").asInt(0) : 0;
                int req = node.has("strengthRequirement") ? node.get("strengthRequirement").asInt(0) : 0;
                boolean stealth = node.has("stealthDisadvantage") && node.get("stealthDisadvantage").asBoolean(false);
                return new ArmorMechanics(category, baseAc, magic, req, stealth);
            } else if ("CONSUMABLE".equalsIgnoreCase(type)) {
                String effStr = node.has("effectType") ? node.get("effectType").asText() : "BUFF_UTILITY";
                ConsumableEffectType effectType;
                try {
                    effectType = ConsumableEffectType.valueOf(effStr.toUpperCase());
                } catch (Exception ex) {
                    effectType = ConsumableEffectType.BUFF_UTILITY;
                }
                String formula = node.has("diceFormula") && !node.get("diceFormula").isNull()
                        ? node.get("diceFormula").asText()
                        : null;
                String desc = node.has("effectDescription") ? node.get("effectDescription").asText() : "";
                return new ConsumableMechanics(effectType, formula, desc);
            }
        } catch (Exception e) {
            // fallback
        }
        return new EmptyItemMechanics();
    }

    public UUID getId() {
        return id;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public String getSlug() {
        return slug;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getName() {
        return name;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemType() {
        return itemType;
    }

    public String getRarity() {
        return rarity;
    }

    public boolean isAttunement() {
        return attunement;
    }

    public String getCost() {
        return cost;
    }

    public String getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getMechanicalProperties() {
        return mechanicalProperties;
    }

    public void setMechanicalProperties(String mechanicalProperties) {
        this.mechanicalProperties = mechanicalProperties;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
