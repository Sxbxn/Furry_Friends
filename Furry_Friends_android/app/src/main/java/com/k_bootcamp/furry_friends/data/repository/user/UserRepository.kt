package com.k_bootcamp.furry_friends.data.repository.user

import com.k_bootcamp.furry_friends.data.response.user.LoginResponse
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.model.user.SignInUser

interface UserRepository {
    suspend fun loginUser(user: LoginUser): LoginResponse?
    suspend fun getInfo(sessionId: String): String?
    suspend fun signInUser(user: SignInUser): String?
    suspend fun logout(): String?
    suspend fun withdrawUser(): String?
}