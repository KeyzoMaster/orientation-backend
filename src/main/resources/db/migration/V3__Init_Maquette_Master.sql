-- ==================================================================================
-- SCRIPT V2 : INITIALISATION MASTER (POSTGRESQL & JPA COMPLIANT)
-- ==================================================================================

-- 1. INSERTION DES SPECIALITES MASTER
-- ==================================================================================
INSERT INTO specialite (code, libelle, filiere_id) VALUES 
('M_GL', 'Master Génie Logiciel', (SELECT id FROM filiere WHERE code = 'INFO')),
('M_RT', 'Master Réseaux et Télécommunications', (SELECT id FROM filiere WHERE code = 'INFO'))
ON CONFLICT DO NOTHING;

-- 2. INSERTION DES MAQUETTES SEMESTRE (MASTER)
-- ==================================================================================
-- Enum Semestre: M_S1, M_S2, M_S3, M_S4
INSERT INTO maquette_semestre (libelle, semestre) VALUES 
('Master 1 - Semestre 1 (Tronc Commun)', 'M_S1'),
('Master 1 - Semestre 2 (Génie Logiciel)', 'M_S2'),
('Master 1 - Semestre 2 (Réseaux & Télécoms)', 'M_S2'),
('Master 2 - Semestre 3 (Génie Logiciel)', 'M_S3'),
('Master 2 - Semestre 3 (Réseaux & Télécoms)', 'M_S3'),
('Master 2 - Semestre 4 (Génie Logiciel)', 'M_S4'),
('Master 2 - Semestre 4 (Réseaux & Télécoms)', 'M_S4');

-- 3. LIAISON SPECIALITES <-> MAQUETTES
-- ==================================================================================

-- M1 S1 (Tronc Commun pour GL et RT)
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code IN ('M_GL', 'M_RT') AND m.libelle = 'Master 1 - Semestre 1 (Tronc Commun)';

-- M1 S2 GL
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'M_GL' AND m.libelle = 'Master 1 - Semestre 2 (Génie Logiciel)';

-- M1 S2 RT
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'M_RT' AND m.libelle = 'Master 1 - Semestre 2 (Réseaux & Télécoms)';

-- M2 S3 GL
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'M_GL' AND m.libelle = 'Master 2 - Semestre 3 (Génie Logiciel)';

-- M2 S3 RT
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'M_RT' AND m.libelle = 'Master 2 - Semestre 3 (Réseaux & Télécoms)';

-- M2 S4 GL
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'M_GL' AND m.libelle = 'Master 2 - Semestre 4 (Génie Logiciel)';

-- M2 S4 RT
INSERT INTO parcours_specialite (specialite_id, maquette_id)
SELECT s.id, m.id FROM specialite s, maquette_semestre m
WHERE s.code = 'M_RT' AND m.libelle = 'Master 2 - Semestre 4 (Réseaux & Télécoms)';


-- ==================================================================================
-- 4. CONTENU PEDAGOGIQUE - MASTER 1 SEMESTRE 1 (TRONC COMMUN)
-- ==================================================================================

-- INF 411
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF411', 'Mathématiques', 6, 2.0, 'Mathématiques', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 1 (Tronc Commun)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Probabilités et Statistiques', (SELECT id FROM ue WHERE code = 'INF411')),
('Algorithme des Graphes', (SELECT id FROM ue WHERE code = 'INF411'));

-- INF 412
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF412', 'Systèmes et Sécurité', 6, 2.0, 'Sécurité', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 1 (Tronc Commun)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Outils Cryptographiques', (SELECT id FROM ue WHERE code = 'INF412')),
('Administration réseaux et systèmes', (SELECT id FROM ue WHERE code = 'INF412'));

-- INF 413
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF413', 'Informatique', 6, 2.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 1 (Tronc Commun)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Systèmes d''information', (SELECT id FROM ue WHERE code = 'INF413')),
('Architecture TCP/IP', (SELECT id FROM ue WHERE code = 'INF413'));

