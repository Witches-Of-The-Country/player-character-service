-- Migration V28: Adapt legacy feat charges and recovery rules (including Barbarian Rage recovery)

-- 1. Update Barbarian Rage (2024) to recover 1 charge on Short Rest and ALL on Long Rest
UPDATE player_character.feats
SET mechanical_effects = COALESCE(mechanical_effects, '{}'::jsonb) || '{
    "actionName": "Fúria",
    "actionType": "BONUS_ACTION",
    "maxChargesType": "FIXED",
    "fixedMaxCharges": 2,
    "usesPerRest": 2,
    "restType": "LONG",
    "shortRestRecoveryType": "FIXED",
    "shortRestRecoveryFixed": 1,
    "longRestRecoveryType": "ALL"
}'::jsonb
WHERE slug = 'rage-2024';

-- 2. Update Barbarian Rage (2014) to recover ALL charges on Long Rest
UPDATE player_character.feats
SET mechanical_effects = COALESCE(mechanical_effects, '{}'::jsonb) || '{
    "actionName": "Fúria",
    "actionType": "BONUS_ACTION",
    "maxChargesType": "FIXED",
    "fixedMaxCharges": 2,
    "usesPerRest": 2,
    "restType": "LONG",
    "shortRestRecoveryType": "NONE",
    "longRestRecoveryType": "ALL"
}'::jsonb
WHERE slug = 'rage-2014';

-- 3. Backfill legacy character_feats with null max_charges or current_charges from feat definitions
UPDATE player_character.character_feats cf
SET max_charges = COALESCE(
        cf.max_charges,
        NULLIF((f.mechanical_effects->>'fixedMaxCharges'), '')::INT,
        NULLIF((f.mechanical_effects->>'usesPerRest'), '')::INT,
        1
    ),
    current_charges = COALESCE(
        cf.current_charges,
        cf.max_charges,
        NULLIF((f.mechanical_effects->>'fixedMaxCharges'), '')::INT,
        NULLIF((f.mechanical_effects->>'usesPerRest'), '')::INT,
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
      OR f.mechanical_effects ? 'maxChargesType'
  );
