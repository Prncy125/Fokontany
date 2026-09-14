package com.example.fokontany.domain.model

data class Habitant(
    val id: Long = 0L,
    val foyerId: Long,
    val nom: String,
    val prenom: String,
    val sexe: String,
    val dateNaissance: String,
    val telephone: String? = null,
    val estRepresentant: Boolean = false,
    val actif: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)