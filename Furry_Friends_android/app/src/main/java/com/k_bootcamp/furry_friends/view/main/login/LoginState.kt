package com.k_bootcamp.furry_friends.view.main.login

sealed class LoginState{
    object Loading: LoginState()

    data class Success(
        val session: String,
        val animalId: Int
    ): LoginState()

    data class Error(
        val message: String
    ): LoginState()
}
