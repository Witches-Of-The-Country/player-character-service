-- Migration V8: Seed SRD Spells (2014 & 2024) translated to pt-BR

-- SRD 2014 Spells
INSERT INTO player_character.spells (ruleset, slug, name_en, name, level, school, casting_time, range_area, components, duration, description, source)
VALUES 
('SRD_2014', 'guidance-2014', 'Guidance', 'Orientação', 0, 'Adivinhação', '1 ação', 'Toque', 'V, S', 'Concentração, até 1 minuto', 
$$Você toca uma criatura voluntária. Uma vez antes da magia acabar, o alvo pode rolar um d4 e adicionar o número rolado a um teste de habilidade de sua escolha.$$, 'SRD'),

('SRD_2014', 'light-2014', 'Light', 'Luz', 0, 'Evocação', '1 ação', 'Toque', 'V, M (um vaga-lume ou musgo fosforescente)', '1 hora', 
$$Você toca um objeto com até 3 metros em qualquer dimensão. Até a magia acabar, o objeto emite luz plena em um raio de 6 metros e meia-luz por mais 6 metros.$$, 'SRD'),

('SRD_2014', 'mage-hand-2014', 'Mage Hand', 'Mãos Mágicas', 0, 'Conjuração', '1 ação', '9 metros', 'V, S', '1 minuto', 
$$Uma mão espectral flutuante surge em um ponto escolhido dentro do alcance. Você pode usar a mão para manipular objetos, abrir portas destrancadas ou guardar itens.$$, 'SRD'),

('SRD_2014', 'cure-wounds-2014', 'Cure Wounds', 'Curar Ferimentos', 1, 'Evocação', '1 ação', 'Toque', 'V, S', 'Instantânea', 
$$Uma criatura tocada recupera pontos de vida iguais a 1d8 + seu modificador de habilidade de conjuração. Esta magia não surte efeito em mortos-vivos ou constructos.$$, 'SRD'),

('SRD_2014', 'magic-missile-2014', 'Magic Missile', 'Mísseis Mágicos', 1, 'Evocação', '1 ação', '36 metros', 'V, S', 'Instantânea', 
$$Você cria três dardos brilhantes de energia mágica. Cada dardo atinge uma criatura de sua escolha à vista, causando 1d4 + 1 de dano de energia sem necessidade de jogada de ataque.$$, 'SRD'),

('SRD_2014', 'shield-2014', 'Shield', 'Escudo Arcano', 1, 'Abjuração', '1 reação', 'Pessoal', 'V, S', '1 rodada', 
$$Uma barreira invisível de força mágica surge para protegê-lo. Até o início do seu próximo turno, você ganha +5 de bônus na CA e não sofre dano de Mísseis Mágicos.$$, 'SRD'),

('SRD_2014', 'fireball-2014', 'Fireball', 'Bola de Fogo', 3, 'Evocação', '1 ação', '45 metros', 'V, S, M (uma bolinha de enxofre e guano de morcego)', 'Instantânea', 
$$Uma labareda brilhante surge do seu dedo indicador até o ponto escolhido dentro do alcance e explode em chamas. Cada criatura em uma esfera de 6 metros de raio deve fazer uma salvaguarda de Destreza, sofrendo 8d6 de dano de fogo se falhar ou metade se obtiver sucesso.$$, 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;

-- SRD 2024 Spells
INSERT INTO player_character.spells (ruleset, slug, name_en, name, level, school, casting_time, range_area, components, duration, description, source)
VALUES 
('SRD_2024', 'guidance-2024', 'Guidance', 'Orientação', 0, 'Adivinhação', '1 ação', 'Toque', 'V, S', 'Concentração, até 1 minuto', 
$$Você canaliza insight divino para uma criatura tocada. Uma vez antes da magia encerrar, a criatura pode adicionar um d4 ao resultado de um teste de perícia à sua escolha.$$, 'SRD'),

('SRD_2024', 'light-2024', 'Light', 'Luz', 0, 'Evocação', '1 ação', 'Toque', 'V, M (um vaga-lume)', '1 hora', 
$$Você infunde um objeto com luminescência radiante. O objeto projeta luz brilhante num raio de 6 metros e penumbra por mais 6 metros.$$, 'SRD'),

('SRD_2024', 'cure-wounds-2024', 'Cure Wounds', 'Curar Ferimentos', 1, 'Abjuração', '1 ação', 'Toque', 'V, S', 'Instantânea', 
$$Uma onda de energia restauradora cura a criatura tocada em 2d8 + seu modificador de conjuração (revisão de 2024).$$, 'SRD'),

('SRD_2024', 'magic-missile-2024', 'Magic Missile', 'Mísseis Mágicos', 1, 'Evocação', '1 ação', '36 metros', 'V, S', 'Instantânea', 
$$Você conjura três dardos cintilantes de energia mágica que atingem alvos visíveis dentro do alcance infalivelmente, infligindo 1d4 + 1 de dano de energia cada.$$, 'SRD'),

('SRD_2024', 'healing-word-2024', 'Healing Word', 'Palavra Curativa', 1, 'Abjuração', '1 ação bônus', '18 metros', 'V', 'Instantânea', 
$$Com uma palavra de encorajamento rápida, uma criatura à vista recupera 2d4 + seu modificador de habilidade de conjuração.$$, 'SRD'),

('SRD_2024', 'fireball-2024', 'Fireball', 'Bola de Fogo', 3, 'Evocação', '1 ação', '45 metros', 'V, S, M (uma esfera de enxofre)', 'Instantânea', 
$$Um raio de poder ígneo detona num raio de 6 metros causando 8d6 de dano de fogo em salvaguarda de Destreza.$$, 'SRD')
ON CONFLICT (ruleset, slug) DO NOTHING;