-- INF 414
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF414', 'Web et IA', 6, 2.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 1 (Tronc Commun)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Services web', (SELECT id FROM ue WHERE code = 'INF414')),
('Introduction à l''IA', (SELECT id FROM ue WHERE code = 'INF414'));

-- INF 415
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF415', 'Humanités et entreprises', 6, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 1 (Tronc Commun)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Anglais 1', (SELECT id FROM ue WHERE code = 'INF415')),
('Droit des TICS', (SELECT id FROM ue WHERE code = 'INF415')),
('Techniques de Communication', (SELECT id FROM ue WHERE code = 'INF415'));


-- ==================================================================================
-- 5. CONTENU PEDAGOGIQUE - MASTER 1 SEMESTRE 2 (OPTION GL)
-- ==================================================================================

-- INF 421 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF421_GL', 'Bases de données', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Bases de données nouvelle génération', (SELECT id FROM ue WHERE code = 'INF421_GL')),
('Administration des Bases de données', (SELECT id FROM ue WHERE code = 'INF421_GL'));

-- INF 422 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF422_GL', 'Génie logiciel', 6, 2.0, 'Génie Logiciel', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Génie et architecture des logiciels', (SELECT id FROM ue WHERE code = 'INF422_GL')),
('Mesure et qualité logicielle', (SELECT id FROM ue WHERE code = 'INF422_GL'));

-- INF 423 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF423_GL', 'Systèmes', 6, 2.0, 'Systèmes', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Cloud et Virtualisation', (SELECT id FROM ue WHERE code = 'INF423_GL')),
('Développement d''applications multi-tiers', (SELECT id FROM ue WHERE code = 'INF423_GL'));

-- INF 424 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF424_GL', 'Sécurité et IA', 6, 2.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Sécurité logicielle', (SELECT id FROM ue WHERE code = 'INF424_GL')),
('Fouille de données', (SELECT id FROM ue WHERE code = 'INF424_GL'));

-- INF 425 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF425_GL', 'Humanités et entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Anglais 2', (SELECT id FROM ue WHERE code = 'INF425_GL')),
('Méthodologies de la recherche scientifique', (SELECT id FROM ue WHERE code = 'INF425_GL'));


-- ==================================================================================
-- 6. CONTENU PEDAGOGIQUE - MASTER 1 SEMESTRE 2 (OPTION RT)
-- ==================================================================================

-- INF 421 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF421_RT', 'Réseaux et Sécurité', 8, 3.0, 'Réseaux', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Services réseaux', (SELECT id FROM ue WHERE code = 'INF421_RT')),
('Réseaux et sécurité', (SELECT id FROM ue WHERE code = 'INF421_RT'));

-- INF 422 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF422_RT', 'Réseaux', 8, 3.0, 'Réseaux', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Qualité de service dans les réseaux', (SELECT id FROM ue WHERE code = 'INF422_RT')),
('Réseaux sans-fil avancés', (SELECT id FROM ue WHERE code = 'INF422_RT'));

-- INF 423 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF423_RT', 'Télécommunication', 6, 2.0, 'Télécoms', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Transmissions analogiques', (SELECT id FROM ue WHERE code = 'INF423_RT')),
('Architecture des réseaux mobiles', (SELECT id FROM ue WHERE code = 'INF423_RT'));

-- INF 424 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF424_RT', 'Veille technologique', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Module complémentaire', (SELECT id FROM ue WHERE code = 'INF424_RT'));

-- INF 425 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF425_RT', 'Humanités et entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 1 - Semestre 2 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Anglais 2', (SELECT id FROM ue WHERE code = 'INF425_RT')),
('Méthodologies de la recherche scientifique', (SELECT id FROM ue WHERE code = 'INF425_RT'));


-- ==================================================================================
-- 7. CONTENU PEDAGOGIQUE - MASTER 2 SEMESTRE 3 (OPTION GL)
-- ==================================================================================

-- INF 531 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF531_GL', 'Génie logiciel', 6, 2.0, 'Génie Logiciel', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Technologies mobiles', (SELECT id FROM ue WHERE code = 'INF531_GL')),
('Programmation avancée', (SELECT id FROM ue WHERE code = 'INF531_GL'));

