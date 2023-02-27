package com.k_bootcamp.furry_friends.model.animal

import com.google.gson.annotations.SerializedName

data class SendRoutine (
    @SerializedName("routine_id")
    val routineId: Int,
    @SerializedName("animal_id")
    val animalId: Int,
    @SerializedName("routine_name")
    val routineName: String,
    @SerializedName("weekday")
    val weekDay: String
)