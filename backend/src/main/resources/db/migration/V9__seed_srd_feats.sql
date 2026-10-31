-- Migration V9: Seed SRD Feats (2014 & 2024) translated to pt-BR

-- SRD 2014 Feats
INSERT INTO player_character.feats (ruleset, slug, name_en, name, prerequisites, description, source)
VALUES 
('SRD_2014', 'grappler-2014', 'Grappler', 'Imobilizador', 'Força 13 ou superior', 
$$Você desenvolveu as habilidades necessárias para se agarrar com eficácia no combate corpo a corpo. Você tem vantagem em jogadas de ataque contra uma criatura agarrada por você e pode tentar imobilizá-la usando uma ação.$$, 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;

-- SRD 2024 Feats
INSERT INTO player_character.feats (ruleset, slug, name_en, name, prerequisites, description, source)
VALUES 
('SRD_2024', 'alert-2024', 'Alert', 'Alerta', 'Nível 1 (Talento de Origem)', 
$$Você ganha proficiência em Iniciativa e pode trocar sua rolagem de iniciativa com a de um aliado voluntário no início do combate.$$, 'SRD'),

('SRD_2024', 'magic-initiate-2024', 'Magic Initiate', 'Iniciado em Magia', 'Nível 1 (Talento de Origem)', 
$$Você aprende dois truques e uma magia de 1º círculo de uma lista de classes específica (Clérigo, Druida ou Mago), podendo conjurá-la uma vez por descanso longo sem gastar espaços de magia.$$, 'SRD'),

('SRD_2024', 'tough-2024', 'Tough', 'Vigoroso', 'Nível 1 (Talento de Origem)', 
$$Seus pontos de vida máximos aumentam em uma quantia igual a duas vezes o seu nível de personagem, e aumentam em 2 PV adicionais a cada nível subsequente.$$, 'SRD'),

('SRD_2024', 'savage-attacker-2024', 'Savage Attacker', 'Atacante Selvagem', 'Nível 1 (Talento de Origem)', 
$$Uma vez por turno, ao atingir um alvo com uma arma, você pode rolar o dado de dano da arma duas vezes e utilizar o maior resultado.$$, 'SRD'),

('SRD_2024', 'war-caster-2024', 'War Caster', 'Conjurador de Batalha', 'Nível 4, capacidade de conjurar pelo menos uma magia', 
$$Você tem vantagem em salvaguardas de Constituição para manter concentração, pode realizar os componentes somáticos empunhando armas e pode conjurar magias como ataque de oportunidade.$$, 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;
