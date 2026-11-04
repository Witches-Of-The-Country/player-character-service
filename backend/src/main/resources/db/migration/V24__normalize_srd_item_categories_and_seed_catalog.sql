-- Migration V24: Normalize SRD Item Categories and Seed Complete Compendium Items
-- Normalizes item_category based on item_type (POTION, WONDROUS, RING, SCROLL, WAND, ROD, STAFF, EQUIPMENT)
-- Seeds all missing standard SRD 2014 & SRD 2024 Potions, Wondrous Items, Adventuring Gear, Rings, Scrolls, Wands, Rods, and Staves.

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Normalization of existing items in player_character.items
UPDATE player_character.items
SET item_category = 'POTION'
WHERE item_type ILIKE '%poção%' OR item_type ILIKE '%potion%';

UPDATE player_character.items
SET item_category = 'RING'
WHERE item_type ILIKE '%anel%' OR item_type ILIKE '%ring%';

UPDATE player_character.items
SET item_category = 'SCROLL'
WHERE item_type ILIKE '%pergaminho%' OR item_type ILIKE '%scroll%';

UPDATE player_character.items
SET item_category = 'WAND'
WHERE item_type ILIKE '%varinha%' OR item_type ILIKE '%wand%';

UPDATE player_character.items
SET item_category = 'ROD'
WHERE item_type ILIKE '%bastão%' OR item_type ILIKE '%rod%';

UPDATE player_character.items
SET item_category = 'STAFF'
WHERE item_type ILIKE '%cajado%' OR item_type ILIKE '%staff%';

UPDATE player_character.items
SET item_category = 'WONDROUS'
WHERE (item_type ILIKE '%maravilhoso%' OR item_type ILIKE '%wondrous%')
  AND item_category NOT IN ('POTION', 'RING', 'SCROLL', 'WAND', 'ROD', 'STAFF');

UPDATE player_character.items
SET item_category = 'EQUIPMENT'
WHERE item_type ILIKE '%equipamento%' 
   OR item_type ILIKE '%ferramenta%' 
   OR item_type ILIKE '%gear%' 
   OR item_type ILIKE '%kit%' 
   OR item_category = 'ADVENTURING_GEAR';

UPDATE player_character.items
SET rarity = 'COMMON'
WHERE (rarity IS NULL OR rarity = 'NONE') AND item_category IN ('WEAPON', 'ARMOR', 'EQUIPMENT');

-- 2. Seed Potions (SRD 2014 & SRD 2024)
INSERT INTO player_character.items (ruleset, slug, name_en, name, item_category, item_type, rarity, attunement, cost, weight, description, source, mechanical_properties)
VALUES
('SRD_2014', 'potion-of-healing-greater-2014', 'Potion of Greater Healing', 'Poção de Cura Maior', 'POTION', 'Poção', 'UNCOMMON', FALSE, '150 po', '0,2 kg',
$$Você recupera 4d4 + 4 pontos de vida ao beber esta poção. O líquido vermelho cintila quando agitado.$$, 'SRD',
'{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "4d4+4", "effectDescription": "Recupera 4d4 + 4 pontos de vida imediatamente ao ser consumida."}'::jsonb),

('SRD_2024', 'potion-of-healing-greater-2024', 'Potion of Greater Healing', 'Poção de Cura Maior', 'POTION', 'Poção', 'UNCOMMON', FALSE, '150 po', '0,2 kg',
$$Você recupera 4d4 + 4 pontos de vida ao beber esta poção. Pode ser consumida como Ação Bônus nas regras de 2024.$$, 'SRD',
'{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "4d4+4", "effectDescription": "Recupera 4d4 + 4 pontos de vida imediatamente ao ser consumida."}'::jsonb),

('SRD_2014', 'potion-of-healing-superior-2014', 'Potion of Superior Healing', 'Poção de Cura Superior', 'POTION', 'Poção', 'RARE', FALSE, '500 po', '0,2 kg',
$$Você recupera 8d4 + 8 pontos de vida ao beber esta poção. O líquido espesso vermelho brilha intensamente.$$, 'SRD',
'{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "8d4+8", "effectDescription": "Recupera 8d4 + 8 pontos de vida imediatamente ao ser consumida."}'::jsonb),

