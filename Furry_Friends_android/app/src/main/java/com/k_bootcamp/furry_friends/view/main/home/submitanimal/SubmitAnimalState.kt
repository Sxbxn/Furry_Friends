package com.k_bootcamp.furry_friends.view.main.home.submitanimal

sealed class SubmitAnimalState{
    object Loading: SubmitAnimalState()

    data class Success(
        val isSuccess: Boolean
    ): SubmitAnimalState()

    data class Error(
        val message: String
    ): SubmitAnimalState()
}
