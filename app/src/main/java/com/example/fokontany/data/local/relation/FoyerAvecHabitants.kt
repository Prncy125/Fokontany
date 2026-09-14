package com.example.fokontany.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity

data class FoyerAvecHabitants(

    @Embedded
    val foyer: FoyerEntity,

    @Relation(
        parentColumn = "idLocal",
        entityColumn = "foyerId"
    )
    val habitants: List<HabitantEntity>
)