package com.example.fokontany.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity

data class HabitantAvecFoyer(

    @Embedded
    val habitant: HabitantEntity,

    @Relation(
        parentColumn = "foyerId",
        entityColumn = "idLocal"
    )
    val foyer: FoyerEntity?
)
