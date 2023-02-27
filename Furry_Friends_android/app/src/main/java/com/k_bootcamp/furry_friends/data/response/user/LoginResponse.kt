package com.k_bootcamp.furry_friends.data.response.user

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("animal_id")
    val animalId: Int,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("animal_name")
    val name: String,
    @SerializedName("bday")
    val birthDay: String,
    @SerializedName("weight")
    val weight: Float,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("neutered")
    val isNeutered: Boolean,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("vet")
    val vet: Int = 0
)
