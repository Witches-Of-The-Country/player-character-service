-- Migration V18: Populate mechanical_properties for SRD items and mechanical_effects for SRD feats
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Weapons (SRD 2014 & SRD 2024)
-- Espada Longa / Longsword
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d8", "damageType": "SLASHING"}], "versatileDice": "1d10", "magicBonus": 0, "properties": ["VERSATILE"]}'::jsonb
WHERE slug IN ('longsword-2014', 'longsword-2024');

-- Adaga / Dagger
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d4", "damageType": "PIERCING"}], "magicBonus": 0, "properties": ["FINESSE", "LIGHT", "THROWN"]}'::jsonb
WHERE slug IN ('dagger-2014', 'dagger-2024');

-- Clava / Club
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d4", "damageType": "BLUDGEONING"}], "magicBonus": 0, "properties": ["LIGHT"]}'::jsonb
WHERE slug IN ('club-2014', 'club-2024');

-- Clava grande / Greatclub
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d8", "damageType": "BLUDGEONING"}], "magicBonus": 0, "properties": ["TWO_HANDED"]}'::jsonb
WHERE slug IN ('greatclub-2014', 'greatclub-2024');

-- Machado de Batalha / Battleaxe
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d8", "damageType": "SLASHING"}], "versatileDice": "1d10", "magicBonus": 0, "properties": ["VERSATILE"]}'::jsonb
WHERE slug IN ('battleaxe-2014', 'battleaxe-2024');

-- Mangual / Flail
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d8", "damageType": "BLUDGEONING"}], "magicBonus": 0, "properties": []}'::jsonb
WHERE slug IN ('flail-2014', 'flail-2024');

-- Glaive / Glaive
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d10", "damageType": "SLASHING"}], "magicBonus": 0, "properties": ["HEAVY", "REACH", "TWO_HANDED"]}'::jsonb
WHERE slug IN ('glaive-2014', 'glaive-2024');

-- Arco Curto / Shortbow
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d6", "damageType": "PIERCING"}], "magicBonus": 0, "properties": ["AMMUNITION", "TWO_HANDED"]}'::jsonb
WHERE slug IN ('shortbow-2014', 'shortbow-2024');

-- Arco Longo / Longbow
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d8", "damageType": "PIERCING"}], "magicBonus": 0, "properties": ["AMMUNITION", "HEAVY", "TWO_HANDED"]}'::jsonb
WHERE slug IN ('longbow-2014', 'longbow-2024');

-- Funda / Sling
UPDATE player_character.items
SET mechanical_properties = '{"type": "WEAPON", "damageRolls": [{"diceFormula": "1d4", "damageType": "BLUDGEONING"}], "magicBonus": 0, "properties": ["AMMUNITION"]}'::jsonb
WHERE slug IN ('sling-2014', 'sling-2024');

-- 2. Armors, Robes & Shields (SRD 2014 & SRD 2024)
-- Cota de Malha / Chain Mail (Heavy Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "HEAVY", "baseAc": 16, "magicBonus": 0, "strengthRequirement": 13, "stealthDisadvantage": true}'::jsonb
WHERE slug IN ('chain-mail-2014', 'chain-mail-2024');

-- Armadura de Placas / Plate Armor (Heavy Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "HEAVY", "baseAc": 18, "magicBonus": 0, "strengthRequirement": 15, "stealthDisadvantage": true}'::jsonb
WHERE slug IN ('plate-armor-2014', 'plate-armor-2024');

-- Armadura de Couro / Leather Armor (Light Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "LIGHT", "baseAc": 11, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": false}'::jsonb
WHERE slug IN ('leather-armor-2014', 'leather-armor-2024');

-- Couro Batido / Studded Leather Armor (Light Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "LIGHT", "baseAc": 12, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": false}'::jsonb
WHERE slug IN ('studded-leather-armor-2014', 'studded-leather-armor-2024');

-- Camisão de Cota de Malha / Chain Shirt (Medium Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "MEDIUM", "baseAc": 13, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": false}'::jsonb
WHERE slug IN ('chain-shirt-2014', 'chain-shirt-2024');

-- Peitoral de Aço / Breastplate (Medium Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "MEDIUM", "baseAc": 14, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": false}'::jsonb
WHERE slug IN ('breastplate-2014', 'breastplate-2024');

-- Meia-Armadura / Half Plate (Medium Armor)
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "MEDIUM", "baseAc": 15, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": true}'::jsonb
WHERE slug IN ('half-plate-2014', 'half-plate-2024');

-- Escudo / Shield
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "SHIELD", "baseAc": 2, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": false}'::jsonb
WHERE slug IN ('shield-2014', 'shield-2024');

-- Manto / Robes / Robe of the Archmagi
UPDATE player_character.items
SET mechanical_properties = '{"type": "ARMOR", "armorCategory": "ROBE", "baseAc": 10, "magicBonus": 0, "strengthRequirement": 0, "stealthDisadvantage": false}'::jsonb
WHERE slug LIKE '%robe%' OR slug LIKE '%veste%';

-- 3. Consumables
-- Poção de Cura / Potion of Healing
UPDATE player_character.items
SET mechanical_properties = '{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "2d4+2", "effectDescription": "Recupera 2d4 + 2 pontos de vida imediatamente ao ser consumida."}'::jsonb
WHERE slug IN ('potion-of-healing-2014', 'potion-of-healing-2024');

-- 4. Update Feats with feat_type and mechanical_effects
-- Vigoroso / Tough (Origin/General Feat)
UPDATE player_character.feats
SET feat_type = 'ORIGIN',
    mechanical_effects = '{"hpPerLevelBonus": 2}'::jsonb
WHERE slug IN ('tough-2024', 'tough-2014');

-- Alerta / Alert (Origin Feat)
UPDATE player_character.feats
SET feat_type = 'ORIGIN',
    mechanical_effects = '{"initiativeBonus": 2}'::jsonb
WHERE slug IN ('alert-2024', 'alert-2014');

-- Defensivo / Defense Fighting Style
UPDATE player_character.feats
SET feat_type = 'GENERAL',
    mechanical_effects = '{"acBonus": 1}'::jsonb
WHERE slug IN ('defense-2024', 'defense-2014');

-- Imobilizador / Grappler
UPDATE player_character.feats
SET feat_type = 'GENERAL',
    mechanical_effects = '{"abilityBonuses": {"STR": 1}}'::jsonb
WHERE slug IN ('grappler-2024', 'grappler-2014');

-- Conjurador de Batalha / War Caster
UPDATE player_character.feats
SET feat_type = 'GENERAL',
    mechanical_effects = '{"actionName": "Conjurador de Batalha", "actionType": "SPECIAL"}'::jsonb
WHERE slug IN ('war-caster-2024', 'war-caster-2014');

-- Benções Épicas (Epic Boons)
UPDATE player_character.feats
SET feat_type = 'GENERAL',
    mechanical_effects = '{"abilityBonuses": {"STR": 1}}'::jsonb
WHERE slug = 'boon-of-combat-prowess-2024';

UPDATE player_character.feats
SET feat_type = 'GENERAL',
    mechanical_effects = '{"abilityBonuses": {"DEX": 1}}'::jsonb
WHERE slug = 'boon-of-dimensional-travel-2024';
