package com.k_bootcamp.furry_friends.view.main.home

sealed class HomeState{
    object Loading: HomeState()

    data class Success(
        val id: Int,
        val name: String,
        val birthDay: String,
        val weight: Float,
        val sex: String,
        val isNeutered: Boolean
    ): HomeState()

    data class Error(
        val message: String
    ): HomeState()
}
