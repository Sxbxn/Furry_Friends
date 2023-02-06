package com.k_bootcamp.furry_friends.data.response.animal

data class AnimalResponse(
    val id: Int,
    val name: String,
    val birthDay: String,
    val weight: Float,
    val sex: String,
    val isNeutered: Boolean
)