('SRD_2024', 'potion-of-healing-superior-2024', 'Potion of Superior Healing', 'Poção de Cura Superior', 'POTION', 'Poção', 'RARE', FALSE, '500 po', '0,2 kg',
$$Você recupera 8d4 + 8 pontos de vida ao beber esta poção. Pode ser consumida como Ação Bônus nas regras de 2024.$$, 'SRD',
'{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "8d4+8", "effectDescription": "Recupera 8d4 + 8 pontos de vida imediatamente ao ser consumida."}'::jsonb),

('SRD_2014', 'potion-of-healing-supreme-2014', 'Potion of Supreme Healing', 'Poção de Cura Suprema', 'POTION', 'Poção', 'VERY_RARE', FALSE, '1500 po', '0,2 kg',
$$Você recupera 10d4 + 20 pontos de vida ao beber esta poção. O líquido vermelho rubi pulsa com pura energia curativa.$$, 'SRD',
'{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "10d4+20", "effectDescription": "Recupera 10d4 + 20 pontos de vida imediatamente ao ser consumida."}'::jsonb),

('SRD_2024', 'potion-of-healing-supreme-2024', 'Potion of Supreme Healing', 'Poção de Cura Suprema', 'POTION', 'Poção', 'VERY_RARE', FALSE, '1500 po', '0,2 kg',
$$Você recupera 10d4 + 20 pontos de vida ao beber esta poção. Pode ser consumida como Ação Bônus nas regras de 2024.$$, 'SRD',
'{"type": "CONSUMABLE", "effectType": "HEAL_HP", "diceFormula": "10d4+20", "effectDescription": "Recupera 10d4 + 20 pontos de vida imediatamente ao ser consumida."}'::jsonb),

('SRD_2014', 'potion-of-invisibility-2014', 'Potion of Invisibility', 'Poção de Invisibilidade', 'POTION', 'Poção', 'VERY_RARE', FALSE, '800 po', '0,2 kg',
$$Ao beber esta poção, você se torna invisível por 1 hora. Qualquer equipamento que esteja usando ou carregando fica invisível com você. O efeito encerra prematuramente se você atacar ou conjurar uma magia.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'potion-of-invisibility-2024', 'Potion of Invisibility', 'Poção de Invisibilidade', 'POTION', 'Poção', 'VERY_RARE', FALSE, '800 po', '0,2 kg',
$$Ao beber esta poção, você ganha a condição Invisível por 1 hora ou até realizar um ataque, causar dano ou conjurar uma magia.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'potion-of-speed-2014', 'Potion of Speed', 'Poção de Velocidade', 'POTION', 'Poção', 'VERY_RARE', FALSE, '1000 po', '0,2 kg',
$$Ao beber esta poção, você ganha os benefícios da magia Velocidade (Haste) por 1 minuto sem precisar se concentrar. O líquido amarelo parece vibrar dentro do frasco.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'potion-of-speed-2024', 'Potion of Speed', 'Poção de Velocidade', 'POTION', 'Poção', 'VERY_RARE', FALSE, '1000 po', '0,2 kg',
$$Ao beber esta poção, você ganha o efeito da magia Velocidade por 1 minuto.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'potion-of-water-breathing-2014', 'Potion of Water Breathing', 'Poção de Respirar na Água', 'POTION', 'Poção', 'UNCOMMON', FALSE, '250 po', '0,2 kg',
$$Você pode respirar sob a água por 1 hora após beber esta poção. O líquido límpido cheira a brisa marítima.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'potion-of-water-breathing-2024', 'Potion of Water Breathing', 'Poção de Respirar na Água', 'POTION', 'Poção', 'UNCOMMON', FALSE, '250 po', '0,2 kg',
$$Você pode respirar sob a água por 1 hora após beber esta poção.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'potion-of-heroism-2014', 'Potion of Heroism', 'Poção de Heroísmo', 'POTION', 'Poção', 'RARE', FALSE, '500 po', '0,2 kg',
$$Por 1 hora após beber esta poção, você ganha 10 pontos de vida temporários e está sob o efeito da magia Benção (adiciona 1d4 a jogadas de ataque e salvaguardas).$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'potion-of-heroism-2024', 'Potion of Heroism', 'Poção de Heroísmo', 'POTION', 'Poção', 'RARE', FALSE, '500 po', '0,2 kg',
$$Por 1 hora após beber esta poção, você ganha 10 pontos de vida temporários e os benefícios da magia Benção.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'potion-of-flying-2014', 'Potion of Flying', 'Poção de Voo', 'POTION', 'Poção', 'VERY_RARE', FALSE, '1000 po', '0,2 kg',
$$Ao beber esta poção, você ganha deslocamento de voo igual ao seu deslocamento de caminhada por 1 hora e pode pairar.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'potion-of-flying-2024', 'Potion of Flying', 'Poção de Voo', 'POTION', 'Poção', 'VERY_RARE', FALSE, '1000 po', '0,2 kg',
$$Ao beber esta poção, você ganha deslocamento de voo igual ao seu deslocamento de caminhada por 1 hora.$$, 'SRD', '{}'::jsonb)

