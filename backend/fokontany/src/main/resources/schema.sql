CREATE DATABASE IF NOT EXISTS fokontany_db;
USE fokontany_db;

CREATE TABLE IF NOT EXISTS foyers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    adresse VARCHAR(255) NOT NULL,
    quartier VARCHAR(255) NOT NULL,
    date_enregistrement VARCHAR(50) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS habitants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    foyer_id BIGINT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    sexe VARCHAR(10) NOT NULL,
    date_naissance VARCHAR(50) NOT NULL,
    telephone VARCHAR(20),
    code_pays_telephone VARCHAR(10) DEFAULT '+261',
    est_representant BOOLEAN DEFAULT FALSE,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (foyer_id) REFERENCES foyers(id)
);

CREATE TABLE IF NOT EXISTS programmes_aide (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS distributions_aide (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    programme_aide_id BIGINT NOT NULL,
    foyer_id BIGINT NOT NULL,
    representant_habitant_id BIGINT NOT NULL,
    date_distribution VARCHAR(50) NOT NULL,
    montant BIGINT,
    quantite INT,
    statut VARCHAR(20) NOT NULL DEFAULT 'RECUPEREE',
    UNIQUE KEY unique_prog_foyer (programme_aide_id, foyer_id),
    FOREIGN KEY (programme_aide_id) REFERENCES programmes_aide(id),
    FOREIGN KEY (foyer_id) REFERENCES foyers(id),
    FOREIGN KEY (representant_habitant_id) REFERENCES habitants(id)
);
