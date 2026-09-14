package com.example.fokontany.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.fokontany.data.local.entity.DistributionAideEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DistributionAideDao {

    @Query(
        """
        SELECT *
        FROM distributions_aide
        WHERE foyerId = :foyerId
        ORDER BY dateDistribution DESC
        """
    )
    abstract fun observerHistoriqueFoyer(
        foyerId: Long
    ): Flow<List<DistributionAideEntity>>

    @Query(
        """
        SELECT COUNT(*)
        FROM distributions_aide
        WHERE programmeAideId = :programmeAideId
        AND foyerId = :foyerId
        AND statut != 'ANNULEE'
        """
    )
    protected abstract suspend fun compterDistributionExistante(
        programmeAideId: Long,
        foyerId: Long
    ): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insererInterne(
        distribution: DistributionAideEntity
    ): Long

    @Transaction
    open suspend fun enregistrerSiAbsente(
        distribution: DistributionAideEntity
    ): Long? {

        val existe = compterDistributionExistante(
            programmeAideId = distribution.programmeAideId,
            foyerId = distribution.foyerId
        )

        if (existe > 0) {
            return null
        }

        return insererInterne(distribution)
    }
}