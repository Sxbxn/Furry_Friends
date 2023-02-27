package com.k_bootcamp.furry_friends.model.animal

import com.google.gson.annotations.SerializedName

data class Animal(
    @SerializedName("animal_name")
    val name: String,
    @SerializedName("bday")
    val birthDay: String,
    @SerializedName("weight")
    val weight: Float,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("neutered")
    val isNeutered: Boolean
)
