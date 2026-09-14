package com.example.fokontany.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fokontany.data.local.entity.ProgrammeAideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgrammeAideDao {

    @Query(
        """
        SELECT *
        FROM programmes_aide
        ORDER BY dateDebut DESC
        """
    )
    fun observerTous(): Flow<List<ProgrammeAideEntity>>

    @Query(
        """
        SELECT *
        FROM programmes_aide
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun trouverParId(
        id: Long
    ): ProgrammeAideEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserer(
        programme: ProgrammeAideEntity
    ): Long

    @Update
    suspend fun modifier(
        programme: ProgrammeAideEntity
    )
}