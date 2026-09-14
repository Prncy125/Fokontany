package com.example.fokontany.domain.model

data class ProgrammeAide(
    val id: Long = 0L,
    val nom: String,
    val description: String? = null,
    val dateDebut: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)