-- Migration V29: Fix legacy items, SRD armors, wondrous mechanics, feat JSON keys, and character_feats backfill
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- =====================================================================
-- 1. Fix Missing & Misnamed SRD Armor Mechanics (V13 vs V18)
-- =====================================================================

-- Padded Armor (Acolchoada): Light, AC 11 + DEX, Stealth Disadvantage
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "baseAc": 11, "armorCategory": "LIGHT", "maxDexBonus": null, "strengthRequirement": null, "stealthDisadvantage": true, "magicBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('padded-armor-2014', 'padded-armor-2024');

-- Hide Armor (Gibão de Peles): Medium, AC 12 + DEX (max 2)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "baseAc": 12, "armorCategory": "MEDIUM", "maxDexBonus": 2, "strengthRequirement": null, "stealthDisadvantage": false, "magicBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('hide-armor-2014', 'hide-armor-2024');

-- Scale Mail (Brunea): Medium, AC 14 + DEX (max 2), Stealth Disadvantage
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "baseAc": 14, "armorCategory": "MEDIUM", "maxDexBonus": 2, "strengthRequirement": null, "stealthDisadvantage": true, "magicBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('scale-mail-2014', 'scale-mail-2024');

-- Half Plate (Meia-Armadura / Placa Parcial): Medium, AC 15 + DEX (max 2), Stealth Disadvantage
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "baseAc": 15, "armorCategory": "MEDIUM", "maxDexBonus": 2, "strengthRequirement": null, "stealthDisadvantage": true, "magicBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('half-plate-armor-2014', 'half-plate-armor-2024', 'half-plate-2014', 'half-plate-2024');

-- Ring Mail (Cota de Anéis): Heavy, AC 14, Stealth Disadvantage
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "baseAc": 14, "armorCategory": "HEAVY", "maxDexBonus": 0, "strengthRequirement": null, "stealthDisadvantage": true, "magicBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ring-mail-2014', 'ring-mail-2024');

-- Splint Armor (Cota de Talas): Heavy, AC 17, STR 15, Stealth Disadvantage
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "baseAc": 17, "armorCategory": "HEAVY", "maxDexBonus": 0, "strengthRequirement": 15, "stealthDisadvantage": true, "magicBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('splint-armor-2014', 'splint-armor-2024');

-- =====================================================================
-- 2. Populate Wondrous & Ring Item Mechanics (Ability Overrides, Bonuses, AC & Saves)
-- =====================================================================

-- Gauntlets of Ogre Power (STR = 19)
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"STR": 19}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('gauntlets-of-ogre-power-2014', 'gauntlets-of-ogre-power-2024');

-- Headband of Intellect (INT = 19)
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"INT": 19}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('headband-of-intellect-2014', 'headband-of-intellect-2024');

-- Amulet of Health (CON = 19)
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"CON": 19}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('amulet-of-health-2014', 'amulet-of-health-2024');

