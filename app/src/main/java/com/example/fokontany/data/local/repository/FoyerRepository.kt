package com.example.fokontany.data.local.repository

import com.example.fokontany.data.local.dao.FoyerDao
import com.example.fokontany.data.local.dao.HabitantDao
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity
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
}