ON CONFLICT (ruleset, slug) DO UPDATE SET
    name = EXCLUDED.name,
    name_en = EXCLUDED.name_en,
    item_category = EXCLUDED.item_category,
    item_type = EXCLUDED.item_type,
    rarity = EXCLUDED.rarity,
    cost = EXCLUDED.cost,
    weight = EXCLUDED.weight,
    description = EXCLUDED.description,
    mechanical_properties = EXCLUDED.mechanical_properties;

-- 3. Seed Adventuring Gear, Kits, and Tools (SRD 2014 & SRD 2024)
INSERT INTO player_character.items (ruleset, slug, name_en, name, item_category, item_type, rarity, attunement, cost, weight, description, source, mechanical_properties)
VALUES
('SRD_2014', 'backpack-2014', 'Backpack', 'Mochila', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '2 po', '2,5 kg',
$$Uma mochila de couro resistente que pode transportar até 15 kg ou 30 litros de equipamento.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'backpack-2024', 'Backpack', 'Mochila', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '2 po', '2,5 kg',
$$Uma mochila resistente com compartimentos para organizar itens e equipamentos de viagem.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'bedroll-2014', 'Bedroll', 'Saco de Dormir', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '1 po', '3 kg',
$$Um cobertor quente e acolchoado com tiras de amarrar para dormir confortavelmente ao ar livre.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'bedroll-2024', 'Bedroll', 'Saco de Dormir', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '1 po', '3 kg',
$$Saco de dormir acolchoado para descanso e recuperação de pontos de vida durante descansos.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'rations-2014', 'Rations (1 day)', 'Rações de Viagem (1 dia)', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '5 pp', '1 kg',
$$Comida seca compacta própria para longas viagens: carne seca, frutas desidratadas, queijo e biscoitos duros.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'rations-2024', 'Rations (1 day)', 'Rações de Viagem (1 dia)', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '5 pp', '1 kg',
$$Provisões de alimentos nutritivos e compactos para suprir as necessidades de um aventureiro por 1 dia.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'waterskin-2014', 'Waterskin', 'Cantil de Couro', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '2 pp', '2,5 kg',
$$Cantil de couro selado capaz de comportar 2 litros de água fresca.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'waterskin-2024', 'Waterskin', 'Cantil de Couro', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '2 pp', '2,5 kg',
$$Cantil de couro selado capaz de comportar 2 litros de água fresca.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'rope-hempen-2014', 'Rope, Hempen (50 feet)', 'Corda de Cânhamo (15 metros)', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '1 po', '5 kg',
$$Possui 2 pontos de vida e pode ser arrebentada com um teste bem-sucedido de Força CD 17.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'rope-hempen-2024', 'Rope, Hempen (50 feet)', 'Corda de Cânhamo (15 metros)', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '1 po', '5 kg',
$$Corda clássica resistente de 15 metros para escaladas, amarras e armadilhas.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'torch-2014', 'Torch', 'Tocha', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '1 pc', '0,5 kg',
$$Queima por 1 hora, emitindo luz plena em um raio de 6 metros e luz penumbra por mais 6 metros.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'torch-2024', 'Torch', 'Tocha', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '1 pc', '0,5 kg',
$$Ilumina por 1 hora, emitindo luz brilhante em 6 metros e penumbra em mais 6 metros.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'tinderbox-2014', 'Tinderbox', 'Pederneira e Isqueiro', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '5 pp', '0,5 kg',
$$Contém uma pederneira, um isqueiro de ferro e mechas secas para acender tochas ou fogueiras em uma ação.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'tinderbox-2024', 'Tinderbox', 'Pederneira e Isqueiro', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '5 pp', '0,5 kg',
$$Kit essencial para atear fogo em materiais inflamáveis e preparar fogueiras de acampamento.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'crowbar-2014', 'Crowbar', 'Pé de Cabra', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '2 po', '2,5 kg',
$$Usar um pé de cabra concede vantagem em testes de Força quando a alavanca puder ser aplicada.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'crowbar-2024', 'Crowbar', 'Pé de Cabra', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '2 po', '2,5 kg',
$$Concede vantagem em testes de Força aplicáveis a abrir portas emperradas ou quebrar baús.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'thieves-tools-2014', 'Thieves Tools', 'Ferramentas de Ladrão', 'EQUIPMENT', 'Ferramenta', 'COMMON', FALSE, '25 po', '0,5 kg',
$$Inclui uma lima pequena, um conjunto de gazuas, um espelho pequeno com cabo de metal e alicates tesoura.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'thieves-tools-2024', 'Thieves Tools', 'Ferramentas de Ladrão', 'EQUIPMENT', 'Ferramenta', 'COMMON', FALSE, '25 po', '0,5 kg',
$$Conjunto de ferramentas para desarmar armadilhas e abrir fechaduras mecânicas.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'healers-kit-2014', 'Healer Kit', 'Kit de Primeiros Socorros', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '5 po', '1,5 kg',
$$Este kit possui 10 usos. Com uma ação, você pode gastar um uso para estabilizar uma criatura que esteja com 0 PV.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'healers-kit-2024', 'Healer Kit', 'Kit de Primeiros Socorros', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '5 po', '1,5 kg',
$$Possui 10 usos. Uma ação estabiliza uma criatura moribunda sem exigir um teste de Medicina.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'holy-symbol-amulet-2014', 'Holy Symbol (Amulet)', 'Símbolo Sagrado (Amuleto)', 'EQUIPMENT', 'Foco de Conjuração', 'COMMON', FALSE, '5 po', '0,5 kg',
$$Um amuleto gravado com o emblema sagrado de uma divindade ou panteão, utilizado por clérigos e paladinos.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'holy-symbol-amulet-2024', 'Holy Symbol (Amulet)', 'Símbolo Sagrado (Amuleto)', 'EQUIPMENT', 'Foco de Conjuração', 'COMMON', FALSE, '5 po', '0,5 kg',
$$Foco sagrado para canalizar magias divinas de clérigos e paladinos.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'arcane-focus-crystal-2014', 'Arcane Focus (Crystal)', 'Foco Arcano (Cristal)', 'EQUIPMENT', 'Foco de Conjuração', 'COMMON', FALSE, '10 po', '0,5 kg',
$$Um cristal especialmente lapidado para canalizar as energias de magias arcanas.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'arcane-focus-crystal-2024', 'Arcane Focus (Crystal)', 'Foco Arcano (Cristal)', 'EQUIPMENT', 'Foco de Conjuração', 'COMMON', FALSE, '10 po', '0,5 kg',
$$Cristal lapidado para magos, bruxos e feiticeiros canalizarem magias arcanas.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'component-pouch-2014', 'Component Pouch', 'Bolsa de Componentes', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '25 po', '1 kg',
$$Uma bolsa de cinto com compartimentos contendo todos os componentes materiais sem custo monetário exigidos por magias.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'component-pouch-2024', 'Component Pouch', 'Bolsa de Componentes', 'EQUIPMENT', 'Equipamento de Aventura', 'COMMON', FALSE, '25 po', '1 kg',
$$Bolsa compartimentada contendo materiais arcanos sem valor explícito para conjuração.$$, 'SRD', '{}'::jsonb)

