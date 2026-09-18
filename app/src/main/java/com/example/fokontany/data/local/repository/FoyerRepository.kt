package com.example.fokontany.data.local.repository

import com.example.fokontany.data.local.dao.FoyerDao
import com.example.fokontany.data.local.dao.HabitantDao
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.relation.FoyerAvecHabitants
import com.example.fokontany.data.local.relation.HabitantAvecFoyer
import kotlinx.coroutines.flow.Flow

class FoyerRepository(
    private val foyerDao: FoyerDao,
    private val habitantDao: HabitantDao
) {

    fun observerTous(): Flow<List<FoyerEntity>> {
        return foyerDao.observerTous()
    }

    suspend fun trouverParId(id: Long): FoyerEntity? {
        return foyerDao.trouverParId(id)
    }

    suspend fun inserer(foyer: FoyerEntity): Long {
        return foyerDao.inserer(foyer)
    }

    fun observerAvecHabitants(foyerId: Long) =
        foyerDao.observerAvecHabitants(foyerId)

    suspend fun insererHabitant(habitant: HabitantEntity): Long {
        return habitantDao.inserer(habitant)
    }

    suspend fun modifierHabitant(habitant: HabitantEntity) {
        habitantDao.modifier(habitant)
    }

    suspend fun desactiverHabitant(habitantId: Long) {
        habitantDao.desactiver(habitantId)
    }

    suspend fun definirRepresentant(
        foyerId: Long,
        habitantId: Long
    ) {
        habitantDao.definirRepresentant(
            foyerId = foyerId,
            habitantId = habitantId
        )
    }

    suspend fun modifierFoyer(foyer: FoyerEntity) {
        foyerDao.modifier(foyer)
    }

    suspend fun desactiverFoyer(foyerId: Long) {
        foyerDao.desactiver(foyerId)
    }

    suspend fun activerHabitant(habitantId: Long) {
        habitantDao.activer(habitantId)
    }

    fun rechercherHabitants(recherche: String) =
        habitantDao.rechercherAvecFoyer(recherche)

    fun rechercherFoyers(recherche: String) =
        foyerDao.rechercherAvecHabitants(recherche)

    suspend fun trouverRepresentant(foyerId: Long): HabitantEntity? {
        return habitantDao.trouverRepresentant(foyerId)
    }

    suspend fun synchroniserFoyersEtHabitants() {
        foyerDao.marquerPendingCommeSynced()
        habitantDao.marquerPendingCommeSynced()
    }

    suspend fun marquerSynchronisationEnErreur() {
        foyerDao.marquerPendingCommeError()
        habitantDao.marquerPendingCommeError()
    }

    fun compterPending(): Flow<Int> {
        return foyerDao.compterPending()
    }

    fun compterErrors(): Flow<Int> {
        return foyerDao.compterErrors()
    }

    fun compterHabitantsPending(): Flow<Int> {
        return habitantDao.compterPending()
    }

    fun compterHabitantsErrors(): Flow<Int> {
        return habitantDao.compterErrors()
    }
}