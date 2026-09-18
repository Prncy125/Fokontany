package com.example.fokontany.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.relation.FoyerAvecHabitants
import kotlinx.coroutines.flow.Flow

@Dao
interface FoyerDao {

    @Query(
        """
        SELECT *
        FROM foyers
        WHERE actif = 1
        ORDER BY quartier ASC, adresse ASC
        """
    )
    fun observerTous(): Flow<List<FoyerEntity>>

    @Query(
        """
        SELECT *
        FROM foyers
        WHERE idLocal = :id
        LIMIT 1
        """
    )
    suspend fun trouverParId(id: Long): FoyerEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserer(foyer: FoyerEntity): Long

    @Update
    suspend fun modifier(foyer: FoyerEntity)

    @Query(
        """
        UPDATE foyers
        SET actif = 0,
            syncStatus = 'PENDING'
        WHERE idLocal = :id
        """
    )
    suspend fun desactiver(id: Long)

    @Query(
        """
        SELECT *
        FROM foyers
        WHERE actif = 1
        AND (
            adresse LIKE '%' || :recherche || '%'
            OR quartier LIKE '%' || :recherche || '%'
        )
        ORDER BY quartier ASC, adresse ASC
        """
    )
    fun rechercher(recherche: String): Flow<List<FoyerEntity>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM foyers
        WHERE actif = 1
        AND (
            adresse LIKE '%' || :recherche || '%'
            OR quartier LIKE '%' || :recherche || '%'
        )
        ORDER BY quartier ASC, adresse ASC
        """
    )
    fun rechercherAvecHabitants(recherche: String): Flow<List<FoyerAvecHabitants>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM foyers
        WHERE idLocal = :foyerId
        LIMIT 1
        """
    )
    fun observerAvecHabitants(
        foyerId: Long
    ): Flow<FoyerAvecHabitants?>


    @Query("""
    UPDATE foyers
    SET syncStatus = 'SYNCED'
    WHERE syncStatus = 'PENDING'
    """)
    suspend fun marquerPendingCommeSynced()


    @Query("""
    UPDATE foyers
    SET syncStatus = 'ERROR'
    WHERE syncStatus = 'PENDING'
    """)
    suspend fun marquerPendingCommeError()

    @Query("SELECT COUNT(*) FROM foyers WHERE syncStatus = 'PENDING'")
    fun compterPending(): Flow<Int>

    @Query("SELECT COUNT(*) FROM foyers WHERE syncStatus = 'ERROR'")
    fun compterErrors(): Flow<Int>

}
