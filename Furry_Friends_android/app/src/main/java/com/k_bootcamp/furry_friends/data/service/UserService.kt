package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.LoginResponse
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.model.user.SignInUser
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    // 로그인  -- 추후 반환값 확인 *****
    @POST("/auth/login")
    suspend fun loginUser(
        @Body user: LoginUser
//    ): Response<AnimalResponse>
    ):Response<LoginResponse>

    // 정보가져오기---- 안 쓸 듯
    @GET("/{sessionId}")  //// session 객체로 교환 필요
    suspend fun getInfo(
        @Path("sessionId") sessionId: String?
    ): Response<String>

    // 회원가입
    @POST("/auth/register")
    suspend fun signInUser(
        @Body user: SignInUser
    ): Response<String>

    @GET("/auth/logout")
    suspend fun logoutUser(): Response<String>

    @DELETE("/auth/withdrawal")
    suspend fun withdrawalUser(): Response<String>
}