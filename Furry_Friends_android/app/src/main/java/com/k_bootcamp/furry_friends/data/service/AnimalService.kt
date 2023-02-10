package com.k_bootcamp.furry_friends.data.service

import com.google.gson.JsonObject
import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineSubmit
import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.model.animal.CheckList
import com.k_bootcamp.furry_friends.model.animal.Routine
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AnimalService {
    // 동물 정보 보내기
    @Multipart
    @POST("/")
    suspend fun submitAnimal(
        @Part animalImg: MultipartBody.Part,
        @Part("animalJson") animal: RequestBody
    ): Response<String>
//    Response<SubmitAnimalResponse>


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
    ): Response<String>
//    ): Response<RoutineSubmit>

    @POST("/weekdayRoutine")
    suspend fun deleteDateRoutine(
        @Body routine: RoutineResponse
    ): Response<String>
//    ): Response<RoutineSubmit>

    @POST("/routinedelete")
    suspend fun deleteRoutineByServer(
        @Body routine: Routine
    ): Response<String>
//    ): Response<RoutineSubmit>

    @GET("/checklist")  ////
    suspend fun getRoutinesFromDate(
        @Header("login") session: String,
        @Header("curr_animal") animalId: Int
    ): Response<List<RoutineResponse>>

    @POST("/checklist")
    suspend fun submitDailyChecklist(
        @Body checkList: CheckList
    ): Response<JsonObject>
//    ): Response<String>  // 추후 실험 해야함  서버코드 보니까 얘는 json 반환하게 되어있었음

    @GET("/datas")
    suspend fun getChecklistDatas(
        // 추후 회의 후 추가
        @Header("currdate") date: String
    ): Response<CheckList>
}