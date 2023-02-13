package com.k_bootcamp.furry_friends.data.response.user

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("user_id")
    val sesionId: String,
    @SerializedName("animal_id")
    val animalId: Int
)