-- Belts of Giant Strength (Hill: 21, Stone/Frost: 23, Fire: 25, Cloud: 27, Storm: 29)
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"STR": 21}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('belt-of-giant-strength-hill-2014', 'belt-of-giant-strength-hill-2024', 'belt-of-giant-strength-2014', 'belt-of-giant-strength-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"STR": 23}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('belt-of-giant-strength-stone-2014', 'belt-of-giant-strength-stone-2024', 'belt-of-giant-strength-frost-2014', 'belt-of-giant-strength-frost-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"STR": 25}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('belt-of-giant-strength-fire-2014', 'belt-of-giant-strength-fire-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"STR": 27}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('belt-of-giant-strength-cloud-2014', 'belt-of-giant-strength-cloud-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {"STR": 29}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('belt-of-giant-strength-storm-2014', 'belt-of-giant-strength-storm-2024');

-- Belt of Dwarvenkind (CON +2)
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"CON": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('belt-of-dwarvenkind-2014', 'belt-of-dwarvenkind-2024');

-- Ioun Stones & Manuals / Tomes (+2 to respective ability score)
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"STR": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-strength-2014', 'ioun-stone-of-strength-2024', 'manual-of-gainful-exercise-2014', 'manual-of-gainful-exercise-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"DEX": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-agility-2014', 'ioun-stone-of-agility-2024', 'manual-of-quickness-of-action-2014', 'manual-of-quickness-of-action-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"CON": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-fortitude-2014', 'ioun-stone-of-fortitude-2024', 'manual-of-bodily-health-2014', 'manual-of-bodily-health-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"INT": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-intellect-2014', 'ioun-stone-of-intellect-2024', 'tome-of-clear-thought-2014', 'tome-of-clear-thought-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"WIS": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-insight-2014', 'ioun-stone-of-insight-2024', 'tome-of-understanding-2014', 'tome-of-understanding-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {"CHA": 2}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-leadership-2014', 'ioun-stone-of-leadership-2024', 'tome-of-leadership-and-influence-2014', 'tome-of-leadership-and-influence-2024');

-- Passive AC & Saving Throw Bonus Items
UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {}, "acBonus": 2, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('bracers-of-defense-2014', 'bracers-of-defense-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {}, "acBonus": 1, "savingThrowBonus": 1}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('cloak-of-protection-2014', 'cloak-of-protection-2024', 'ring-of-protection-2014', 'ring-of-protection-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {}, "acBonus": 1, "savingThrowBonus": 0}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('ioun-stone-of-protection-2014', 'ioun-stone-of-protection-2024');

UPDATE player_character.items
SET mechanical_properties = '{"type": "WONDROUS", "abilityBonuses": {}, "abilityOverrides": {}, "acBonus": 0, "savingThrowBonus": 1}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('stone-of-good-luck-luckstone-2014', 'stone-of-good-luck-luckstone-2024', 'robe-of-stars-2014', 'robe-of-stars-2024');

-- =====================================================================
-- 3. Normalize Legacy Feat JSON Keys (maxUses / rechargeOn -> fixedMaxCharges / usesPerRest / restType)
-- =====================================================================

-- Ensure Fighter progression charge features have explicit actionName & actionType
UPDATE player_character.feats
SET mechanical_effects = mechanical_effects || '{"actionName": "Surto de Ação (2 usos)", "actionType": "FREE"}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('fighter-action-surge-2-2014', 'fighter-action-surge-2-2024');

UPDATE player_character.feats
SET mechanical_effects = mechanical_effects || '{"actionName": "Indomável (2 usos)", "actionType": "REACTION"}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('fighter-indomitable-2-2014', 'fighter-indomitable-2-2024');

UPDATE player_character.feats
SET mechanical_effects = mechanical_effects || '{"actionName": "Indomável (3 usos)", "actionType": "REACTION"}'::jsonb,
    updated_at = NOW()
WHERE slug IN ('fighter-indomitable-3-2014', 'fighter-indomitable-3-2024');

-- Normalize all feats using legacy maxUses or rechargeOn
UPDATE player_character.feats
SET mechanical_effects = mechanical_effects || jsonb_build_object(
        'maxChargesType', COALESCE(mechanical_effects->>'maxChargesType', 'FIXED'),
        'fixedMaxCharges', COALESCE(
            NULLIF(mechanical_effects->>'fixedMaxCharges', '')::INT,
            NULLIF(mechanical_effects->>'usesPerRest', '')::INT,
            NULLIF(mechanical_effects->>'maxUses', '')::INT,
            1
        ),
        'usesPerRest', COALESCE(
            NULLIF(mechanical_effects->>'usesPerRest', '')::INT,
            NULLIF(mechanical_effects->>'fixedMaxCharges', '')::INT,
            NULLIF(mechanical_effects->>'maxUses', '')::INT,
            1
        ),
        'restType', CASE
            WHEN mechanical_effects->>'restType' IN ('SHORT', 'LONG') THEN mechanical_effects->>'restType'
            WHEN UPPER(COALESCE(mechanical_effects->>'rechargeOn', '')) LIKE 'SHORT%' THEN 'SHORT'
            ELSE 'LONG'
        END,
        'shortRestRecoveryType', COALESCE(
            mechanical_effects->>'shortRestRecoveryType',
            CASE
                WHEN mechanical_effects->>'restType' = 'SHORT' OR UPPER(COALESCE(mechanical_effects->>'rechargeOn', '')) LIKE 'SHORT%' THEN 'ALL'
                ELSE 'NONE'
            END
        ),
        'longRestRecoveryType', COALESCE(
            mechanical_effects->>'longRestRecoveryType',
            'ALL'
        )
    ),
    updated_at = NOW()
WHERE mechanical_effects IS NOT NULL
  AND (
      mechanical_effects ? 'maxUses'
      OR mechanical_effects ? 'rechargeOn'
      OR (mechanical_effects ? 'usesPerRest' AND NOT (mechanical_effects ? 'fixedMaxCharges'))
  );

-- =====================================================================
-- 4. Backfill player_character.character_feats for Existing Characters
-- =====================================================================

UPDATE player_character.character_feats cf
SET max_charges = COALESCE(
        cf.max_charges,
        NULLIF((f.mechanical_effects->>'fixedMaxCharges'), '')::INT,
        NULLIF((f.mechanical_effects->>'usesPerRest'), '')::INT,
        NULLIF((f.mechanical_effects->>'maxUses'), '')::INT,
        1
    ),
    current_charges = COALESCE(
        cf.current_charges,
        cf.max_charges,
        NULLIF((f.mechanical_effects->>'fixedMaxCharges'), '')::INT,
        NULLIF((f.mechanical_effects->>'usesPerRest'), '')::INT,
        NULLIF((f.mechanical_effects->>'maxUses'), '')::INT,
        1
    ),
    updated_at = NOW()
FROM player_character.feats f
WHERE cf.feat_id = f.id
  AND (cf.max_charges IS NULL OR cf.current_charges IS NULL)
  AND f.mechanical_effects IS NOT NULL
  AND (
      f.mechanical_effects ? 'usesPerRest'
      OR f.mechanical_effects ? 'fixedMaxCharges'
      OR f.mechanical_effects ? 'maxUses'
      OR f.mechanical_effects ? 'maxChargesType'
  );
