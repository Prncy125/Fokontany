package com.example.fokontany.domain.model

data class DistributionAide(
    val id: Long = 0L,
    val programmeAideId: Long,
    val foyerId: Long,
    val representantHabitantId: Long,
    val dateDistribution: String,
    val montant: Long? = null,
    val quantite: Int? = null,
    val statut: StatutDistribution = StatutDistribution.RECUPEREE,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)