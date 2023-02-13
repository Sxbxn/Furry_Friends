package com.k_bootcamp.furry_friends.view.main.home

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse

sealed class HomeState{
    object Loading: HomeState()

    data class Success(
        val id: Int,
        val userId: String,
        val name: String,
        val birthDay: String,
        val weight: Float,
        val sex: String,
        val isNeutered: Boolean,
        val imageUrl: String
    ): HomeState()

    data class SuccessList(
        val infoList: List<AnimalResponse>
    ): HomeState()

    data class Error(
        val message: String
    ): HomeState()
}
