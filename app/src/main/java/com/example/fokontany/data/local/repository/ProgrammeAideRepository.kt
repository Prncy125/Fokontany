package com.example.fokontany.data.local.repository

import com.example.fokontany.data.local.dao.ProgrammeAideDao
import com.example.fokontany.data.local.entity.ProgrammeAideEntity
import kotlinx.coroutines.flow.Flow

class ProgrammeAideRepository(
    private val dao: ProgrammeAideDao
) {

    fun observerTous(): Flow<List<ProgrammeAideEntity>> {
        return dao.observerTous()
    }

    suspend fun trouverParId(id: Long): ProgrammeAideEntity? {
        return dao.trouverParId(id)
    }

    suspend fun inserer(programme: ProgrammeAideEntity): Long {
        return dao.inserer(programme)
    }

    suspend fun modifier(programme: ProgrammeAideEntity) {
        dao.modifier(programme)
    }
}