ON CONFLICT (ruleset, slug) DO UPDATE SET
    name = EXCLUDED.name,
    name_en = EXCLUDED.name_en,
    item_category = EXCLUDED.item_category,
    item_type = EXCLUDED.item_type,
    rarity = EXCLUDED.rarity,
    cost = EXCLUDED.cost,
    weight = EXCLUDED.weight,
    description = EXCLUDED.description;

-- 4. Seed Iconic Wondrous Items (SRD 2014 & SRD 2024)
INSERT INTO player_character.items (ruleset, slug, name_en, name, item_category, item_type, rarity, attunement, cost, weight, description, source, mechanical_properties)
VALUES
('SRD_2014', 'bag-of-holding-2014', 'Bag of Holding', 'Bolsa de Carga', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', FALSE, '500 po', '7 kg',
$$Esta bolsa tem um espaço interior consideravelmente maior do que suas dimensões externas. Ela pode comportar até 250 kg sem exceder um volume de 1,8 metros cúbicos. O peso da bolsa permanece fixo em 7 kg independentemente do que houver dentro.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'bag-of-holding-2024', 'Bag of Holding', 'Bolsa de Carga', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', FALSE, '500 po', '7 kg',
$$Bolsa mágica extradimensional clássica que armazena até 250 kg mantendo sempre um peso leve.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'cloak-of-elvenkind-2014', 'Cloak of Elvenkind', 'Manto Élfico', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', TRUE, '500 po', '0,5 kg',
$$Enquanto estiver usando este manto com o capuz erguido, testes de Destreza (Furtividade) feitos para se esconder são realizados com vantagem, e testes de Sabedoria (Percepção) para notar você são feitos com desvantagem.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'cloak-of-elvenkind-2024', 'Cloak of Elvenkind', 'Manto Élfico', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', TRUE, '500 po', '0,5 kg',
$$Manto camuflado que concede vantagem em Furtividade e impõe desvantagem em testes de Percepção contra o usuário.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'gauntlets-of-ogre-power-2014', 'Gauntlets of Ogre Power', 'Manoplas do Poder de Ogro', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', TRUE, '1000 po', '1 kg',
$$Seu valor de Força se torna 19 enquanto você vestir estas manoplas. Elas não têm efeito se seu valor de Força já for 19 ou maior.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'gauntlets-of-ogre-power-2024', 'Gauntlets of Ogre Power', 'Manoplas do Poder de Ogro', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', TRUE, '1000 po', '1 kg',
$$Define o valor de Força do usuário em 19 caso seja inferior.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'headband-of-intellect-2014', 'Headband of Intellect', 'Tiara do Intelecto', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', TRUE, '1000 po', '0,5 kg',
$$Seu valor de Inteligência se torna 19 enquanto você usar esta tiara. Ela não tem efeito se seu valor de Inteligência já for 19 ou maior.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'headband-of-intellect-2024', 'Headband of Intellect', 'Tiara do Intelecto', 'WONDROUS', 'Item Maravilhoso', 'UNCOMMON', TRUE, '1000 po', '0,5 kg',
$$Define o valor de Inteligência do usuário em 19 caso seja inferior.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'amulet-of-health-2014', 'Amulet of Health', 'Amuleto da Saúde', 'WONDROUS', 'Item Maravilhoso', 'RARE', TRUE, '4000 po', '0,5 kg',
$$Seu valor de Constituição se torna 19 enquanto você usar este amuleto. Não tem efeito se sua Constituição já for 19 ou maior.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'amulet-of-health-2024', 'Amulet of Health', 'Amuleto da Saúde', 'WONDROUS', 'Item Maravilhoso', 'RARE', TRUE, '4000 po', '0,5 kg',
$$Define o valor de Constituição do usuário em 19 caso seja inferior.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'bracers-of-defense-2014', 'Bracers of Defense', 'Braçadeiras de Defesa', 'WONDROUS', 'Item Maravilhoso', 'RARE', TRUE, '3000 po', '0,5 kg',
$$Enquanto usar estas braçadeiras, você ganha +2 de bônus na Classe de Armadura se não estiver vestindo armadura nem empunhando escudo.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'bracers-of-defense-2024', 'Bracers of Defense', 'Braçadeiras de Defesa', 'WONDROUS', 'Item Maravilhoso', 'RARE', TRUE, '3000 po', '0,5 kg',
$$Concede +2 na CA do usuário desarmadurado e sem escudo.$$, 'SRD', '{}'::jsonb)

