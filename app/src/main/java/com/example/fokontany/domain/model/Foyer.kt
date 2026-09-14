package com.example.fokontany.domain.model

data class Foyer(
    val idLocal: Long = 0L,
    val adresse: String,
    val quartier: String,
    val dateEnregistrement: String,
    val actif: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)