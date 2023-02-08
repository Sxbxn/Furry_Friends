package com.k_bootcamp.furry_friends.model.animal

import com.google.gson.annotations.SerializedName

data class CheckList(
    @SerializedName("currdate")
    val date: String,
    @SerializedName("food")
    val eatQuantity: String,
    @SerializedName("bowels")
    val poobStatus: String,
    @SerializedName("note")
    val note: String,
    @SerializedName("routines")
    val routineList: List<RoutineStatus>
)
