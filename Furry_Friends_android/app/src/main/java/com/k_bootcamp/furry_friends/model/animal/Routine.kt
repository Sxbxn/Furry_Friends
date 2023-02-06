package com.k_bootcamp.furry_friends.model.animal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val routineId: Int = 0,
    val session: String,
    val animalId: Int,
    val routineName: String,
    val isOn: Boolean
//    val mon: Boolean,
//    val tue: Boolean,
//    val wed: Boolean,
//    val thu: Boolean,
//    val fri: Boolean,
//    val sat: Boolean,
//    val sun: Boolean
)
