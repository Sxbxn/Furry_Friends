package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.model.animal.Animal
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Part

interface AnimalService {
    // 동물 정보 보내기
    @POST("/")
    suspend fun submitAnimal(
//        @Part animalImg: MultipartBody.Part,
        @Body animal: Animal
    ): Response<SubmitAnimalResponse>
}