-- INSERTION DES FILIERES
INSERT INTO filiere (code, libelle) VALUES 
('INFO', 'Informatique'),
('MATH', 'Mathématiques'),
('PC', 'Physique-Chimie'),
('HYDRO', 'Hydrosciences')
ON CONFLICT (code) DO NOTHING;

-- INSERTION DES SPECIALITES
INSERT INTO specialite (code, libelle, filiere_id) VALUES 
('L_INFO', 'Licence Informatique (Tronc Commun)', (SELECT id FROM filiere WHERE code = 'INFO')),
('L3_GL', 'Licence 3 - Génie Logiciel', (SELECT id FROM filiere WHERE code = 'INFO')),
('L3_RT', 'Licence 3 - Réseaux et Télécoms', (SELECT id FROM filiere WHERE code = 'INFO'));

-- INSERTION DES MAQUETTES SEMESTRE
INSERT INTO maquette_semestre (libelle, semestre) VALUES 
('Semestre 1 - Tronc Commun', 'L_S1'),
('Semestre 2 - Tronc Commun', 'L_S2'),
('Semestre 3 - Tronc Commun', 'L_S3'),
('Semestre 4 - Tronc Commun', 'L_S4'),
('Semestre 5 - Génie Logiciel', 'L_S5'),
('Semestre 6 - Génie Logiciel', 'L_S6'),
('Semestre 5 - Réseaux & Télécoms', 'L_S5'),
('Semestre 6 - Réseaux & Télécoms', 'L_S6');

-- LIAISON PARCOURS (TRONC COMMUN)
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'L_INFO' AND m.semestre IN ('L_S1', 'L_S2', 'L_S3', 'L_S4');

-- LIAISON GL
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'L3_GL' AND m.libelle LIKE '%Génie Logiciel%';

-- LIAISON RT
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'L3_RT' AND m.libelle LIKE '%Réseaux%';

-- CONTENU L1 - S1
-- INF111
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF111', 'Mathématiques', 8, 3.0, 'Mathématiques', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 1 - Tronc Commun'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Analyse 1', (SELECT id FROM ue WHERE code = 'INF111')),
('Algèbre 1', (SELECT id FROM ue WHERE code = 'INF111'));

-- INF112
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF112', 'Physique', 8, 3.0, 'Physique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 1 - Tronc Commun'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Fondamentaux de physique', (SELECT id FROM ue WHERE code = 'INF112')),
('Électricité', (SELECT id FROM ue WHERE code = 'INF112'));

-- INF113
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF113', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 1 - Tronc Commun'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Algorithmique et programmation 1', (SELECT id FROM ue WHERE code = 'INF113')),
('Introduction aux systèmes d''exploitation', (SELECT id FROM ue WHERE code = 'INF113'));

-- INF114
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF114', 'Humanités et entreprises', 6, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 1 - Tronc Commun'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Anglais 1', (SELECT id FROM ue WHERE code = 'INF114')),
('Recherche documentaire', (SELECT id FROM ue WHERE code = 'INF114'));

-- CONTENU L1 - S2
-- INF121
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF121', 'Mathématiques', 8, 3.0, 'Mathématiques', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 2 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Analyse 2', (SELECT id FROM ue WHERE code='INF121')), ('Algèbre 2', (SELECT id FROM ue WHERE code='INF121'));

-- INF122
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF122', 'Physique', 8, 3.0, 'Physique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 2 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Ondes et Propagation', (SELECT id FROM ue WHERE code='INF122')), ('Électronique', (SELECT id FROM ue WHERE code='INF122'));

-- INF123
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF123', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 2 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Algorithmique et Programmation 2', (SELECT id FROM ue WHERE code='INF123')), ('Architecture des ordinateurs', (SELECT id FROM ue WHERE code='INF123'));

-- INF124
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF124', 'Humanités et entreprises', 6, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 2 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Anglais 2', (SELECT id FROM ue WHERE code='INF124')), ('Technique de communications', (SELECT id FROM ue WHERE code='INF124'));

-- CONTENU L2 - S3
-- INF231
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF231', 'Mathématiques', 6, 2.0, 'Mathématiques', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 3 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Probabilités et Statistiques', (SELECT id FROM ue WHERE code='INF231')), ('Calcul Numérique', (SELECT id FROM ue WHERE code='INF231'));

-- INF232
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF232', 'Réseaux et Systèmes', 6, 2.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 3 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Introduction aux réseaux', (SELECT id FROM ue WHERE code='INF232')), ('Systèmes d''Exploitation', (SELECT id FROM ue WHERE code='INF232'));

-- INF233
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF233', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 3 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Algorithmique et Structures de données', (SELECT id FROM ue WHERE code='INF233')), ('Développement web 1', (SELECT id FROM ue WHERE code='INF233'));

-- INF234
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF234', 'Systèmes d''Information', 6, 2.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 3 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Analyse et Conception des SI', (SELECT id FROM ue WHERE code='INF234')), ('Intro BD Relationnelles', (SELECT id FROM ue WHERE code='INF234'));

-- INF235
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF235', 'Humanités et entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 3 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Projet Personnel Professionnel', (SELECT id FROM ue WHERE code='INF235')), ('Anglais 3', (SELECT id FROM ue WHERE code='INF235'));

