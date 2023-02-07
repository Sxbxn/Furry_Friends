package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineSubmit
import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.model.animal.Routine
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AnimalService {
    // 동물 정보 보내기
    @POST("/")
    suspend fun submitAnimal(
//        @Part animalImg: MultipartBody.Part,
        @Body animal: Animal
    ): Response<SubmitAnimalResponse>


    // 동물 정보 가져오기
    @GET("/{session}")  ////
    suspend fun getAnimalInfo(
      @Path("session") session: Session?
    ): Response<AnimalResponse>


    // 루틴 정보 가져오기
    @GET("/{animalId}")  ////
    suspend fun getRoutinesFromId(
        @Path("animalId") animalId: Int?
    ): Response<List<RoutineResponse>>

    @POST("/routine")
    suspend fun submitDateRoutine(
        @Body routine: RoutineResponse
    ): Response<RoutineSubmit>

    @POST("/weekdayRoutine")
    suspend fun deleteDateRoutine(
        @Body routine: RoutineResponse
    ): Response<RoutineSubmit>

    @POST("/routinedelete")
    suspend fun deleteRoutineByServer(
        @Body routine: Routine
    ): Response<RoutineSubmit>


}