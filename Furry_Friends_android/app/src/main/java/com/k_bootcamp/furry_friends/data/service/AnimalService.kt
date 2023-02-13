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
    @POST("/auth/registerAnimal")
    suspend fun submitAnimal(
        @Part animalImg: MultipartBody.Part,
        @Part("animalJson") animal: RequestBody
    ): Response<AnimalResponse>

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




    @GET("/check/checklist")  ////
    suspend fun getRoutinesFromDate(): Response<List<RoutineResponse>>

    @POST("/check/checklist")
    suspend fun submitDailyChecklist(
        @Body checkList: CheckList
    ): Response<JsonObject>
    //    ): Response<String>  // 추후 실험 해야함  서버코드 보니까 얘는 json 반환하게 되어있었음

    // 루틴 정보 가져오기
    @GET("/check/checklist")  ////
    suspend fun getRoutinesFromId(): Response<List<RoutineResponse>>


    @GET("/datas")
    suspend fun getChecklistDatas(
        // 추후 회의 후 추가
        @Header("currdate") date: String
    ): Response<CheckList>






    // 한 user의 모든 동물정보 가져오기
    @GET("/pet/management")
    suspend fun getAllAnimalInfo(): Response<List<AnimalResponse>>

    // 동물 정보 가져오기
    @GET("/pet/profile")  ////
    suspend fun getAnimalInfo(): Response<AnimalResponse>

    @DELETE("/pet/delete")
    suspend fun deleteAnimalInfo(): Response<String>

    @Multipart
    @PUT("/pet/update")
    suspend fun updateAnimalInfo(
        @Part body: MultipartBody.Part,
        @Part("updateAnimal") jsonUpdateProfile: RequestBody
    ): Response<String>
}