ON CONFLICT (ruleset, slug) DO UPDATE SET
    name = EXCLUDED.name,
    name_en = EXCLUDED.name_en,
    item_category = EXCLUDED.item_category,
    item_type = EXCLUDED.item_type,
    rarity = EXCLUDED.rarity,
    attunement = EXCLUDED.attunement,
    cost = EXCLUDED.cost,
    weight = EXCLUDED.weight,
    description = EXCLUDED.description;

-- 5. Seed Rings, Scrolls, Wands, Rods, Staves (SRD 2014 & SRD 2024)
INSERT INTO player_character.items (ruleset, slug, name_en, name, item_category, item_type, rarity, attunement, cost, weight, description, source, mechanical_properties)
VALUES
('SRD_2014', 'ring-of-protection-2014', 'Ring of Protection', 'Anel de Proteção', 'RING', 'Anel', 'RARE', TRUE, '3500 po', '—',
$$Você ganha um bônus de +1 na Classe de Armadura e em todas as salvaguardas enquanto usar este anel sintonizado.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'ring-of-protection-2024', 'Ring of Protection', 'Anel de Proteção', 'RING', 'Anel', 'RARE', TRUE, '3500 po', '—',
$$Concede +1 na CA e em todas as jogadas de proteção enquanto estiver sintonizado.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'ring-of-feather-falling-2014', 'Ring of Feather Falling', 'Anel de Queda Suave', 'RING', 'Anel', 'RARE', TRUE, '2000 po', '—',
$$Quando você cai enquanto usa este anel, você cai a uma velocidade segura de 18 metros por rodada e não sofre dano de queda.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'ring-of-feather-falling-2024', 'Ring of Feather Falling', 'Anel de Queda Suave', 'RING', 'Anel', 'RARE', TRUE, '2000 po', '—',
$$Anula todo o dano de queda enquanto sintonizado com o usuário.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'wand-of-magic-missiles-2014', 'Wand of Magic Missiles', 'Varinha de Mísseis Mágicos', 'WAND', 'Varinha', 'UNCOMMON', FALSE, '1500 po', '0,5 kg',
$$Esta varinha possui 7 cargas. Com uma ação, você pode gastar de 1 a 7 cargas para conjurar a magia Mísseis Mágicos. Recupera 1d6 + 1 cargas ao amanhecer.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'wand-of-magic-missiles-2024', 'Wand of Magic Missiles', 'Varinha de Mísseis Mágicos', 'WAND', 'Varinha', 'UNCOMMON', FALSE, '1500 po', '0,5 kg',
$$Varinha com 7 cargas para conjurar Mísseis Mágicos sem consumir espaços de magia.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'rod-of-the-pact-keeper-plus1-2014', 'Rod of the Pact Keeper +1', 'Bastão do Guardião do Pacto +1', 'ROD', 'Bastão', 'UNCOMMON', TRUE, '2000 po', '1 kg',
$$Enquanto empunha este bastão, você ganha +1 de bônus nas jogadas de ataque de magia e na CD das magias de Bruxo. Pode recuperar 1 espaço de magia de bruxo com uma ação uma vez por descanso longo.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'rod-of-the-pact-keeper-plus1-2024', 'Rod of the Pact Keeper +1', 'Bastão do Guardião do Pacto +1', 'ROD', 'Bastão', 'UNCOMMON', TRUE, '2000 po', '1 kg',
$$Concede +1 na CD e ataque de magia de bruxo e restaura 1 espaço de magia pacto diariamente.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'staff-of-healing-2014', 'Staff of Healing', 'Cajado da Cura', 'STAFF', 'Cajado', 'RARE', TRUE, '5000 po', '2 kg',
$$Possui 10 cargas. Permite conjurar Curar Ferimentos (1 carga por nível), Restauração Menor (2 cargas) ou Cura Completa (6 cargas). Recupera 1d6 + 4 cargas ao amanhecer.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'staff-of-healing-2024', 'Staff of Healing', 'Cajado da Cura', 'STAFF', 'Cajado', 'RARE', TRUE, '5000 po', '2 kg',
$$Cajado potente com 10 cargas para conjurar magias restaurativas vitais.$$, 'SRD', '{}'::jsonb),

('SRD_2014', 'spell-scroll-level-1-2014', 'Spell Scroll (Level 1)', 'Pergaminho de Magia (1º Nível)', 'SCROLL', 'Pergaminho', 'COMMON', FALSE, '50 po', '—',
$$Um pergaminho contendo uma magia de 1º nível. Permite conjurar a magia sem componentes materiais. O pergaminho se desfaz em pó após o uso.$$, 'SRD', '{}'::jsonb),

('SRD_2024', 'spell-scroll-level-1-2024', 'Spell Scroll (Level 1)', 'Pergaminho de Magia (1º Nível)', 'SCROLL', 'Pergaminho', 'COMMON', FALSE, '50 po', '—',
$$Pergaminho consumível contendo uma magia de 1º círculo.$$, 'SRD', '{}'::jsonb)

ON CONFLICT (ruleset, slug) DO UPDATE SET
    name = EXCLUDED.name,
    name_en = EXCLUDED.name_en,
    item_category = EXCLUDED.item_category,
    item_type = EXCLUDED.item_type,
    rarity = EXCLUDED.rarity,
    attunement = EXCLUDED.attunement,
    cost = EXCLUDED.cost,
    weight = EXCLUDED.weight,
    description = EXCLUDED.description;
