package com.k_bootcamp.furry_friends.data.repository.user

import com.k_bootcamp.furry_friends.data.response.user.LoginResponse
import com.k_bootcamp.furry_friends.data.service.UserService
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.model.user.SignInUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userService: UserService,
    private val ioDispatcher: CoroutineDispatcher
): UserRepository {
    override suspend fun loginUser(user: LoginUser): LoginResponse? = withContext(ioDispatcher){
        val response = userService.loginUser(user)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getInfo(sessionId: String): String?  = withContext(ioDispatcher){
        val response = userService.getInfo(sessionId)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun signInUser(user: SignInUser): String? = withContext(ioDispatcher) {
        val response = userService.signInUser(user)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun logout(): String? = withContext(ioDispatcher) {
        val response = userService.logoutUser()
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun withdrawUser(): String? = withContext(ioDispatcher) {
        val response = userService.withdrawalUser()
        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}