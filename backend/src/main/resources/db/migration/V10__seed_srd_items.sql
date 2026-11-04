-- Migration V10: Seed SRD Items (2014 & 2024) translated to pt-BR

-- SRD 2014 Items
INSERT INTO player_character.items (ruleset, slug, name_en, name, item_category, item_type, rarity, attunement, cost, weight, description, source)
VALUES 
('SRD_2014', 'longsword-2014', 'Longsword', 'Espada Longa', 'WEAPON', 'Arma Marcial Corpo a Corpo', 'NONE', FALSE, '15 po', '1,5 kg', 
$$Uma espada de lâmina reta versátil de gume duplo. Causa 1d8 de dano cortante com uma mão, ou 1d10 se empunhada com as duas mãos (versátil).$$, 'SRD'),

('SRD_2014', 'chain-mail-2014', 'Chain Mail', 'Cota de Malha', 'ARMOR', 'Armadura Pesada', 'NONE', FALSE, '75 po', '25 kg', 
$$Feita de anéis de metal entrelaçados. Fornece CA 16 fixa, exige Força 13 e impõe desvantagem em testes de Furtividade.$$, 'SRD'),

('SRD_2014', 'potion-of-healing-2014', 'Potion of Healing', 'Poção de Cura', 'MAGIC', 'Poção', 'COMMON', FALSE, '50 po', '0,2 kg', 
$$Um líquido vermelho borbulhante. Beber ou ministrar esta poção recupera 2d4 + 2 pontos de vida.$$, 'SRD'),

('SRD_2014', 'boots-of-elvenkind-2014', 'Boots of Elvenkind', 'Botas Élficas', 'MAGIC', 'Item Maravilhoso', 'UNCOMMON', FALSE, '500 po', '0,5 kg', 
$$Enquanto você estiver calçando estas botas, seus passos não produzem som algum e você tem vantagem em testes de Destreza (Furtividade) que dependam de silêncio.$$, 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;

-- SRD 2024 Items
INSERT INTO player_character.items (ruleset, slug, name_en, name, item_category, item_type, rarity, attunement, cost, weight, description, source)
VALUES 
('SRD_2024', 'longsword-2024', 'Longsword', 'Espada Longa', 'WEAPON', 'Arma Marcial Corpo a Corpo', 'NONE', FALSE, '15 po', '1,5 kg', 
$$Espada versátil das regras atualizadas de 2024. Causa 1d8 cortante (1d10 versátil) e possui a propriedade de Domínio de Arma Sap (Abalar).$$, 'SRD'),

('SRD_2024', 'plate-armor-2024', 'Plate Armor', 'Armadura de Placas', 'ARMOR', 'Armadura Pesada', 'NONE', FALSE, '1500 po', '30 kg', 
$$Armadura completa de placas metálicas moldadas. Concede CA 18, exige Força 15 e impõe desvantagem em Furtividade.$$, 'SRD'),

('SRD_2024', 'potion-of-healing-2024', 'Potion of Healing', 'Poção de Cura', 'MAGIC', 'Poção', 'COMMON', FALSE, '50 po', '0,2 kg', 
$$Elixir medicinal alquímico. Recupera 2d4 + 2 pontos de vida, utilizável com uma Ação Bônus nas regras revisadas de 2024.$$, 'SRD'),

('SRD_2024', 'cloak-of-protection-2024', 'Cloak of Protection', 'Manto de Proteção', 'MAGIC', 'Item Maravilhoso', 'UNCOMMON', TRUE, '1000 po', '0,5 kg', 
$$Você ganha +1 de bônus na Classe de Armadura e em todas as salvaguardas enquanto estiver vestindo este manto.$$, 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;
