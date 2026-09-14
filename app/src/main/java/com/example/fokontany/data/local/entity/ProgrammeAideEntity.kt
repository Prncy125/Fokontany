package com.example.fokontany.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fokontany.domain.model.SyncStatus

@Entity(tableName = "programmes_aide")
data class ProgrammeAideEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val nom: String,

    val description: String? = null,

    val dateDebut: String,

    val syncStatus: SyncStatus = SyncStatus.PENDING
)