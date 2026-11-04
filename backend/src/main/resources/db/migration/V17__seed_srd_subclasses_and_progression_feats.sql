-- Migration V17: Seed SRD Subclasses (2014 & 2024), update subclass_level, and seed class/subclass progression feats
-- player-character-service

CREATE SCHEMA IF NOT EXISTS player_character;

-- 1. Update subclass_level on archetypes
-- 2024 standardized at level 3
UPDATE player_character.archetypes
SET subclass_level = 3
WHERE ruleset = 'SRD_2024';

-- 2014 specific levels: Cleric (1), Sorcerer (1), Warlock (1), Druid (2), Wizard (2), others (3)
UPDATE player_character.archetypes
SET subclass_level = 1
WHERE ruleset = 'SRD_2014' AND name IN ('Cleric', 'Sorcerer', 'Warlock');

UPDATE player_character.archetypes
SET subclass_level = 2
WHERE ruleset = 'SRD_2014' AND name IN ('Druid', 'Wizard');

UPDATE player_character.archetypes
SET subclass_level = 3
WHERE ruleset = 'SRD_2014' AND name IN ('Barbarian', 'Bard', 'Fighter', 'Monk', 'Paladin', 'Ranger', 'Rogue', 'Artificer');

-- 2. Seed all 13 SRD Subclasses (SRD_2014 and SRD_2024)
-- Barbarian: Path of the Berserker / Caminho do Furioso
INSERT INTO player_character.subclasses (id, archetype_id, ruleset, slug, name_en, name, description, source)
VALUES
    ('12000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'SRD_2014', 'berserker-2014', 'Path of the Berserker', 'Caminho do Furioso', 'Para alguns bárbaros, a fúria é um meio para um fim — esse fim sendo a violência pura. O Caminho do Furioso trilha a glória da matança crua e descontrolada.', 'SRD'),
    ('22000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'SRD_2024', 'berserker-2024', 'Path of the Berserker', 'Caminho do Furioso', 'O berserker das regras de 2024 canaliza violência com golpes adicionais devastadores durante a Fúria sem sofrer exaustão severa.', 'SRD'),

-- Bard: College of Lore / Colégio do Conhecimento
    ('12000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'SRD_2014', 'college-of-lore-2014', 'College of Lore', 'Colégio do Conhecimento', 'Bardos do Colégio do Conhecimento sabem um pouco sobre quase tudo, coletando fragmentos de sabedoria em contos e lendas antigas.', 'SRD'),
    ('22000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'SRD_2024', 'college-of-lore-2024', 'College of Lore', 'Colégio do Conhecimento', 'Mestres de segredos mágicos e palavras cortantes que desestabilizam seus oponentes.', 'SRD'),

-- Cleric: Life Domain / Domínio da Vida
    ('12000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'SRD_2014', 'life-domain-2014', 'Life Domain', 'Domínio da Vida', 'O domínio da vida foca na energia positiva vibrante que sustenta todo o multiverso e cura ferimentos.', 'SRD'),
    ('22000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', 'SRD_2024', 'life-domain-2024', 'Life Domain', 'Domínio da Vida', 'Clérigos dedicados a preservar a vida através de bênçãos restauradoras supremas.', 'SRD'),

-- Druid: Circle of the Land / Círculo da Terra
    ('12000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 'SRD_2014', 'circle-of-the-land-2014', 'Circle of the Land', 'Círculo da Terra', 'Guardiões de ritos e conhecimentos antigos que mantêm conexão mística com a própria terra.', 'SRD'),
    ('22000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000004', 'SRD_2024', 'circle-of-the-land-2024', 'Circle of the Land', 'Círculo da Terra', 'Conjuradores ligados a biomas naturais, utilizando recuperação natural e magias de ambiente.', 'SRD'),

-- Fighter: Champion / Campeão
    ('12000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000005', 'SRD_2014', 'champion-2014', 'Champion', 'Campeão', 'O Campeão foca no desenvolvimento de puro poder físico levado à perfeição letal, com acertos críticos aprimorados.', 'SRD'),
    ('22000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000005', 'SRD_2024', 'champion-2024', 'Champion', 'Campeão', 'Guerreiro implacável que desfere críticos no 19-20 e ganha heroísmo em combate.', 'SRD'),

-- Monk: Way of the Open Hand / Caminho da Mão Aberta
    ('12000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000006', 'SRD_2014', 'open-hand-2014', 'Way of the Open Hand', 'Caminho da Mão Aberta', 'Mestres do combate marcial desarmado capazes de empurrar, derrubar e manipular a energia corporal de seus inimigos.', 'SRD'),
    ('22000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000006', 'SRD_2024', 'open-hand-2024', 'Warrior of the Open Hand', 'Guerreiro da Mão Aberta', 'Técnica lendária de controle de combate, cura física e golpes de palma vibratória.', 'SRD'),

-- Paladin: Oath of Devotion / Juramento de Devoção
    ('12000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000007', 'SRD_2014', 'oath-of-devotion-2014', 'Oath of Devotion', 'Juramento de Devoção', 'O arquétipo do cavaleiro de armadura brilhante, comprometido com honra, justiça e proteção dos inocentes.', 'SRD'),
    ('22000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000007', 'SRD_2024', 'oath-of-devotion-2024', 'Oath of Devotion', 'Juramento de Devoção', 'Canaliza arma sagrada e auras que protegem aliados contra encantamentos e medo.', 'SRD'),

-- Ranger: Hunter / Caçador
    ('12000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000008', 'SRD_2014', 'hunter-2014', 'Hunter', 'Caçador', 'Mestres de técnicas defensivas e ofensivas para derrotar ameaças que assolam as fronteiras selvagens.', 'SRD'),
    ('22000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000008', 'SRD_2024', 'hunter-2024', 'Hunter', 'Caçador', 'Especialista em presas de caçador com dano colateral adicional em inimigos próximos.', 'SRD'),

-- Rogue: Thief / Ladrão
    ('12000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000009', 'SRD_2014', 'thief-2014', 'Thief', 'Ladrão', 'Ladrões aprimoram habilidades furtivas e agilidade, tornando-se batedores rápidos e especialistas em infiltrar locais inacessíveis.', 'SRD'),
    ('22000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000009', 'SRD_2024', 'thief-2024', 'Thief', 'Ladrão', 'Permite usar objetos, gazuas e furtividade com extrema velocidade como ação bônus.', 'SRD'),

-- Sorcerer: Draconic Bloodline / Linhagem Dracônica
    ('12000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000010', 'SRD_2014', 'draconic-bloodline-2014', 'Draconic Bloodline', 'Linhagem Dracônica', 'Sua magia inata deriva de poder draconiano misturado ao seu sangue, concedendo escamas resistentes e afinidade elemental.', 'SRD'),
    ('22000000-0000-0000-0000-000000000010', '20000000-0000-0000-0000-000000000010', 'SRD_2024', 'draconic-sorcery-2024', 'Draconic Sorcery', 'Feitiçaria Dracônica', 'Resistência dracônica com bônus de pontos de vida e afinidade mágica elemental aprimorada.', 'SRD'),

-- Warlock: The Fiend / O Corruptor
    ('12000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000011', 'SRD_2014', 'the-fiend-2014', 'The Fiend', 'O Corruptor', 'Você fez um pacto com um íncubo, súcubo, diabo ou demônio dos Planos Inferiores, ganhando bênçãos e chamas infernais.', 'SRD'),
    ('22000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000011', 'SRD_2024', 'fiend-patron-2024', 'Fiend Patron', 'Patrono Corruptor', 'Ao abater inimigos você ganha pontos de vida temporários imediatos da bênção sombria.', 'SRD'),

-- Wizard: School of Evocation / Escola de Evocação
    ('12000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000012', 'SRD_2014', 'school-of-evocation-2014', 'School of Evocation', 'Escola de Evocação', 'Especialistas em manipular energia mágica pura para criar efeitos estrondosos e proteger aliados na área de impacto.', 'SRD'),
    ('22000000-0000-0000-0000-000000000012', '20000000-0000-0000-0000-000000000012', 'SRD_2024', 'evoker-2024', 'Evoker', 'Evocador', 'Mestres de magias explosivas com escultura de magias para poupar aliados do fogo amigo.', 'SRD'),

-- Artificer: Alchemist / Alquimista
    ('12000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000013', 'SRD_2014', 'alchemist-2014', 'Alchemist', 'Alquimista', 'Um especialista em combinar reagentes mágicos exóticos para produzir elixires experimentais e curativos potentes.', 'SRD'),
    ('22000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000013', 'SRD_2024', 'alchemist-2024', 'Alchemist', 'Alquimista', 'Artífice voltado à ciência alquímica e poções benéficas em combate.', 'SRD')
ON CONFLICT (ruleset, slug) DO UPDATE SET
    name_en = EXCLUDED.name_en,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    source = EXCLUDED.source,
    updated_at = NOW();

-- 3. Seed Core Class & Subclass Feats with mechanical_effects
-- Fighter Features (2014 & 2024)
INSERT INTO player_character.feats (id, ruleset, slug, name_en, name, feat_type, prerequisites, description, mechanical_effects, source)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'SRD_2014', 'second-wind-2014', 'Second Wind', 'Retomar o Fôlego', 'CLASS', 'Guerreiro 1', 'Você possui um poço de vigor que pode utilizar para se proteger de danos. No seu turno, você pode usar uma ação bônus para recuperar pontos de vida iguais a 1d10 + seu nível de guerreiro.', '{"actionName": "Retomar o Fôlego", "actionType": "BONUS_ACTION", "diceFormula": "1d10", "usesPerRest": 1, "restType": "SHORT"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000002', 'SRD_2014', 'action-surge-2014', 'Action Surge', 'Surto de Ação', 'CLASS', 'Guerreiro 2', 'Você pode ultrapassar seus limites normais por um instante. No seu turno, você pode realizar uma ação adicional além da sua ação regular e qualquer ação bônus.', '{"actionName": "Surto de Ação", "actionType": "ACTION", "usesPerRest": 1, "restType": "SHORT"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000003', 'SRD_2014', 'extra-attack-fighter-2014', 'Extra Attack', 'Ataque Extra', 'CLASS', 'Guerreiro 5', 'Você pode atacar duas vezes, em vez de uma, sempre que realizar a ação de Ataque no seu turno.', '{"actionName": "Ataque Extra", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000004', 'SRD_2014', 'indomitable-2014', 'Indomitable', 'Indomável', 'CLASS', 'Guerreiro 9', 'Você pode rolar novamente uma salvaguarda que tenha falhado. Se fizer isso, você deve usar o novo resultado.', '{"actionName": "Indomável", "actionType": "REACTION", "usesPerRest": 1, "restType": "LONG"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000005', 'SRD_2014', 'improved-critical-2014', 'Improved Critical', 'Crítico Aprimorado', 'SUBCLASS', 'Campeão 3', 'Suas jogadas de ataque com armas conseguem um acerto crítico em um resultado 19 ou 20 no d20.', '{"actionName": "Crítico Aprimorado (19-20)", "actionType": "SPECIAL"}'::jsonb, 'SRD'),

    ('40000000-0000-0000-0000-000000000001', 'SRD_2024', 'second-wind-2024', 'Second Wind', 'Retomar o Fôlego', 'CLASS', 'Guerreiro 1', 'Você recupera 1d10 + nível de Guerreiro de vida como ação bônus e pode usar também para testes de perícia com Vigor Tático.', '{"actionName": "Retomar o Fôlego", "actionType": "BONUS_ACTION", "diceFormula": "1d10", "usesPerRest": 2, "restType": "SHORT"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000002', 'SRD_2024', 'action-surge-2024', 'Action Surge', 'Surto de Ação', 'CLASS', 'Guerreiro 2', 'Ação extra imediata por turno, recarregável em descanso curto.', '{"actionName": "Surto de Ação", "actionType": "ACTION", "usesPerRest": 1, "restType": "SHORT"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000003', 'SRD_2024', 'extra-attack-fighter-2024', 'Extra Attack', 'Ataque Extra', 'CLASS', 'Guerreiro 5', 'Dois ataques ao realizar a ação de Ataque no seu turno.', '{"actionName": "Ataque Extra", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000004', 'SRD_2024', 'indomitable-2024', 'Indomitable', 'Indomável', 'CLASS', 'Guerreiro 9', 'Rola novamente uma salvaguarda falha com bônus igual ao nível de Guerreiro.', '{"actionName": "Indomável", "actionType": "REACTION", "usesPerRest": 1, "restType": "LONG"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000005', 'SRD_2024', 'improved-critical-2024', 'Improved Critical', 'Crítico Aprimorado', 'SUBCLASS', 'Campeão 3', 'Crítico em 19 ou 20 no d20.', '{"actionName": "Crítico Aprimorado (19-20)", "actionType": "SPECIAL"}'::jsonb, 'SRD'),

-- Barbarian Features (Rage, Unarmored Defense, Reckless Attack, Frenzy)
    ('30000000-0000-0000-0000-000000000010', 'SRD_2014', 'rage-2014', 'Rage', 'Fúria', 'CLASS', 'Bárbaro 1', 'Em batalha, você luta com ferocidade primal. No seu turno, você pode entrar em fúria como uma ação bônus, ganhando bônus de dano de fúria e resistência a dano cortante, perfurante e concussão.', '{"actionName": "Fúria", "actionType": "BONUS_ACTION", "usesPerRest": 2, "restType": "LONG"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000011', 'SRD_2014', 'unarmored-defense-barbarian-2014', 'Unarmored Defense', 'Defesa sem Armadura', 'CLASS', 'Bárbaro 1', 'Quando não estiver vestindo armadura, sua CA é 10 + mod de Destreza + mod de Constituição.', '{"actionName": "Defesa sem Armadura", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000012', 'SRD_2014', 'reckless-attack-2014', 'Reckless Attack', 'Ataque Descuidado', 'CLASS', 'Bárbaro 2', 'Você pode jogar de lado qualquer preocupação com a defesa para atacar com ferocidade desesperada, ganhando vantagem nas jogadas de ataque.', '{"actionName": "Ataque Descuidado", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000013', 'SRD_2014', 'frenzy-2014', 'Frenzy', 'Frenesi', 'SUBCLASS', 'Furioso 3', 'Você pode entrar em frenesi quando estiver em fúria, podendo desferir um ataque corpo a corpo como ação bônus em cada turno subsequente.', '{"actionName": "Frenesi", "actionType": "BONUS_ACTION"}'::jsonb, 'SRD'),

    ('40000000-0000-0000-0000-000000000010', 'SRD_2024', 'rage-2024', 'Rage', 'Fúria', 'CLASS', 'Bárbaro 1', 'Fúria revisada: dura 10 minutos e concede vantagem em salvaguardas de Força e resistências.', '{"actionName": "Fúria", "actionType": "BONUS_ACTION", "usesPerRest": 2, "restType": "LONG"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000011', 'SRD_2024', 'unarmored-defense-barbarian-2024', 'Unarmored Defense', 'Defesa sem Armadura', 'CLASS', 'Bárbaro 1', 'CA é 10 + Destreza + Constituição.', '{"actionName": "Defesa sem Armadura", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000012', 'SRD_2024', 'reckless-attack-2024', 'Reckless Attack', 'Ataque Descuidado', 'CLASS', 'Bárbaro 2', 'Ataque descuidado garantindo vantagem em ataques baseados em Força.', '{"actionName": "Ataque Descuidado", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000013', 'SRD_2024', 'frenzy-2024', 'Frenzy', 'Frenesi', 'SUBCLASS', 'Furioso 3', 'Causa dano extra igual a múltiplos de d6 de dano da arma durante a fúria.', '{"actionName": "Frenesi", "actionType": "SPECIAL", "diceFormula": "2d6"}'::jsonb, 'SRD'),

-- Rogue Features (Sneak Attack, Cunning Action, Fast Hands)
    ('30000000-0000-0000-0000-000000000020', 'SRD_2014', 'sneak-attack-2014', 'Sneak Attack', 'Ataque Furtivo', 'CLASS', 'Ladino 1', 'Uma vez por turno, você pode causar dano extra a uma criatura atingida por um ataque com arma sutil ou à distância se tiver vantagem.', '{"actionName": "Ataque Furtivo", "actionType": "SPECIAL", "diceFormula": "1d6"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000021', 'SRD_2014', 'cunning-action-2014', 'Cunning Action', 'Ação Astuta', 'CLASS', 'Ladino 2', 'Seu raciocínio rápido e agilidade permitem que você se mova e aja rapidamente. Você pode usar uma ação bônus para Correr, Desengajar ou Esconder-se.', '{"actionName": "Ação Astuta", "actionType": "BONUS_ACTION"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000022', 'SRD_2014', 'uncanny-dodge-2014', 'Uncanny Dodge', 'Esquiva Sobrenatural', 'CLASS', 'Ladino 5', 'Quando um atacante que você pode ver acerta um ataque em você, você pode usar sua reação para reduzir pela metade o dano recebido.', '{"actionName": "Esquiva Sobrenatural", "actionType": "REACTION"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000023', 'SRD_2014', 'fast-hands-2014', 'Fast Hands', 'Mãos Rápidas', 'SUBCLASS', 'Ladrão 3', 'Você pode usar a ação bônus concedida por sua Ação Astuta para fazer um teste de Destreza (Prestidigitação) ou Usar um Objeto.', '{"actionName": "Mãos Rápidas", "actionType": "BONUS_ACTION"}'::jsonb, 'SRD'),

    ('40000000-0000-0000-0000-000000000020', 'SRD_2024', 'sneak-attack-2024', 'Sneak Attack', 'Ataque Furtivo', 'CLASS', 'Ladino 1', 'Dano extra de ataque furtivo que progride por nível.', '{"actionName": "Ataque Furtivo", "actionType": "SPECIAL", "diceFormula": "1d6"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000021', 'SRD_2024', 'cunning-action-2024', 'Cunning Action', 'Ação Astuta', 'CLASS', 'Ladino 2', 'Ação bônus para Disparar, Desengajar ou Esconder-se.', '{"actionName": "Ação Astuta", "actionType": "BONUS_ACTION"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000022', 'SRD_2024', 'uncanny-dodge-2024', 'Uncanny Dodge', 'Esquiva Sobrenatural', 'CLASS', 'Ladino 5', 'Reação para reduzir dano pela metade.', '{"actionName": "Esquiva Sobrenatural", "actionType": "REACTION"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000023', 'SRD_2024', 'fast-hands-2024', 'Fast Hands', 'Mãos Rápidas', 'SUBCLASS', 'Ladrão 3', 'Usar objetos ou ferramentas com ação bônus.', '{"actionName": "Mãos Rápidas", "actionType": "BONUS_ACTION"}'::jsonb, 'SRD'),

-- Paladin Features (Divine Smite, Lay on Hands, Channel Divinity)
    ('30000000-0000-0000-0000-000000000030', 'SRD_2014', 'lay-on-hands-2014', 'Lay on Hands', 'Cura pelas Mãos', 'CLASS', 'Paladino 1', 'Seu toque abençoado pode curar ferimentos. Você tem uma reserva de poder de cura que se recupera quando você realiza um descanso longo.', '{"actionName": "Cura pelas Mãos", "actionType": "ACTION", "usesPerRest": 5, "restType": "LONG"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000031', 'SRD_2014', 'divine-smite-2014', 'Divine Smite', 'Destruição Divina', 'CLASS', 'Paladino 2', 'Ao atingir uma criatura com um ataque com arma corpo a corpo, você pode gastar um espaço de magia para causar dano radiante extra.', '{"actionName": "Destruição Divina", "actionType": "SPECIAL", "diceFormula": "2d8"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000032', 'SRD_2014', 'sacred-weapon-2014', 'Sacred Weapon', 'Arma Sagrada', 'SUBCLASS', 'Devoção 3', 'Como ação, você imbui uma arma que esteja empunhando com energia positiva, adicionando seu modificador de Carisma às jogadas de ataque.', '{"actionName": "Arma Sagrada", "actionType": "ACTION", "usesPerRest": 1, "restType": "SHORT"}'::jsonb, 'SRD'),

    ('40000000-0000-0000-0000-000000000030', 'SRD_2024', 'lay-on-hands-2024', 'Lay on Hands', 'Cura pelas Mãos', 'CLASS', 'Paladino 1', 'Reserva de cura com ação bônus nas regras de 2024.', '{"actionName": "Cura pelas Mãos", "actionType": "BONUS_ACTION", "usesPerRest": 5, "restType": "LONG"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000031', 'SRD_2024', 'divine-smite-2024', 'Divine Smite', 'Destruição Divina', 'CLASS', 'Paladino 2', 'Destruição radiante com ação bônus.', '{"actionName": "Destruição Divina", "actionType": "BONUS_ACTION", "diceFormula": "2d8"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000032', 'SRD_2024', 'sacred-weapon-2024', 'Sacred Weapon', 'Arma Sagrada', 'SUBCLASS', 'Devoção 3', 'Arma sagrada canalizada para bônus de Carisma no ataque.', '{"actionName": "Arma Sagrada", "actionType": "BONUS_ACTION", "usesPerRest": 1, "restType": "SHORT"}'::jsonb, 'SRD'),

-- Bard & Cleric Features (Bardic Inspiration, Channel Divinity: Preserve Life)
    ('30000000-0000-0000-0000-000000000040', 'SRD_2014', 'bardic-inspiration-2014', 'Bardic Inspiration', 'Inspiração Bárdica', 'CLASS', 'Bardo 1', 'Você pode inspirar outros através de palavras motivadoras ou música com ação bônus, concedendo um d6 de bônus.', '{"actionName": "Inspiração Bárdica", "actionType": "BONUS_ACTION", "diceFormula": "1d6", "usesPerRest": 3, "restType": "SHORT"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000041', 'SRD_2014', 'cutting-words-2014', 'Cutting Words', 'Palavras Cortantes', 'SUBCLASS', 'Conhecimento 3', 'Você aprende a usar sua inteligência para distrair, confundir e abalar a confiança de seus oponentes.', '{"actionName": "Palavras Cortantes", "actionType": "REACTION", "diceFormula": "1d6"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000050', 'SRD_2014', 'disciple-of-life-2014', 'Disciple of Life', 'Discípulo da Vida', 'SUBCLASS', 'Vida 1', 'Suas magias de cura são mais eficazes. Sempre que usar uma magia de nível 1 ou superior para curar pontos de vida, a criatura recupera pontos de vida adicionais iguais a 2 + o nível da magia.', '{"actionName": "Discípulo da Vida", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('30000000-0000-0000-0000-000000000051', 'SRD_2014', 'channel-divinity-preserve-life-2014', 'Channel Divinity: Preserve Life', 'Canalizar Divindade: Preservar a Vida', 'SUBCLASS', 'Vida 2', 'Você pode canalizar energia sagrada para curar criaturas gravemente feridas a até 9 metros de você.', '{"actionName": "Preservar a Vida", "actionType": "ACTION", "usesPerRest": 1, "restType": "SHORT"}'::jsonb, 'SRD'),

    ('40000000-0000-0000-0000-000000000040', 'SRD_2024', 'bardic-inspiration-2024', 'Bardic Inspiration', 'Inspiração Bárdica', 'CLASS', 'Bardo 1', 'Inspiração com dado d6 com duração de 1 hora.', '{"actionName": "Inspiração Bárdica", "actionType": "BONUS_ACTION", "diceFormula": "1d6", "usesPerRest": 3, "restType": "SHORT"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000041', 'SRD_2024', 'cutting-words-2024', 'Cutting Words', 'Palavras Cortantes', 'SUBCLASS', 'Conhecimento 3', 'Palavras cortantes usando reação.', '{"actionName": "Palavras Cortantes", "actionType": "REACTION", "diceFormula": "1d6"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000050', 'SRD_2024', 'disciple-of-life-2024', 'Disciple of Life', 'Discípulo da Vida', 'SUBCLASS', 'Vida 3', 'Cura potencializada pelo domínio da vida.', '{"actionName": "Discípulo da Vida", "actionType": "SPECIAL"}'::jsonb, 'SRD'),
    ('40000000-0000-0000-0000-000000000051', 'SRD_2024', 'channel-divinity-preserve-life-2024', 'Channel Divinity: Preserve Life', 'Canalizar Divindade: Preservar a Vida', 'SUBCLASS', 'Vida 3', 'Canalizar divindade para curar aliados.', '{"actionName": "Preservar a Vida", "actionType": "ACTION", "usesPerRest": 2, "restType": "SHORT"}'::jsonb, 'SRD')
ON CONFLICT (ruleset, slug) DO UPDATE SET
    name_en = EXCLUDED.name_en,
    name = EXCLUDED.name,
    feat_type = EXCLUDED.feat_type,
    prerequisites = EXCLUDED.prerequisites,
    description = EXCLUDED.description,
    mechanical_effects = EXCLUDED.mechanical_effects,
    source = EXCLUDED.source,
    updated_at = NOW();

-- 4. Associate feats to Archetypes (archetype_feats)
-- Fighter (2014)
INSERT INTO player_character.archetype_feats (archetype_id, feat_id, unlock_level)
VALUES
    ('10000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000001', 1), -- Second Wind L1
    ('10000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000002', 2), -- Action Surge L2
    ('10000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000003', 5), -- Extra Attack L5
    ('10000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000004', 9), -- Indomitable L9

-- Fighter (2024)
    ('20000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000001', 1),
    ('20000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000002', 2),
    ('20000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000003', 5),
    ('20000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000004', 9),

-- Barbarian (2014)
    ('10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000010', 1), -- Rage L1
    ('10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000011', 1), -- Unarmored Defense L1
    ('10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000012', 2), -- Reckless Attack L2

-- Barbarian (2024)
    ('20000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000010', 1),
    ('20000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000011', 1),
    ('20000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000012', 2),

-- Rogue (2014)
    ('10000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000020', 1), -- Sneak Attack L1
    ('10000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000021', 2), -- Cunning Action L2
    ('10000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000022', 5), -- Uncanny Dodge L5

-- Rogue (2024)
    ('20000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000020', 1),
    ('20000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000021', 2),
    ('20000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000022', 5),

-- Paladin (2014)
    ('10000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000030', 1), -- Lay on Hands L1
    ('10000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000031', 2), -- Divine Smite L2

-- Paladin (2024)
    ('20000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000030', 1),
    ('20000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000031', 2),

-- Bard (2014 & 2024)
    ('10000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000040', 1),
    ('20000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000040', 1)
ON CONFLICT (archetype_id, feat_id, unlock_level) DO NOTHING;

-- 5. Associate feats to Subclasses (subclass_feats)
INSERT INTO player_character.subclass_feats (subclass_id, feat_id, unlock_level)
VALUES
    ('12000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000005', 3), -- Champion: Improved Critical L3
    ('22000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000005', 3),

    ('12000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000013', 3), -- Berserker: Frenzy L3
    ('22000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000013', 3),

    ('12000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000023', 3), -- Thief: Fast Hands L3
    ('22000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000023', 3),

    ('12000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000032', 3), -- Devotion: Sacred Weapon L3
    ('22000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000032', 3),

    ('12000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000041', 3), -- Lore: Cutting Words L3
    ('22000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000041', 3),

    ('12000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000050', 1), -- Life Domain 2014: Disciple of Life L1
    ('12000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000051', 2), -- Life Domain 2014: Preserve Life L2
    ('22000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000050', 3), -- Life Domain 2024: L3
    ('22000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000051', 3)
ON CONFLICT (subclass_id, feat_id, unlock_level) DO NOTHING;
