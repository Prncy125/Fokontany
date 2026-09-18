package com.example.fokontany.data.local.repository

import com.example.fokontany.data.local.dao.DistributionAideDao
import com.example.fokontany.data.local.entity.DistributionAideEntity
import kotlinx.coroutines.flow.Flow

class DistributionAideRepository(
    private val dao: DistributionAideDao
) {

    fun observerTous(): Flow<List<DistributionAideEntity>> {
        return dao.observerTous()
    }

    fun observerHistoriqueFoyer(foyerId: Long): Flow<List<DistributionAideEntity>> {
        return dao.observerHistoriqueFoyer(foyerId)
    }

    suspend fun enregistrerSiAbsente(distribution: DistributionAideEntity): Long? {
        return dao.enregistrerSiAbsente(distribution)
    }

    suspend fun synchroniserDistributions() {
        dao.marquerPendingCommeSynced()
    }

    suspend fun marquerSynchronisationEnErreur() {
        dao.marquerPendingCommeError()
    }

    fun compterPending(): Flow<Int> {
        return dao.compterPending()
    }

    fun compterErrors(): Flow<Int> {
        return dao.compterErrors()
    }
}