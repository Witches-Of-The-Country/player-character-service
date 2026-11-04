-- Migration V20: Seed Complete SRD Class Progression Feats, Additional Subclasses, and Subclass Feats (2014 & 2024)
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Insert Additional Subclasses for All Classes
INSERT INTO player_character.subclasses (id, archetype_id, ruleset, slug, name_en, name, description, source)
VALUES
    -- Fighter: Battle Master / Mestre da Batalha
    ('12000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000005', 'SRD_2014', 'battle-master-2014', 'Battle Master', 'Mestre da Batalha', 'Mestres da Batalha empregam manobras marciais táticas e dados de superioridade para controlar o campo de combate.', 'SRD'),
    ('22000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000005', 'SRD_2024', 'battle-master-2024', 'Battle Master', 'Mestre da Batalha', 'Guerreiro tático supremo das regras 2024 com manobras refinadas e versatilidade marcial.', 'SRD'),

    -- Fighter: Eldritch Knight / Cavaleiro Arcano
    ('12000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000005', 'SRD_2014', 'eldritch-knight-2014', 'Eldritch Knight', 'Cavaleiro Arcano', 'Combina a mestria marcial do guerreiro com conjuração de magias arcanas de abjuração e evocação.', 'SRD'),
    ('22000000-0000-0000-0000-000000000015', '20000000-0000-0000-0000-000000000005', 'SRD_2024', 'eldritch-knight-2024', 'Eldritch Knight', 'Cavaleiro Arcano', 'Guerreiro arcano versátil capaz de tecer magias entre ataques corpo a corpo.', 'SRD'),

    -- Barbarian: Path of the Totem Warrior / Caminho do Guerreiro Totêmico
    ('12000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000001', 'SRD_2014', 'totem-warrior-2014', 'Path of the Totem Warrior', 'Caminho do Totem', 'Guia espiritual animal que concede resistência sobrenatural e dons de feras míticas.', 'SRD'),
    ('22000000-0000-0000-0000-000000000016', '20000000-0000-0000-0000-000000000001', 'SRD_2024', 'wild-heart-2024', 'Path of the Wild Heart', 'Caminho do Coração Selvagem', 'Canaliza o poder primal da natureza e dos espíritos animais com fúrias adaptativas.', 'SRD'),

    -- Rogue: Assassin / Assassino
    ('12000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000009', 'SRD_2014', 'assassin-2014', 'Assassin', 'Assassino', 'Especialista em disfarces, venenos e ataques surpresa letais instantâneos.', 'SRD'),
    ('22000000-0000-0000-0000-000000000017', '20000000-0000-0000-0000-000000000009', 'SRD_2024', 'assassin-2024', 'Assassin', 'Assassino', 'Mestre do bote letal das sombras, com vantagens devastadoras na rodada de surpresa.', 'SRD'),

    -- Rogue: Arcane Trickster / Trapaceiro Arcano
    ('12000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000009', 'SRD_2014', 'arcane-trickster-2014', 'Arcane Trickster', 'Trapaceiro Arcano', 'Ladino aprimorado com ilusões e encantamentos arcanos para roubos e infiltrações mágicas.', 'SRD'),
    ('22000000-0000-0000-0000-000000000018', '20000000-0000-0000-0000-000000000009', 'SRD_2024', 'arcane-trickster-2024', 'Arcane Trickster', 'Trapaceiro Arcano', 'Especialista em Mãos Mágicas invisíveis e truques arcanos táticos em combate.', 'SRD'),

    -- Cleric: Light Domain / Domínio da Luz
    ('12000000-0000-0000-0000-000000000019', '10000000-0000-0000-0000-000000000003', 'SRD_2014', 'light-domain-2014', 'Light Domain', 'Domínio da Luz', 'Canaliza o fogo radiante solar para queimar trevas e cegar agressores.', 'SRD'),
    ('22000000-0000-0000-0000-000000000019', '20000000-0000-0000-0000-000000000003', 'SRD_2024', 'light-domain-2024', 'Light Domain', 'Domínio da Luz', 'Clérigo radiante que irradia poder coronal e queima inimigos com luz divina.', 'SRD'),

    -- Wizard: School of Abjuration / Escola de Abjuração
    ('12000000-0000-0000-0000-000000000021', '10000000-0000-0000-0000-000000000012', 'SRD_2014', 'abjuration-2014', 'School of Abjuration', 'Escola de Abjuração', 'Mago focado em feitiços de proteção, barreiras arcanas e cancelamento de magias inimigas.', 'SRD'),
    ('22000000-0000-0000-0000-000000000021', '20000000-0000-0000-0000-000000000012', 'SRD_2024', 'abjurer-2024', 'Abjurer', 'Abjurador', 'Mago protetor supremo com proteção arcana projetada e absorção mágica defensiva.', 'SRD'),

    -- Wizard: School of Divination / Escola de Adivinhação
    ('12000000-0000-0000-0000-000000000022', '10000000-0000-0000-0000-000000000012', 'SRD_2014', 'divination-2014', 'School of Divination', 'Escola de Adivinhação', 'Vê o futuro através de presságios, manipulando rolagens de dados com a habilidade Portento.', 'SRD'),
    ('22000000-0000-0000-0000-000000000022', '20000000-0000-0000-0000-000000000012', 'SRD_2024', 'diviner-2024', 'Diviner', 'Adivinhador', 'Mestre do destino que altera o fluxo dos acontecimentos com visões premonitórias.', 'SRD'),

    -- Warlock: The Archfey / A Arquifada
    ('12000000-0000-0000-0000-000000000023', '10000000-0000-0000-0000-000000000011', 'SRD_2014', 'the-archfey-2014', 'The Archfey', 'A Arquifada', 'Pacto com nobres das fadas de Feywild, concedendo ilusões, encantamentos e fugas místicas.', 'SRD'),
    ('22000000-0000-0000-0000-000000000023', '20000000-0000-0000-0000-000000000011', 'SRD_2024', 'archfey-patron-2024', 'Archfey Patron', 'Patrono Arquifada', 'Bruxo feérico com passos misty repetidos e feitiços ilusórios sedutores.', 'SRD'),

    -- Paladin: Oath of Vengeance / Juramento de Vingança
    ('12000000-0000-0000-0000-000000000026', '10000000-0000-0000-0000-000000000007', 'SRD_2014', 'oath-of-vengeance-2014', 'Oath of Vengeance', 'Juramento de Vingança', 'Guerreiro sagrado vingador implacável contra o mal, usando Voto de Inimizade.', 'SRD'),
    ('22000000-0000-0000-0000-000000000026', '20000000-0000-0000-0000-000000000007', 'SRD_2024', 'oath-of-vengeance-2024', 'Oath of Vengeance', 'Juramento de Vingança', 'Paladino vingador com mobilidade implacável e destruição impiedosa dos jurados.', 'SRD'),

    -- Druid: Circle of the Moon / Círculo da Lua
    ('12000000-0000-0000-0000-000000000029', '10000000-0000-0000-0000-000000000004', 'SRD_2014', 'circle-of-the-moon-2014', 'Circle of the Moon', 'Círculo da Lua', 'Forma Selvagem de combate aprimorada, transformando-se em bestas ferozes como ação bônus.', 'SRD'),
    ('22000000-0000-0000-0000-000000000029', '20000000-0000-0000-0000-000000000004', 'SRD_2024', 'circle-of-the-moon-2024', 'Circle of the Moon', 'Círculo da Lua', 'Druida metamorfo supremo com armadura lunar, ataques radiantes e cura elemental em forma animal.', 'SRD'),

    -- Monk: Way of Shadow / Caminho das Sombras
    ('12000000-0000-0000-0000-000000000030', '10000000-0000-0000-0000-000000000006', 'SRD_2014', 'way-of-shadow-2014', 'Way of Shadow', 'Caminho das Sombras', 'Monges das sombras agem como espiões e assassinos, teletransportando-se entre sombras.', 'SRD'),
    ('22000000-0000-0000-0000-000000000030', '20000000-0000-0000-0000-000000000006', 'SRD_2024', 'shadow-warrior-2024', 'Warrior of Shadow', 'Guerreiro das Sombras', 'Ninja místico que manipula trevas mágicas e ataques furtivos velozes.', 'SRD'),

    -- Ranger: Beast Master / Mestre das Feras
    ('12000000-0000-0000-0000-000000000031', '10000000-0000-0000-0000-000000000008', 'SRD_2014', 'beast-master-2014', 'Beast Master', 'Mestre das Feras', 'Cria um vínculo inabalável com um companheiro animal leal que luta em perfeita harmonia.', 'SRD'),
    ('22000000-0000-0000-0000-000000000031', '20000000-0000-0000-0000-000000000008', 'SRD_2024', 'beast-master-2024', 'Beast Master', 'Mestre das Feras', 'Patrulheiro com companheiro espiritual da terra, mar ou ar que ataca em conjunto.', 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;

-- 2. Seed Complete Class Progression Feats (Levels 1-20) for Missing Classes
INSERT INTO player_character.feats (id, ruleset, slug, name_en, name, prerequisites, description, source, feat_type, mechanical_effects)
VALUES
    -- Cleric Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000060', 'SRD_2014', 'cleric-spellcasting-2014', 'Spellcasting (Cleric)', 'Conjuração (Clérigo)', NULL, 'Conjurador de magia divina baseada em Sabedoria que prepara magias diariamente.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000061', 'SRD_2014', 'channel-divinity-2014', 'Channel Divinity', 'Canalizar Divindade', 'Clérigo Nível 2', 'Canaliza energia divina para abastecer efeitos mágicos como Expulsar Mortos-Vivos.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"ACTION","actionName":"Canalizar Divindade","maxUses":1,"rechargeOn":"SHORT_REST"}'::jsonb),
    ('30000000-0000-0000-0000-000000000062', 'SRD_2014', 'destroy-undead-2014', 'Destroy Undead', 'Destruir Mortos-Vivos', 'Clérigo Nível 5', 'Mortos-vivos de baixo ND que falharem no teste de resistência contra Expulsar Mortos-Vivos são instantaneamente destruídos.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000063', 'SRD_2014', 'divine-intervention-2014', 'Divine Intervention', 'Intervenção Divina', 'Clérigo Nível 10', 'Suplica auxílio direto de sua divindade para intervir na batalha com uma chance percentual.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"ACTION","actionName":"Intervenção Divina","maxUses":1,"rechargeOn":"LONG_REST"}'::jsonb),

    ('40000000-0000-0000-0000-000000000060', 'SRD_2024', 'cleric-spellcasting-2024', 'Spellcasting (Cleric)', 'Conjuração (Clérigo)', NULL, 'Canalizador divino que prepara magias de sua lista completa a cada descanso longo.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000061', 'SRD_2024', 'channel-divinity-2024', 'Channel Divinity', 'Canalizar Divindade', 'Clérigo Nível 2', 'Canalização divina com usos escalonados por descanso para centelhas de cura e afastar infiéis.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"ACTION","actionName":"Canalizar Divindade","maxUses":2,"rechargeOn":"SHORT_REST"}'::jsonb),
    ('40000000-0000-0000-0000-000000000062', 'SRD_2024', 'destroy-undead-2024', 'Destroy Undead', 'Destruir Mortos-Vivos', 'Clérigo Nível 5', 'Causa dano radiante massivo ou aniquilação contra mortos-vivos afetados.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000063', 'SRD_2024', 'divine-intervention-2024', 'Divine Intervention', 'Intervenção Divina', 'Clérigo Nível 10', 'Milagre divino garantido sem necessidade de teste percentual uma vez a cada descanso longo.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"ACTION","actionName":"Intervenção Divina","maxUses":1,"rechargeOn":"LONG_REST"}'::jsonb),

    -- Druid Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000070', 'SRD_2014', 'druid-druidic-2014', 'Druidic', 'Druídico', NULL, 'Conhece a linguagem secreta dos druidas e deixa mensagens ocultas na natureza.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000071', 'SRD_2014', 'wild-shape-2014', 'Wild Shape', 'Forma Selvagem', 'Druida Nível 2', 'Assume magicamente a forma de uma fera que você já tenha visto.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"BONUS_ACTION","actionName":"Forma Selvagem","maxUses":2,"rechargeOn":"SHORT_REST"}'::jsonb),
    ('30000000-0000-0000-0000-000000000072', 'SRD_2014', 'timeless-body-druid-2014', 'Timeless Body', 'Corpo Atemporal', 'Druida Nível 18', 'A magia primordial faz com que seu corpo envelheça dez vezes mais devagar.', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000070', 'SRD_2024', 'druid-druidic-2024', 'Druidic', 'Druídico', NULL, 'Idioma secreto druídico e conexão perpétua com plantas e animais.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000071', 'SRD_2024', 'wild-shape-2024', 'Wild Shape', 'Forma Selvagem', 'Druida Nível 2', 'Transformação ágil em feras com Pontos de Vida Temporários calculados de forma simplificada.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"BONUS_ACTION","actionName":"Forma Selvagem","maxUses":2,"rechargeOn":"SHORT_REST"}'::jsonb),

    -- Monk Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000080', 'SRD_2014', 'unarmored-defense-monk-2014', 'Unarmored Defense (Monk)', 'Defesa Sem Armadura (Monge)', NULL, 'Sua CA é igual a 10 + DES + SAB quando não estiver usando armadura ou escudo.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000081', 'SRD_2014', 'martial-arts-2014', 'Martial Arts', 'Artes Marciais', NULL, 'Permite usar Destreza para ataques desarmados e desferir golpes desarmados como ação bônus.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000082', 'SRD_2014', 'ki-monk-2014', 'Ki Points', 'Pontos de Ki', 'Monge Nível 2', 'Energia mística interna usada para Rajada de Golpes, Defesa Paciente e Passo do Vento.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"BONUS_ACTION","actionName":"Habilidade de Ki","maxUses":2,"rechargeOn":"SHORT_REST"}'::jsonb),
    ('30000000-0000-0000-0000-000000000083', 'SRD_2014', 'unarmored-movement-2014', 'Unarmored Movement', 'Movimento Sem Armadura', 'Monge Nível 2', 'Seu deslocamento aumenta em 10 pés enquanto não vestir armadura.', 'SRD', 'CLASS', '{"speedBonus":10}'::jsonb),
    ('30000000-0000-0000-0000-000000000084', 'SRD_2014', 'stunning-strike-2014', 'Stunning Strike', 'Ataque Desarmante', 'Monge Nível 5', 'Ao acertar um ataque corpo a corpo, gasta 1 ponto de Ki para atordoar o alvo.', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000080', 'SRD_2024', 'unarmored-defense-monk-2024', 'Unarmored Defense (Monk)', 'Defesa Sem Armadura (Monge)', NULL, 'CA 10 + mod DES + mod SAB sem armadura.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000081', 'SRD_2024', 'martial-arts-2024', 'Martial Arts', 'Artes Marciais', NULL, 'Golpes desarmados letais com dano escalonado e Destreza para ataques.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000082', 'SRD_2024', 'monk-focus-2024', 'Focus Points', 'Pontos de Foco', 'Monge Nível 2', 'Pontos de energia de combate recuperados em descanso curto para manobras de artes marciais.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"BONUS_ACTION","actionName":"Foco Marcial","maxUses":2,"rechargeOn":"SHORT_REST"}'::jsonb),

    -- Wizard Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000090', 'SRD_2014', 'arcane-recovery-2014', 'Arcane Recovery', 'Recuperação Arcana', NULL, 'Uma vez por dia em descanso curto, recupera espaços de magia igual a metade do seu nível de mago.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"FREE","actionName":"Recuperação Arcana","maxUses":1,"rechargeOn":"LONG_REST"}'::jsonb),
    ('30000000-0000-0000-0000-000000000091', 'SRD_2014', 'spellbook-wizard-2014', 'Spellbook', 'Grimório de Magias', NULL, 'Permite aprender e copiar magias arcanas escritas para o seu grimório pessoal.', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000090', 'SRD_2024', 'arcane-recovery-2024', 'Arcane Recovery', 'Recuperação Arcana', NULL, 'Recuperação de espaços de magia durante descansos curtos acelerados.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"FREE","actionName":"Recuperação Arcana","maxUses":1,"rechargeOn":"LONG_REST"}'::jsonb),
    ('40000000-0000-0000-0000-000000000091', 'SRD_2024', 'spellbook-wizard-2024', 'Spellbook', 'Grimório', NULL, 'Mestre dos rituais arcanos e transcrição mágica.', 'SRD', 'CLASS', '{}'::jsonb),

    -- Warlock Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000100', 'SRD_2014', 'pact-magic-warlock-2014', 'Pact Magic', 'Magia de Pacto', NULL, 'Espaços de magia de círculo máximo recuperados a cada descanso curto.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000101', 'SRD_2014', 'eldritch-invocations-2014', 'Eldritch Invocations', 'Invocações Místicas', 'Bruxo Nível 2', 'Segredos místicos que concedem habilidades mágicas permanentes como Visão Diabólica e Rajada Repulsiva.', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000100', 'SRD_2024', 'pact-magic-warlock-2024', 'Pact Magic', 'Magia de Pacto', NULL, 'Magia de pacto com recarga total em descansos curtos.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000101', 'SRD_2024', 'eldritch-invocations-2024', 'Eldritch Invocations', 'Invocações Místicas', 'Bruxo Nível 2', 'Poderes místicos permanentes adaptáveis.', 'SRD', 'CLASS', '{}'::jsonb),

    -- Sorcerer Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000110', 'SRD_2014', 'font-of-magic-2014', 'Font of Magic', 'Fonte de Magia', 'Feiticeiro Nível 2', 'Pontos de Feitiçaria para converter em espaços de magia e abastecer Metamágica.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"BONUS_ACTION","actionName":"Converter Feitiçaria","maxUses":2,"rechargeOn":"LONG_REST"}'::jsonb),
    ('30000000-0000-0000-0000-000000000111', 'SRD_2014', 'metamagic-2014', 'Metamagic', 'Metamágica', 'Feiticeiro Nível 3', 'Modifica magias em tempo real (Magia Acelerada, Magia Sutil, Magia Gêmea).', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000110', 'SRD_2024', 'sorcery-points-2024', 'Sorcery Points', 'Pontos de Feitiçaria', 'Feiticeiro Nível 2', 'Reserva de magia pura inata para alterar a realidade e recarregar magias.', 'SRD', 'CLASS', '{"grantsAction":true,"actionType":"BONUS_ACTION","actionName":"Pontos de Feitiçaria","maxUses":2,"rechargeOn":"LONG_REST"}'::jsonb),
    ('40000000-0000-0000-0000-000000000111', 'SRD_2024', 'metamagic-2024', 'Metamagic', 'Metamágica', 'Feiticeiro Nível 3', 'Metamágica expandida com custo otimizado.', 'SRD', 'CLASS', '{}'::jsonb),

    -- Ranger Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000120', 'SRD_2014', 'favored-enemy-2014', 'Favored Enemy', 'Inimigo Favorito', NULL, 'Vantagem em rastrear e recordar informações sobre determinados tipos de criaturas.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000121', 'SRD_2014', 'natural-explorer-2014', 'Natural Explorer', 'Explorador Natural', NULL, 'Especialista em navegar por terrenos difíceis como florestas, montanhas e pântanos.', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000120', 'SRD_2024', 'deft-explorer-2024', 'Deft Explorer', 'Explorador Hábil', NULL, 'Especialização em perícia e deslocamento aprimorado em terra e água.', 'SRD', 'CLASS', '{"speedBonus":5}'::jsonb),

    -- Artificer Feats (2014 & 2024)
    ('30000000-0000-0000-0000-000000000130', 'SRD_2014', 'magical-tinkering-2014', 'Magical Tinkering', 'Engenhocas Mágicas', NULL, 'Imbui propriedades mágicas sensoriais simples em objetos mundanos minúsculos.', 'SRD', 'CLASS', '{}'::jsonb),
    ('30000000-0000-0000-0000-000000000131', 'SRD_2014', 'infuse-item-2014', 'Infuse Item', 'Infundir Item', 'Artífice Nível 2', 'Cria itens mágicos protótipos de sua lista de infusões conhecidas.', 'SRD', 'CLASS', '{}'::jsonb),

    ('40000000-0000-0000-0000-000000000130', 'SRD_2024', 'magical-tinkering-2024', 'Magical Tinkering', 'Engenhocas Mágicas', NULL, 'Criação rápida de dispositivos mágicos de utilidade.', 'SRD', 'CLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000131', 'SRD_2024', 'infuse-item-2024', 'Infuse Item', 'Infundir Item', 'Artífice Nível 2', 'Infusões mágicas aprimoradas para armaduras e armas.', 'SRD', 'CLASS', '{}'::jsonb)
