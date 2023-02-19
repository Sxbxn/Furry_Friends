package com.k_bootcamp.furry_friends.data.response.animal

import com.google.gson.annotations.SerializedName

data class CheckListResponse(
    @SerializedName("index")
    val id: Int,
    @SerializedName("animal_id")
    val animalId: Int,
    @SerializedName("currdate")
    val date: String,
    @SerializedName("food")
    val eatQuantity: String,
    @SerializedName("bowels")
    val poobStatus: String,
    @SerializedName("note")
    val note: String
)
