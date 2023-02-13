package com.k_bootcamp.furry_friends.view.main.home.submitanimal

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse

sealed class SubmitAnimalState{
    object Loading: SubmitAnimalState()

    data class Success(
        val isSuccess: AnimalResponse
    ): SubmitAnimalState()

    data class Error(
        val message: String
    ): SubmitAnimalState()
}