ON CONFLICT (ruleset, slug) DO NOTHING;

-- 3. Link Class Progression Feats to Archetypes (archetype_feats)
INSERT INTO player_character.archetype_feats (archetype_id, feat_id, unlock_level)
VALUES
    -- Cleric (2014 & 2024)
    ('10000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000060', 1),
    ('10000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000061', 2),
    ('10000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000062', 5),
    ('10000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000063', 10),

    ('20000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000060', 1),
    ('20000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000061', 2),
    ('20000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000062', 5),
    ('20000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000063', 10),

    -- Druid (2014 & 2024)
    ('10000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000070', 1),
    ('10000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000071', 2),
    ('10000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000072', 18),

    ('20000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000070', 1),
    ('20000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000071', 2),

    -- Monk (2014 & 2024)
    ('10000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000080', 1),
    ('10000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000081', 1),
    ('10000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000082', 2),
    ('10000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000083', 2),
    ('10000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000084', 5),

    ('20000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000080', 1),
    ('20000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000081', 1),
    ('20000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000082', 2),

    -- Wizard (2014 & 2024)
    ('10000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000090', 1),
    ('10000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000091', 1),

    ('20000000-0000-0000-0000-000000000012', '40000000-0000-0000-0000-000000000090', 1),
    ('20000000-0000-0000-0000-000000000012', '40000000-0000-0000-0000-000000000091', 1),

    -- Warlock (2014 & 2024)
    ('10000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000100', 1),
    ('10000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000101', 2),

    ('20000000-0000-0000-0000-000000000011', '40000000-0000-0000-0000-000000000100', 1),
    ('20000000-0000-0000-0000-000000000011', '40000000-0000-0000-0000-000000000101', 2),

    -- Sorcerer (2014 & 2024)
    ('10000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000110', 2),
    ('10000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000111', 3),

    ('20000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000110', 2),
    ('20000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000111', 3),

    -- Ranger (2014 & 2024)
    ('10000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000120', 1),
    ('10000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000121', 1),

    ('20000000-0000-0000-0000-000000000008', '40000000-0000-0000-0000-000000000120', 1),

    -- Artificer (2014 & 2024)
    ('10000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000130', 1),
    ('10000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000131', 2),

    ('20000000-0000-0000-0000-000000000013', '40000000-0000-0000-0000-000000000130', 1),
    ('20000000-0000-0000-0000-000000000013', '40000000-0000-0000-0000-000000000131', 2)
