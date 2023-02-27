package com.k_bootcamp.furry_friends.model.animal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "routineStatus")
data class RoutineStatus(
    @SerializedName("index")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("routine_id")
    val routineId: Int,
    @SerializedName("routine_name")
    val routineName: String,
    @SerializedName("currdate")
    val date: String,
    @SerializedName("status")
    val status: Boolean // 0 or 1
)
