package com.k_bootcamp.furry_friends.model.animal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routineStatus")
data class RoutineStatus(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routineId: Int,
    val routineName: String,
    val date: String,
    val status: Boolean // 0 or 1
)
