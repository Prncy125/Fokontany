package com.example.fokontany.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fokontany.domain.model.SyncStatus

@Entity(tableName = "foyers")
data class FoyerEntity(

    @PrimaryKey(autoGenerate = true)
    val idLocal: Long = 0L,

    val adresse: String,

    val quartier: String,

    val dateEnregistrement: String,

    val actif: Boolean = true,

    val syncStatus: SyncStatus = SyncStatus.PENDING
)