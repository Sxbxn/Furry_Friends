package com.k_bootcamp.furry_friends.model.animal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val routineId: Int,
    val animalId: Int,
    val routineName: String
)
