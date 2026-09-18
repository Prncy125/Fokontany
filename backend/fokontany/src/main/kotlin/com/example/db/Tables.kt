package com.example.db

import org.jetbrains.exposed.sql.Table

object FoyersTable : Table("foyers") {
    val id = long("id").autoIncrement()
    val adresse = varchar("adresse", 255)
    val quartier = varchar("quartier", 255)
    val dateEnregistrement = varchar("date_enregistrement", 50)
    val actif = bool("actif").default(true)

    override val primaryKey = PrimaryKey(id)
}

object HabitantsTable : Table("habitants") {
    val id = long("id").autoIncrement()
    val foyerId = long("foyer_id").references(FoyersTable.id)
    val nom = varchar("nom", 100)
    val prenom = varchar("prenom", 100)
    val sexe = varchar("sexe", 10)
    val dateNaissance = varchar("date_naissance", 50)
    val telephone = varchar("telephone", 20).nullable()
    val codePaysTelephone = varchar("code_pays_telephone", 10).default("+261")
    val estRepresentant = bool("est_representant").default(false)
    val actif = bool("actif").default(true)

    override val primaryKey = PrimaryKey(id)
}

object ProgrammesAideTable : Table("programmes_aide") {
    val id = long("id").autoIncrement()
    val nom = varchar("nom", 255)
    val description = text("description").nullable()
    val dateDebut = varchar("date_debut", 50)

    override val primaryKey = PrimaryKey(id)
}

object DistributionsAideTable : Table("distributions_aide") {
    val id = long("id").autoIncrement()
    val programmeAideId = long("programme_aide_id").references(ProgrammesAideTable.id)
    val foyerId = long("foyer_id").references(FoyersTable.id)
    val representantHabitantId = long("representant_habitant_id").references(HabitantsTable.id)
    val dateDistribution = varchar("date_distribution", 50)
    val montant = long("montant").nullable()
    val quantite = integer("quantite").nullable()
    val statut = varchar("statut", 20).default("RECUPEREE")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex("unique_prog_foyer", programmeAideId, foyerId)
    }
}
