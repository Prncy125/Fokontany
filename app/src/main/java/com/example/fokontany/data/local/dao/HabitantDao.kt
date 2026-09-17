package com.example.fokontany.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.relation.HabitantAvecFoyer
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HabitantDao {

    @Query(
        """
        SELECT *
        FROM habitants
        WHERE foyerId = :foyerId
        AND actif = 1
        ORDER BY estRepresentant DESC, nom ASC, prenom ASC
        """
    )
    abstract fun observerParFoyer(
        foyerId: Long
    ): Flow<List<HabitantEntity>>

    @Query(
        """
        SELECT *
        FROM habitants
        WHERE id = :id
        LIMIT 1
        """
    )
    abstract suspend fun trouverParId(
        id: Long
    ): HabitantEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun inserer(
        habitant: HabitantEntity
    ): Long

    @Update
    abstract suspend fun modifier(
        habitant: HabitantEntity
    )

    @Query(
        """
        UPDATE habitants
        SET actif = 0,
            estRepresentant = 0,
            syncStatus = 'PENDING'
        WHERE id = :id
        """
    )
    abstract suspend fun desactiver(id: Long)

    @Query(
        """
        SELECT *
        FROM habitants
        WHERE actif = 1
        AND (
            nom LIKE '%' || :recherche || '%'
            OR prenom LIKE '%' || :recherche || '%'
        )
        ORDER BY nom ASC, prenom ASC
        """
    )
    abstract fun rechercher(
        recherche: String
    ): Flow<List<HabitantEntity>>
    @Transaction
    @Query(
        """
        SELECT *
        FROM habitants
        WHERE actif = 1
        AND (
            nom LIKE '%' || :recherche || '%'
            OR prenom LIKE '%' || :recherche || '%'
        )
        ORDER BY nom ASC, prenom ASC
        """
    )
    abstract fun rechercherAvecFoyer(
        recherche: String
    ): Flow<List<HabitantAvecFoyer>>


    @Query(
        """
        SELECT *
        FROM habitants
        WHERE foyerId = :foyerId
        AND estRepresentant = 1
        AND actif = 1
        LIMIT 1
        """
    )
    abstract suspend fun trouverRepresentant(
        foyerId: Long
    ): HabitantEntity?

    @Query(
        """
        SELECT COUNT(*)
        FROM habitants
        WHERE id = :habitantId
        AND foyerId = :foyerId
        AND actif = 1
        """
    )

    protected abstract suspend fun appartientAuFoyer(
        foyerId: Long,
        habitantId: Long
    ): Int

    @Query(
        """
        UPDATE habitants
        SET estRepresentant = 0,
            syncStatus = 'PENDING'
        WHERE foyerId = :foyerId
        AND actif = 1
        """
    )
    protected abstract suspend fun retirerRepresentant(
        foyerId: Long
    )

    @Query(
        """
    UPDATE habitants
    SET actif = 1,
        syncStatus = 'PENDING'
    WHERE id = :id
    """
    )
    abstract suspend fun activer(id: Long)

    @Query(
        """
        UPDATE habitants
        SET estRepresentant = 1,
            syncStatus = 'PENDING'
        WHERE id = :habitantId
        AND foyerId = :foyerId
        AND actif = 1
        """
    )
    protected abstract suspend fun definirRepresentantInterne(
        foyerId: Long,
        habitantId: Long
    )

    @Transaction
    open suspend fun definirRepresentant(
        foyerId: Long,
        habitantId: Long
    ) {

        val existe = appartientAuFoyer(
            foyerId = foyerId,
            habitantId = habitantId
        )

        require(existe > 0) {
            "L'habitant n'appartient pas à ce foyer."
        }

        retirerRepresentant(foyerId)

        definirRepresentantInterne(
            foyerId = foyerId,
            habitantId = habitantId
        )
    }
}