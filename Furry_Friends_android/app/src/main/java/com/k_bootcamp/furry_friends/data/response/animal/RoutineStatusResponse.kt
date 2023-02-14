package com.k_bootcamp.furry_friends.data.response.animal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class RoutineStatusResponse(
    @SerializedName("index")
    val id: Int,
    @SerializedName("routine_id")
    val animalId: Int,
    @SerializedName("routine_index")
    val routineId: Int,
    @SerializedName("routine_name")
    val routineName: String,
    @SerializedName("currdate")
    val date: String,
    @SerializedName("status")
    val status: Boolean // 0 or 1
)