ON CONFLICT (archetype_id, feat_id, unlock_level) DO NOTHING;

-- 4. Seed Subclass Progression Feats for Both Existing and New Subclasses
INSERT INTO player_character.feats (id, ruleset, slug, name_en, name, prerequisites, description, source, feat_type, mechanical_effects)
VALUES
    -- Battle Master: Combat Superiority
    ('30000000-0000-0000-0000-000000000200', 'SRD_2014', 'combat-superiority-2014', 'Combat Superiority', 'Superioridade em Combate', 'Mestre da Batalha Nível 3', 'Ganha quatro dados de superioridade d8 para usar em manobras especiais como Desarmar e Ripostar.', 'SRD', 'SUBCLASS', '{"grantsAction":true,"actionType":"FREE","actionName":"Manobra de Batalha","maxUses":4,"rechargeOn":"SHORT_REST"}'::jsonb),
    ('40000000-0000-0000-0000-000000000200', 'SRD_2024', 'combat-superiority-2024', 'Combat Superiority', 'Superioridade em Combate', 'Mestre da Batalha Nível 3', 'Dados de superioridade para manobras táticas com recuperação parcial ao rolar iniciativa.', 'SRD', 'SUBCLASS', '{"grantsAction":true,"actionType":"FREE","actionName":"Manobra Tática","maxUses":4,"rechargeOn":"SHORT_REST"}'::jsonb),

    -- Eldritch Knight: Weapon Bond
    ('30000000-0000-0000-0000-000000000201', 'SRD_2014', 'weapon-bond-2014', 'Weapon Bond', 'Vínculo com Arma', 'Cavaleiro Arcano Nível 3', 'Cria um elo mágico com até duas armas, não podendo ser desarmado e invocando-a para a mão como ação bônus.', 'SRD', 'SUBCLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000201', 'SRD_2024', 'weapon-bond-2024', 'Weapon Bond', 'Vínculo com Arma', 'Cavaleiro Arcano Nível 3', 'Elo mágico indissolúvel com sua arma.', 'SRD', 'SUBCLASS', '{}'::jsonb),

    -- Assassin: Assassinate
    ('30000000-0000-0000-0000-000000000202', 'SRD_2014', 'assassinate-2014', 'Assassinate', 'Assassinar', 'Assassino Nível 3', 'Vantagem nas jogadas de ataque contra qualquer criatura que ainda não tenha agido no combate. Crítico automático se surpreendida.', 'SRD', 'SUBCLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000202', 'SRD_2024', 'assassinate-2024', 'Assassinate', 'Assassinar', 'Assassino Nível 3', 'Vantagem na iniciativa e dano extra na rodada inicial de emboscada.', 'SRD', 'SUBCLASS', '{}'::jsonb),

    -- Arcane Trickster: Mage Hand Legerdemain
    ('30000000-0000-0000-0000-000000000203', 'SRD_2014', 'mage-hand-legerdemain-2014', 'Mage Hand Legerdemain', 'Prestidigitação com Mãos Mágicas', 'Trapaceiro Arcano Nível 3', 'Sua Mão Mágica torna-se invisível e pode arrombar fechaduras, desarmar armadilhas e furtar à distância.', 'SRD', 'SUBCLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000203', 'SRD_2024', 'mage-hand-legerdemain-2024', 'Mage Hand Legerdemain', 'Prestidigitação com Mãos Mágicas', 'Trapaceiro Arcano Nível 3', 'Mão Mágica invisível para trapaças furtivas.', 'SRD', 'SUBCLASS', '{}'::jsonb),

    -- Circle of the Moon: Combat Wild Shape
    ('30000000-0000-0000-0000-000000000204', 'SRD_2014', 'combat-wild-shape-2014', 'Combat Wild Shape', 'Forma Selvagem de Combate', 'Círculo da Lua Nível 2', 'Assume Forma Selvagem como Ação Bônus e pode gastar espaços de magia para recuperar PV.', 'SRD', 'SUBCLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000204', 'SRD_2024', 'combat-wild-shape-2024', 'Combat Wild Shape', 'Forma Selvagem de Combate', 'Círculo da Lua Nível 3', 'Forma Selvagem rápida com CA lunar adicional e dano radiante.', 'SRD', 'SUBCLASS', '{}'::jsonb),

    -- School of Evocation: Sculpt Spells
    ('30000000-0000-0000-0000-000000000205', 'SRD_2014', 'sculpt-spells-2014', 'Sculpt Spells', 'Esculpir Magias', 'Evocador Nível 2', 'Cria bolsões de segurança para seus aliados dentro de áreas de dano de suas magias de evocação.', 'SRD', 'SUBCLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000205', 'SRD_2024', 'sculpt-spells-2024', 'Sculpt Spells', 'Esculpir Magias', 'Evocador Nível 3', 'Aliados são imunes ao dano de suas magias de evocação em área.', 'SRD', 'SUBCLASS', '{}'::jsonb),

    -- The Fiend: Dark One's Blessing
    ('30000000-0000-0000-0000-000000000206', 'SRD_2014', 'dark-ones-blessing-2014', 'Dark One''s Blessing', 'Bênção do Obscuro', 'O Corruptor Nível 1', 'Ao reduzir um inimigo hostil a 0 PV, ganha PV Temporários iguais ao seu mod CAR + nível de bruxo.', 'SRD', 'SUBCLASS', '{}'::jsonb),
    ('40000000-0000-0000-0000-000000000206', 'SRD_2024', 'dark-ones-blessing-2024', 'Dark One''s Blessing', 'Bênção do Obscuro', 'O Corruptor Nível 3', 'PV Temporários imediatos ao abater qualquer criatura hostil.', 'SRD', 'SUBCLASS', '{}'::jsonb),

    -- Draconic Bloodline: Draconic Resilience
    ('30000000-0000-0000-0000-000000000207', 'SRD_2014', 'draconic-resilience-2014', 'Draconic Resilience', 'Resiliência Dracônica', 'Linhagem Dracônica Nível 1', 'Seus PV máximos aumentam em 1 por nível e sua CA sem armadura torna-se 13 + DES.', 'SRD', 'SUBCLASS', '{"hpPerLevelBonus":1,"acBonus":3}'::jsonb),
    ('40000000-0000-0000-0000-000000000207', 'SRD_2024', 'draconic-resilience-2024', 'Draconic Resilience', 'Resiliência Dracônica', 'Feitiçaria Dracônica Nível 3', 'Escamas dracônicas que concedem CA 13 + DES e bônus contínuo de PV.', 'SRD', 'SUBCLASS', '{"hpPerLevelBonus":1,"acBonus":3}'::jsonb)