-- INF 532 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF532_GL', 'Gestion de données', 8, 3.0, 'Informatique', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Entrepôt de données (DatawareHouse)', (SELECT id FROM ue WHERE code = 'INF532_GL')),
('Web sémantique', (SELECT id FROM ue WHERE code = 'INF532_GL'));

-- INF 533 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF533_GL', 'Développement', 6, 2.0, 'Génie Logiciel', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Compilation', (SELECT id FROM ue WHERE code = 'INF533_GL')),
('Systèmes distribués et multitiers', (SELECT id FROM ue WHERE code = 'INF533_GL'));

-- INF 534 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF534_GL', 'Intelligence Artificielle', 6, 2.0, 'IA', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Analyse et traitement de données massives', (SELECT id FROM ue WHERE code = 'INF534_GL')),
('Introduction aux systèmes complexes', (SELECT id FROM ue WHERE code = 'INF534_GL'));

-- INF 535 GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF535_GL', 'Humanités et entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Gestion de projets Informatiques', (SELECT id FROM ue WHERE code = 'INF535_GL')),
('Management des entreprises', (SELECT id FROM ue WHERE code = 'INF535_GL'));


-- ==================================================================================
-- 8. CONTENU PEDAGOGIQUE - MASTER 2 SEMESTRE 3 (OPTION RT)
-- ==================================================================================

-- INF 531 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF531_RT', 'Sécurité et ingénierie', 8, 3.0, 'Sécurité', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Audit et Sécurité des réseaux', (SELECT id FROM ue WHERE code = 'INF531_RT')),
('Ingénierie de trafic', (SELECT id FROM ue WHERE code = 'INF531_RT'));

-- INF 532 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF532_RT', 'Réseaux', 6, 2.0, 'Réseaux', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Services IPv6', (SELECT id FROM ue WHERE code = 'INF532_RT')),
('Internet des Objets', (SELECT id FROM ue WHERE code = 'INF532_RT'));

-- INF 533 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF533_RT', 'Télécommunications', 6, 2.0, 'Télécoms', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Transmissions numériques', (SELECT id FROM ue WHERE code = 'INF533_RT')),
('Dimensionnement et planification des réseaux mobiles', (SELECT id FROM ue WHERE code = 'INF533_RT'));

-- INF 534 RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF534_RT', 'Virtualisation et Cloud', 6, 2.0, 'Systèmes', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Virtualisation des infrastructures et des réseaux', (SELECT id FROM ue WHERE code = 'INF534_RT')),
('Virtualisation et stockage', (SELECT id FROM ue WHERE code = 'INF534_RT'));

-- INF 535 RT (Même contenu que GL)
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF535_RT', 'Humanités et entreprises', 4, 2.0, 'Transversal', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 3 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Gestion de projets Informatiques', (SELECT id FROM ue WHERE code = 'INF535_RT')),
('Management des entreprises', (SELECT id FROM ue WHERE code = 'INF535_RT'));


-- ==================================================================================
-- 9. CONTENU PEDAGOGIQUE - MASTER 2 SEMESTRE 4 (STAGE - COMMUN GL & RT)
-- ==================================================================================

-- Pour GL
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF541_GL', 'Mémoire et Stage', 30, 15.0, 'Stage', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 4 (Génie Logiciel)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Mémoire et Stage', (SELECT id FROM ue WHERE code = 'INF541_GL'));

-- Pour RT
INSERT INTO ue (code, libelle, credits, coefficient, domaine, maquette_id) 
VALUES ('INF541_RT', 'Mémoire et Stage', 30, 15.0, 'Stage', (SELECT id FROM maquette_semestre WHERE libelle = 'Master 2 - Semestre 4 (Réseaux & Télécoms)'));

INSERT INTO ec (libelle, ue_id) VALUES 
('Mémoire et Stage', (SELECT id FROM ue WHERE code = 'INF541_RT'));