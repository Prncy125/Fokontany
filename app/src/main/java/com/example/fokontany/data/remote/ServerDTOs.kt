package com.example.fokontany.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FoyerDTO(
    val id: Long = 0L,
    val adresse: String,
    val quartier: String,
    val dateEnregistrement: String,
    val actif: Boolean = true
)

@Serializable
data class FoyerDetailDTO(
    val foyer: FoyerDTO,
    val habitants: List<HabitantDTO>
)

@Serializable
data class HabitantDTO(
    val id: Long = 0L,
    val foyerId: Long,
    val nom: String,
    val prenom: String,
    val sexe: String,
    val dateNaissance: String,
    val telephone: String? = null,
    val codePaysTelephone: String = "+261",
    val estRepresentant: Boolean = false,
    val actif: Boolean = true
)

@Serializable
data class ProgrammeAideDTO(
    val id: Long = 0L,
    val nom: String,
    val description: String? = null,
    val dateDebut: String
)

@Serializable
data class DistributionAideDTO(
    val id: Long = 0L,
    val programmeAideId: Long,
    val foyerId: Long,
    val representantHabitantId: Long,
    val dateDistribution: String,
    val montant: Long? = null,
    val quantite: Int? = null,
    val statut: String = "RECUPEREE"
)
