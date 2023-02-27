package com.k_bootcamp.furry_friends.view.main.signin

sealed class SignInState{
    object Loading: SignInState()

    data class Success(
        val isSuccess: String
    ): SignInState()

    data class Error(
        val message: String
    ): SignInState()
}
