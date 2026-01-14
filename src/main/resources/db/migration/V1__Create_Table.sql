-- ==================================================================================
-- SCRIPT V1 : CREATION DES TABLES (STRUCTURE LMD MISE A JOUR)
-- ==================================================================================

-- 1. MAQUETTE PEDAGOGIQUE
-- ==================================================================================
CREATE TABLE IF NOT EXISTS filiere (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) UNIQUE,
    libelle VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS specialite (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255),
    libelle VARCHAR(255),
    filiere_id BIGINT REFERENCES filiere(id)
);

CREATE TABLE IF NOT EXISTS maquette_semestre (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255),
    semestre VARCHAR(255) -- Enum: L_S1, L_S2...
);

CREATE TABLE IF NOT EXISTS parcours_specialite (
    specialite_id BIGINT REFERENCES specialite(id),
    maquette_id BIGINT REFERENCES maquette_semestre(id)
);

CREATE TABLE IF NOT EXISTS ue (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255),
    libelle VARCHAR(255),
    credits INT,
    coefficient DOUBLE PRECISION,
    domaine VARCHAR(255),
    maquette_id BIGINT REFERENCES maquette_semestre(id)
);

CREATE TABLE IF NOT EXISTS ec (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255),
    ue_id BIGINT REFERENCES ue(id)
);

-- 2. UTILISATEURS & SECURITE
-- ==================================================================================
CREATE TABLE IF NOT EXISTS utilisateur (
    id BIGSERIAL PRIMARY KEY,
    code_anonyme VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    role VARCHAR(255), -- Enum: ADMIN, ETUDIANT
    est_ancien BOOLEAN DEFAULT FALSE
);

-- 3. SCOLARITE (CURSUS ETUDIANT) - STRUCTURE MISE A JOUR
-- ==================================================================================

-- Niveau 1 : L'Année (Administrative)
CREATE TABLE IF NOT EXISTS inscription_annuelle (
    id BIGSERIAL PRIMARY KEY,
    annee_academique INT,
    cycle VARCHAR(255),             -- Ex: LICENCE
    decision_conseil VARCHAR(255),  -- Ex: ADMIS, REDOUBLANT
    moyenne_annuelle DOUBLE PRECISION,
    etudiant_id BIGINT REFERENCES utilisateur(id),
    specialite_id BIGINT REFERENCES specialite(id)
);

-- Niveau 2 : Le Semestre (Pédagogique) -- NOUVELLE TABLE
CREATE TABLE IF NOT EXISTS inscription_semestrielle (
    id BIGSERIAL PRIMARY KEY,
    semestre VARCHAR(255),          -- Enum: L_S1, L_S2...
    moyenne_semestre DOUBLE PRECISION,
    credits_obtenus INT,
    est_valide BOOLEAN,
    inscription_annuelle_id BIGINT REFERENCES inscription_annuelle(id)
);

-- Niveau 3 : L'UE (Résultat)
CREATE TABLE IF NOT EXISTS resultat_ue (
    id BIGSERIAL PRIMARY KEY,
    moyenne DOUBLE PRECISION,
    statut VARCHAR(255),            -- Enum: VALIDE, AJOURNE...
    type_inscription VARCHAR(255),  -- Enum: STANDARD, DETTE
    is_report_de_note BOOLEAN,
    ue_id BIGINT REFERENCES ue(id),
    inscription_semestrielle_id BIGINT REFERENCES inscription_semestrielle(id) -- Changement de la FK
);

-- Niveau 4 : L'EC (Note)
CREATE TABLE IF NOT EXISTS note_ec (
    id BIGSERIAL PRIMARY KEY,
    note DOUBLE PRECISION,
    session VARCHAR(255), -- Enum: NORMALE, RATTRAPAGE
    is_absence_justifiee BOOLEAN,
    ec_id BIGINT REFERENCES ec(id),
    resultat_ue_id BIGINT REFERENCES resultat_ue(id)
);

-- 4. ORIENTATION (MASTER)
-- ==================================================================================
CREATE TABLE IF NOT EXISTS candidature_master (
    id BIGSERIAL PRIMARY KEY,
    type_formation VARCHAR(255),
    verdict VARCHAR(255),
    etudiant_id BIGINT REFERENCES utilisateur(id),
    specialite_id BIGINT REFERENCES specialite(id)
);