-- CONTENU L2 - S4
-- INF241
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF241', 'Réseaux et Sécurité', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 4 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Introduction à la sécurité', (SELECT id FROM ue WHERE code='INF241')), ('Réseaux locaux', (SELECT id FROM ue WHERE code='INF241'));

-- INF242
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF242', 'Programmation', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 4 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Programmation Orientée Objet 1', (SELECT id FROM ue WHERE code='INF242')), ('Analyse et Conception OO', (SELECT id FROM ue WHERE code='INF242'));

-- INF243
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF243', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 4 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Technologies XML', (SELECT id FROM ue WHERE code='INF243')), ('Développement web 2', (SELECT id FROM ue WHERE code='INF243'));

-- INF244
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF244', 'Humanités et entreprises', 6, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 4 - Tronc Commun'));
INSERT INTO ec (libelle, ue_id) VALUES ('Gestion de projets', (SELECT id FROM ue WHERE code='INF244')), ('Leadership et dev. personnel', (SELECT id FROM ue WHERE code='INF244')), ('Anglais 4', (SELECT id FROM ue WHERE code='INF244'));

-- CONTENU L3 GL - S5
-- INF351
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF351', 'Programmation', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Programmation des mobiles', (SELECT id FROM ue WHERE code='INF351' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel'))), ('Programmation Orientée Objet 2', (SELECT id FROM ue WHERE code='INF351' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel')));

-- INF352
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF352', 'Génie Logiciel', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Développement Web Avancée', (SELECT id FROM ue WHERE code='INF352' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel'))), ('Introduction aux Génies logiciels', (SELECT id FROM ue WHERE code='INF352' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel')));

-- INF353
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF353', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Bases de données avancées', (SELECT id FROM ue WHERE code='INF353' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel'))), ('Programmation fonctionnelle', (SELECT id FROM ue WHERE code='INF353' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel')));

-- INF354
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF354', 'Humanités et entreprises', 6, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Anglais 5', (SELECT id FROM ue WHERE code='INF354' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel'))), ('Création d''entreprises', (SELECT id FROM ue WHERE code='INF354' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Génie Logiciel')));

-- CONTENU L3 GL - S6
-- INF361
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF361', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 6 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Dév. d''Applications Distribuées', (SELECT id FROM ue WHERE code='INF361' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Génie Logiciel'))), ('Langage Automate et Compilation', (SELECT id FROM ue WHERE code='INF361' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Génie Logiciel')));

-- INF362
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF362', 'Humanités et entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 6 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Rech. doc. et Rédac. scientifique', (SELECT id FROM ue WHERE code='INF362' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Génie Logiciel'))), ('Anglais 6', (SELECT id FROM ue WHERE code='INF362' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Génie Logiciel')));

-- INF363
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF363', 'Stage', 18, 6.0, 'Stage', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 6 - Génie Logiciel'));
INSERT INTO ec (libelle, ue_id) VALUES ('Stage ou Projet Opérationnel', (SELECT id FROM ue WHERE code='INF363' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Génie Logiciel')));


-- CONTENU L3 RT - S5
-- INF351
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF351', 'Réseaux et telecoms', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Réseaux sans-fil', (SELECT id FROM ue WHERE code='INF351' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms'))), ('Signaux et systèmes analogiques', (SELECT id FROM ue WHERE code='INF351' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms')));

-- INF352
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF352', 'Informatique', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Programmation des mobiles', (SELECT id FROM ue WHERE code='INF352' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms'))), ('Introduction à l''IoT', (SELECT id FROM ue WHERE code='INF352' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms')));

-- INF353
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF353', 'Réseaux et Systèmes', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Administration réseaux et systèmes', (SELECT id FROM ue WHERE code='INF353' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms'))), ('Sécurité des réseaux', (SELECT id FROM ue WHERE code='INF353' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms')));

-- INF354
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF354', 'Humanités et entreprises', 6, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 5 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Anglais 5', (SELECT id FROM ue WHERE code='INF354' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms'))), ('Création d''entreprise', (SELECT id FROM ue WHERE code='INF354' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 5 - Réseaux & Télécoms')));

-- CONTENU L3 RT - S6
-- INF361
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF361', 'Réseaux et Systèmes', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 6 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Maintenance Informatique', (SELECT id FROM ue WHERE code='INF361' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Réseaux & Télécoms'))), ('Services réseaux', (SELECT id FROM ue WHERE code='INF361' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Réseaux & Télécoms')));

-- INF362
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF362', 'Humanités et Entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 6 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Rech. doc. et Rédac. scientifique', (SELECT id FROM ue WHERE code='INF362' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Réseaux & Télécoms'))), ('Anglais 6', (SELECT id FROM ue WHERE code='INF362' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Réseaux & Télécoms')));

-- INF363
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF363', 'Stage', 18, 6.0, 'Stage', (SELECT id FROM maquette_semestre WHERE libelle = 'Semestre 6 - Réseaux & Télécoms'));
INSERT INTO ec (libelle, ue_id) VALUES ('Stage ou Projet Opérationnel', (SELECT id FROM ue WHERE code='INF363' AND maquette_id=(SELECT id FROM maquette_semestre WHERE libelle='Semestre 6 - Réseaux & Télécoms')));