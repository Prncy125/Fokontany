package com.example.fokontany.data.local.database

import androidx.room.TypeConverter
import com.example.fokontany.domain.model.StatutDistribution
import com.example.fokontany.domain.model.SyncStatus

class Converters {

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }

    @TypeConverter
    fun fromStatutDistribution(value: StatutDistribution): String {
        return value.name
    }

    @TypeConverter
    fun toStatutDistribution(value: String): StatutDistribution {
        return StatutDistribution.valueOf(value)
    }
}