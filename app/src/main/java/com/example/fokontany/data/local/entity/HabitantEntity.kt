package com.example.fokontany.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fokontany.domain.model.SyncStatus

@Entity(
    tableName = "habitants",

    foreignKeys = [
        ForeignKey(
            entity = FoyerEntity::class,
            parentColumns = ["idLocal"],
            childColumns = ["foyerId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],

    indices = [
        Index(value = ["foyerId"])
    ]
)
data class HabitantEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val foyerId: Long,

    val nom: String,

    val prenom: String,

    val sexe: String,

    val dateNaissance: String,

    val telephone: String? = null,
    val codePaysTelephone: String = "",

    val estRepresentant: Boolean = false,

    val actif: Boolean = true,

    val syncStatus: SyncStatus = SyncStatus.PENDING,
)