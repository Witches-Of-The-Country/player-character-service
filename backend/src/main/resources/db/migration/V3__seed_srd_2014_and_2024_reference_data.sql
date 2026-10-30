-- Migration V3: Seed authoritative SRD 2014 and 2024 Reference Data

-- ============================================================================
-- 1. ARCHETYPES (13 Classes x 2 Rulesets = 26 records)
-- ============================================================================

-- SRD 2014 Classes
INSERT INTO player_character.archetypes (id, name, hit_dice, primary_ability, saving_throw_proficiencies, armor_proficiencies, weapon_proficiencies, spellcasting_type, spellcasting_ability, ruleset, source)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'Barbarian', 'd12', 'STRENGTH', ARRAY['STRENGTH', 'CONSTITUTION'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'NONE', NULL, 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000002', 'Bard', 'd8', 'CHARISMA', ARRAY['DEXTERITY', 'CHARISMA'], ARRAY['LIGHT'], ARRAY['SIMPLE', 'HAND_CROSSBOW', 'LONGSWORD', 'RAPIER', 'SHORTSWORD'], 'FULL', 'CHARISMA', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000003', 'Cleric', 'd8', 'WISDOM', ARRAY['WISDOM', 'CHARISMA'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE'], 'FULL', 'WISDOM', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000004', 'Druid', 'd8', 'WISDOM', ARRAY['INTELLIGENCE', 'WISDOM'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['CLUBS', 'DAGGERS', 'DARTS', 'JAVELINS', 'MACES', 'QUARTERSTAFFS', 'SCIMITARS', 'SICKLES', 'SLINGS', 'SPEARS'], 'FULL', 'WISDOM', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000005', 'Fighter', 'd10', 'STRENGTH', ARRAY['STRENGTH', 'CONSTITUTION'], ARRAY['ALL_ARMOR', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'NONE', NULL, 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000006', 'Monk', 'd8', 'DEXTERITY', ARRAY['STRENGTH', 'DEXTERITY'], ARRAY[]::varchar[], ARRAY['SIMPLE', 'SHORTSWORD'], 'NONE', NULL, 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000007', 'Paladin', 'd10', 'STRENGTH', ARRAY['WISDOM', 'CHARISMA'], ARRAY['ALL_ARMOR', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'HALF', 'CHARISMA', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000008', 'Ranger', 'd10', 'DEXTERITY', ARRAY['STRENGTH', 'DEXTERITY'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'HALF', 'WISDOM', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000009', 'Rogue', 'd8', 'DEXTERITY', ARRAY['DEXTERITY', 'INTELLIGENCE'], ARRAY['LIGHT'], ARRAY['SIMPLE', 'HAND_CROSSBOW', 'LONGSWORD', 'RAPIER', 'SHORTSWORD'], 'NONE', NULL, 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000010', 'Sorcerer', 'd6', 'CHARISMA', ARRAY['CONSTITUTION', 'CHARISMA'], ARRAY[]::varchar[], ARRAY['DAGGERS', 'DARTS', 'SLINGS', 'QUARTERSTAFFS', 'LIGHT_CROSSBOWS'], 'FULL', 'CHARISMA', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000011', 'Warlock', 'd8', 'CHARISMA', ARRAY['WISDOM', 'CHARISMA'], ARRAY['LIGHT'], ARRAY['SIMPLE'], 'PACT', 'CHARISMA', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000012', 'Wizard', 'd6', 'INTELLIGENCE', ARRAY['INTELLIGENCE', 'WISDOM'], ARRAY[]::varchar[], ARRAY['DAGGERS', 'DARTS', 'SLINGS', 'QUARTERSTAFFS', 'LIGHT_CROSSBOWS'], 'FULL', 'INTELLIGENCE', 'SRD_2014', 'SRD'),
    ('10000000-0000-0000-0000-000000000013', 'Artificer', 'd8', 'INTELLIGENCE', ARRAY['CONSTITUTION', 'INTELLIGENCE'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE'], 'HALF', 'INTELLIGENCE', 'SRD_2014', 'SRD')
ON CONFLICT (name, ruleset, source) DO NOTHING;

-- SRD 2024 Classes
INSERT INTO player_character.archetypes (id, name, hit_dice, primary_ability, saving_throw_proficiencies, armor_proficiencies, weapon_proficiencies, spellcasting_type, spellcasting_ability, ruleset, source)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'Barbarian', 'd12', 'STRENGTH', ARRAY['STRENGTH', 'CONSTITUTION'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'NONE', NULL, 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000002', 'Bard', 'd8', 'CHARISMA', ARRAY['DEXTERITY', 'CHARISMA'], ARRAY['LIGHT'], ARRAY['SIMPLE', 'HAND_CROSSBOW', 'LONGSWORD', 'RAPIER', 'SHORTSWORD'], 'FULL', 'CHARISMA', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000003', 'Cleric', 'd8', 'WISDOM', ARRAY['WISDOM', 'CHARISMA'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE'], 'FULL', 'WISDOM', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000004', 'Druid', 'd8', 'WISDOM', ARRAY['INTELLIGENCE', 'WISDOM'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['CLUBS', 'DAGGERS', 'DARTS', 'JAVELINS', 'MACES', 'QUARTERSTAFFS', 'SCIMITARS', 'SICKLES', 'SLINGS', 'SPEARS'], 'FULL', 'WISDOM', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000005', 'Fighter', 'd10', 'STRENGTH', ARRAY['STRENGTH', 'CONSTITUTION'], ARRAY['ALL_ARMOR', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'NONE', NULL, 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000006', 'Monk', 'd8', 'DEXTERITY', ARRAY['STRENGTH', 'DEXTERITY'], ARRAY[]::varchar[], ARRAY['SIMPLE', 'MARTIAL'], 'NONE', NULL, 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000007', 'Paladin', 'd10', 'STRENGTH', ARRAY['WISDOM', 'CHARISMA'], ARRAY['ALL_ARMOR', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'HALF', 'CHARISMA', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000008', 'Ranger', 'd10', 'DEXTERITY', ARRAY['STRENGTH', 'DEXTERITY'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE', 'MARTIAL'], 'HALF', 'WISDOM', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000009', 'Rogue', 'd8', 'DEXTERITY', ARRAY['DEXTERITY', 'INTELLIGENCE'], ARRAY['LIGHT'], ARRAY['SIMPLE', 'MARTIAL'], 'NONE', NULL, 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000010', 'Sorcerer', 'd6', 'CHARISMA', ARRAY['CONSTITUTION', 'CHARISMA'], ARRAY[]::varchar[], ARRAY['DAGGERS', 'DARTS', 'SLINGS', 'QUARTERSTAFFS', 'LIGHT_CROSSBOWS'], 'FULL', 'CHARISMA', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000011', 'Warlock', 'd8', 'CHARISMA', ARRAY['WISDOM', 'CHARISMA'], ARRAY['LIGHT'], ARRAY['SIMPLE'], 'PACT', 'CHARISMA', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000012', 'Wizard', 'd6', 'INTELLIGENCE', ARRAY['INTELLIGENCE', 'WISDOM'], ARRAY[]::varchar[], ARRAY['DAGGERS', 'DARTS', 'SLINGS', 'QUARTERSTAFFS', 'LIGHT_CROSSBOWS'], 'FULL', 'INTELLIGENCE', 'SRD_2024', 'SRD'),
    ('20000000-0000-0000-0000-000000000013', 'Artificer', 'd8', 'INTELLIGENCE', ARRAY['CONSTITUTION', 'INTELLIGENCE'], ARRAY['LIGHT', 'MEDIUM', 'SHIELDS'], ARRAY['SIMPLE'], 'HALF', 'INTELLIGENCE', 'SRD_2024', 'SRD')
ON CONFLICT (name, ruleset, source) DO NOTHING;

-- ============================================================================
-- 2. LINEAGES (9 Lineages x 2 Rulesets = 18 records)
-- ============================================================================

-- SRD 2014 Lineages (Fixed ASI JSONB)
INSERT INTO player_character.lineages (id, name, description, speed, size, ability_bonuses, traits_summary, ruleset, source)
VALUES
    ('11000000-0000-0000-0000-000000000001', 'Human', 'Adaptable and ambitious, humans are innovators and achievers.', 30, 'MEDIUM', '{"STRENGTH": 1, "DEXTERITY": 1, "CONSTITUTION": 1, "INTELLIGENCE": 1, "WISDOM": 1, "CHARISMA": 1}'::jsonb, ARRAY['Extra Language'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000002', 'Elf', 'Magical people of otherworldly grace, living in the world but not entirely part of it.', 30, 'MEDIUM', '{"DEXTERITY": 2}'::jsonb, ARRAY['Darkvision', 'Keen Senses', 'Fey Ancestry', 'Trance'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000003', 'Dwarf', 'Bold and hardy, dwarves are known as skilled warriors, miners, and workers of stone and metal.', 25, 'MEDIUM', '{"CONSTITUTION": 2}'::jsonb, ARRAY['Darkvision', 'Dwarven Resilience', 'Dwarven Combat Training', 'Stonecunning'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000004', 'Halfling', 'The comforts of home are the goals of most halflings: a place to settle in peace and quiet.', 25, 'SMALL', '{"DEXTERITY": 2}'::jsonb, ARRAY['Lucky', 'Brave', 'Halfling Nimbleness'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000005', 'Gnome', 'A constant hum of busy activity pervades the warrens and neighborhoods where gnomes form their close-knit communities.', 25, 'SMALL', '{"INTELLIGENCE": 2}'::jsonb, ARRAY['Darkvision', 'Gnome Cunning'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000006', 'Half-Elf', 'Walking in two worlds but truly belonging to neither, half-elves combine what some say are the best qualities of their elf and human parents.', 30, 'MEDIUM', '{"CHARISMA": 2}'::jsonb, ARRAY['Darkvision', 'Fey Ancestry', 'Skill Versatility'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000007', 'Half-Orc', 'Half-orcs gray coloration, sloping foreheads, jutting jaws, and prominent canine teeth immediately identify them as other.', 30, 'MEDIUM', '{"STRENGTH": 2, "CONSTITUTION": 1}'::jsonb, ARRAY['Darkvision', 'Menacing', 'Relentless Endurance', 'Savage Attacks'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000008', 'Tiefling', 'To be greeted with stares and whispers, to suffer violence and insult on the street, to see mistrust and fear in every eye: this is the lot of the tiefling.', 30, 'MEDIUM', '{"CHARISMA": 2, "INTELLIGENCE": 1}'::jsonb, ARRAY['Darkvision', 'Hellish Resistance', 'Infernal Legacy'], 'SRD_2014', 'SRD'),
    ('11000000-0000-0000-0000-000000000009', 'Dragonborn', 'Born of dragons, as their name proclaims, the dragonborn walk proudly through a world that greets them with fearful incomprehension.', 30, 'MEDIUM', '{"STRENGTH": 2, "CHARISMA": 1}'::jsonb, ARRAY['Draconic Ancestry', 'Breath Weapon', 'Damage Resistance'], 'SRD_2014', 'SRD')
ON CONFLICT (name, ruleset, source) DO NOTHING;

-- SRD 2024 Lineages (Cosmetic Lineages with traits, ASI moved to Background)
INSERT INTO player_character.lineages (id, name, description, speed, size, ability_bonuses, traits_summary, ruleset, source)
VALUES
    ('21000000-0000-0000-0000-000000000001', 'Human', 'Found throughout the multiverse, humans display extraordinary adaptability and resilience.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Resourceful', 'Skillful', 'Versatile'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000002', 'Elf', 'Tracing ancestry to the Feywild, elves possess magical grace and centuries-long lifespan.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Darkvision', 'Elven Lineage', 'Fey Ancestry', 'Keen Senses', 'Trance'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000003', 'Dwarf', 'Raised from bedrock and forge fire, dwarves embody endurance and mastery of stone and metal.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Darkvision', 'Dwarven Resilience', 'Dwarven Toughness', 'Stonecunning'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000004', 'Halfling', 'Small in stature and quick of wit, halflings survive adversity through fellowship and resourcefulness.', 30, 'SMALL', '{}'::jsonb, ARRAY['Brave', 'Halfling Nimbleness', 'Luck', 'Naturally Stealthy'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000005', 'Gnome', 'Filled with unbounded enthusiasm, gnomes express curiosity through invention and wonder.', 30, 'SMALL', '{}'::jsonb, ARRAY['Darkvision', 'Gnomish Cunning', 'Gnomish Lineage'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000006', 'Half-Elf', 'Versatile lineage blending human ambition with elven heritage.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Darkvision', 'Fey Ancestry', 'Versatile Lineage'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000007', 'Half-Orc', 'Resilient warriors enduring through sheer will, possessing unwavering determination.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Darkvision', 'Relentless Endurance', 'Savage Attacks'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000008', 'Tiefling', 'Touched by otherworldly fiendish planes, tieflings wield fiery resilience and darkvision.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Darkvision', 'Fiendish Legacy', 'Otherworldly Presence'], 'SRD_2024', 'SRD'),
    ('21000000-0000-0000-0000-000000000009', 'Dragonborn', 'Carrying the legacy of ancient dragons, dragonborn exhale elemental power and take to the skies.', 30, 'MEDIUM', '{}'::jsonb, ARRAY['Draconic Flight', 'Breath Weapon', 'Damage Resistance', 'Darkvision'], 'SRD_2024', 'SRD')
ON CONFLICT (name, ruleset, source) DO NOTHING;

-- ============================================================================
-- 3. BACKGROUNDS (6 Backgrounds x 2 Rulesets = 12 records)
-- ============================================================================

-- SRD 2014 Backgrounds (No origin feats, no ASI choices)
INSERT INTO player_character.backgrounds (id, name, description, skill_proficiencies, tool_proficiencies, languages, allowed_ability_bonuses, origin_feat_name, origin_feat_description, ruleset, source)
VALUES
    ('12000000-0000-0000-0000-000000000001', 'Acolyte', 'You have spent your life in the service of a temple to a specific god or pantheon of gods.', ARRAY['INSIGHT', 'RELIGION'], ARRAY[]::varchar[], ARRAY['TWO_OF_CHOICE'], ARRAY[]::varchar[], NULL, NULL, 'SRD_2014', 'SRD'),
    ('12000000-0000-0000-0000-000000000002', 'Criminal', 'You are an experienced criminal with a history of breaking the law and surviving in the shadows.', ARRAY['DECEPTION', 'STEALTH'], ARRAY['THIEVES_TOOLS', 'GAMING_SET'], ARRAY[]::varchar[], ARRAY[]::varchar[], NULL, NULL, 'SRD_2014', 'SRD'),
    ('12000000-0000-0000-0000-000000000003', 'Folk Hero', 'You come from a humble social rank, but you are destined for so much more.', ARRAY['ANIMAL_HANDLING', 'SURVIVAL'], ARRAY['ARTISANS_TOOLS', 'VEHICLES_LAND'], ARRAY[]::varchar[], ARRAY[]::varchar[], NULL, NULL, 'SRD_2014', 'SRD'),
    ('12000000-0000-0000-0000-000000000004', 'Noble', 'You understand wealth, power, and privilege. You carry a noble title, and your family owns land.', ARRAY['HISTORY', 'PERSUASION'], ARRAY['GAMING_SET'], ARRAY['ONE_OF_CHOICE'], ARRAY[]::varchar[], NULL, NULL, 'SRD_2014', 'SRD'),
    ('12000000-0000-0000-0000-000000000005', 'Sage', 'You spent years learning the lore of the multiverse, scouring manuscripts and studying scrolls.', ARRAY['ARCANA', 'HISTORY'], ARRAY[]::varchar[], ARRAY['TWO_OF_CHOICE'], ARRAY[]::varchar[], NULL, NULL, 'SRD_2014', 'SRD'),
    ('12000000-0000-0000-0000-000000000006', 'Soldier', 'War has been your life for as long as you can remember. You trained as a youth in military tactics.', ARRAY['ATHLETICS', 'INTIMIDATION'], ARRAY['GAMING_SET', 'VEHICLES_LAND'], ARRAY[]::varchar[], ARRAY[]::varchar[], NULL, NULL, 'SRD_2014', 'SRD')
ON CONFLICT (name, ruleset, source) DO NOTHING;

-- SRD 2024 Backgrounds (Grants 3 Allowed ASI Abilities + Origin Feat metadata)
INSERT INTO player_character.backgrounds (id, name, description, skill_proficiencies, tool_proficiencies, languages, allowed_ability_bonuses, origin_feat_name, origin_feat_description, ruleset, source)
VALUES
    ('22000000-0000-0000-0000-000000000001', 'Acolyte', 'Devoted to service in a holy sanctuary, skilled in divine rites and rituals.', ARRAY['INSIGHT', 'RELIGION'], ARRAY['CALLIGRAPHERS_SUPPLIES'], ARRAY[]::varchar[], ARRAY['INTELLIGENCE', 'WISDOM', 'CHARISMA'], 'Magic Initiate (Cleric)', 'Gain two cleric cantrips and one 1st-level cleric spell.', 'SRD_2024', 'SRD'),
    ('22000000-0000-0000-0000-000000000002', 'Criminal', 'Surviving on illicit deeds, lockpicking, and connections in the criminal underworld.', ARRAY['DECEPTION', 'STEALTH'], ARRAY['THIEVES_TOOLS'], ARRAY[]::varchar[], ARRAY['DEXTERITY', 'CONSTITUTION', 'INTELLIGENCE'], 'Alert', 'Gain initiative proficiency bonus and swap initiative rolls.', 'SRD_2024', 'SRD'),
    ('22000000-0000-0000-0000-000000000003', 'Folk Hero', 'A champion of commoners who stood against tyranny, gifted with rugged grit.', ARRAY['ANIMAL_HANDLING', 'SURVIVAL'], ARRAY['ARTISANS_TOOLS'], ARRAY[]::varchar[], ARRAY['STRENGTH', 'CONSTITUTION', 'WISDOM'], 'Tough', 'Hit point maximum increases by 2 per level.', 'SRD_2024', 'SRD'),
    ('22000000-0000-0000-0000-000000000004', 'Noble', 'Heir to high station and politics, tutored in leadership and fine etiquette.', ARRAY['HISTORY', 'PERSUASION'], ARRAY['GAMING_SET'], ARRAY[]::varchar[], ARRAY['STRENGTH', 'INTELLIGENCE', 'CHARISMA'], 'Skilled', 'Gain proficiency in any combination of three skills or tools.', 'SRD_2024', 'SRD'),
    ('22000000-0000-0000-0000-000000000005', 'Sage', 'Dedicated to archival research, deciphering arcane secrets, and ancient history.', ARRAY['ARCANA', 'HISTORY'], ARRAY['CALLIGRAPHERS_SUPPLIES'], ARRAY[]::varchar[], ARRAY['CONSTITUTION', 'INTELLIGENCE', 'WISDOM'], 'Magic Initiate (Wizard)', 'Gain two wizard cantrips and one 1st-level wizard spell.', 'SRD_2024', 'SRD'),
    ('22000000-0000-0000-0000-000000000006', 'Soldier', 'Trained soldier hardened in battle formations, physical conditioning, and tactical prowess.', ARRAY['ATHLETICS', 'INTIMIDATION'], ARRAY['GAMING_SET'], ARRAY[]::varchar[], ARRAY['STRENGTH', 'DEXTERITY', 'CONSTITUTION'], 'Savage Attacker', 'Roll weapon damage twice and use higher result once per turn.', 'SRD_2024', 'SRD')
ON CONFLICT (name, ruleset, source) DO NOTHING;
