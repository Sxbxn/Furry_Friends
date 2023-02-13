package com.k_bootcamp.furry_friends.data.repository.user

import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.data.response.user.SessionResponse
import com.k_bootcamp.furry_friends.data.response.user.SignInResponse
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.model.user.SignInUser
import com.k_bootcamp.furry_friends.util.network.APIResponse

interface UserRepository {
    suspend fun loginUser(user: LoginUser): Session?
    suspend fun getInfo(sessionId: String): String?
    suspend fun signInUser(user: SignInUser): String?
    suspend fun logout(): String?
//    suspend fun loginUser(user: LoginUser): APIResponse<SessionResponse>
//    suspend fun getInfo(sessionId: String): APIResponse<String?>
}