ON CONFLICT (ruleset, slug) DO NOTHING;

-- 5. Associate Subclass Progression Feats to Subclasses (subclass_feats)
INSERT INTO player_character.subclass_feats (subclass_id, feat_id, unlock_level)
VALUES
    -- Battle Master
    ('12000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000200', 3),
    ('22000000-0000-0000-0000-000000000014', '40000000-0000-0000-0000-000000000200', 3),

    -- Eldritch Knight
    ('12000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000201', 3),
    ('22000000-0000-0000-0000-000000000015', '40000000-0000-0000-0000-000000000201', 3),

    -- Assassin
    ('12000000-0000-0000-0000-000000000017', '30000000-0000-0000-0000-000000000202', 3),
    ('22000000-0000-0000-0000-000000000017', '40000000-0000-0000-0000-000000000202', 3),

    -- Arcane Trickster
    ('12000000-0000-0000-0000-000000000018', '30000000-0000-0000-0000-000000000203', 3),
    ('22000000-0000-0000-0000-000000000018', '40000000-0000-0000-0000-000000000203', 3),

    -- Circle of the Moon
    ('12000000-0000-0000-0000-000000000029', '30000000-0000-0000-0000-000000000204', 2),
    ('22000000-0000-0000-0000-000000000029', '40000000-0000-0000-0000-000000000204', 3),

    -- School of Evocation (2014 & 2024 existing subclasses)
    ('12000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000205', 2),
    ('22000000-0000-0000-0000-000000000012', '40000000-0000-0000-0000-000000000205', 3),

    -- The Fiend (2014 & 2024 existing subclasses)
    ('12000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000206', 1),
    ('22000000-0000-0000-0000-000000000011', '40000000-0000-0000-0000-000000000206', 3),

    -- Draconic Bloodline (2014 & 2024 existing subclasses)
    ('12000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000207', 1),
    ('22000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000207', 3)
ON CONFLICT (subclass_id, feat_id, unlock_level) DO NOTHING;
