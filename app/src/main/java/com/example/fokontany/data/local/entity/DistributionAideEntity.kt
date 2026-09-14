package com.example.fokontany.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fokontany.domain.model.StatutDistribution
import com.example.fokontany.domain.model.SyncStatus

@Entity(
    tableName = "distributions_aide",

    foreignKeys = [

        ForeignKey(
            entity = ProgrammeAideEntity::class,
            parentColumns = ["id"],
            childColumns = ["programmeAideId"],
            onDelete = ForeignKey.NO_ACTION
        ),

        ForeignKey(
            entity = FoyerEntity::class,
            parentColumns = ["idLocal"],
            childColumns = ["foyerId"],
            onDelete = ForeignKey.NO_ACTION
        ),

        ForeignKey(
            entity = HabitantEntity::class,
            parentColumns = ["id"],
            childColumns = ["representantHabitantId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],

    indices = [

        Index(value = ["programmeAideId"]),

        Index(value = ["foyerId"]),

        Index(value = ["representantHabitantId"]),

        // Un foyer ne peut recevoir qu'une seule fois
        // le même programme d'aide.
        Index(
            value = ["programmeAideId", "foyerId"],
            unique = true
        )
    ]
)
data class DistributionAideEntity(

    @PrimaryKey(autoGenerate = true)
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