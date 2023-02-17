package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.ReadOnlyCheckListResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.model.animal.SendRoutine
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
        @Part("data") animal: RequestBody
    ): Response<AnimalResponse>

    //  요일별 루틴 등록하기
    @POST("/routine/routine")
    suspend fun submitDateRoutine(
        @Body routine: SendRoutine
    ): Response<String>
//    ): Response<RoutineSubmit>

    @POST("/routine/weekdaydelete")
    suspend fun deleteDateRoutine(
        @Body routine: SendRoutine
    ): Response<String>
//    ): Response<RoutineSubmit>

    @POST("/routine/routinedelete")
    suspend fun deleteRoutineByServer(
        @Body routine: Routine
    ): Response<String>
//    ): Response<RoutineSubmit>

    // 선택된 동물의 루틴을 모두 가져와서 요일별로 필터링해서 사용한다.
    @GET("/routine/routine")
    suspend fun getAllRoutinesByAnimalId(
        // 헤더로 animal id 날아감
    ): Response<List<RoutineResponse>>



    // deprecated
    @GET("/check/checklist/deprecated")  ////
    suspend fun getRoutinesFromDate(): Response<List<SendRoutine>>
    // 루틴 정보 가져오기
    @GET("/check/checklist/deprecated")  ////
    suspend fun getRoutinesFromId(): Response<List<SendRoutine>>



    @POST("/check/checklist")
    suspend fun submitDailyChecklist(
        @Body checkList: CheckList
    ): Response<String>
//    ): Response<JsonObject>
      // 추후 실험 해야함  서버코드 보니까 얘는 json 반환하게 되어있었음


    @GET("/check/checklist")
    suspend fun getChecklistDatas(
        // 추후 회의 후 추가
        @Header("currdate") date: String,
        @Header("weekday") weekday: String
    ): Response<ReadOnlyCheckListResponse>
//    ): Response<CheckList>





    // 한 user의 모든 동물정보 가져오기
    @GET("/pet/management")
    suspend fun getAllAnimalInfo(): Response<List<AnimalResponse>>

    // 동물 정보 가져오기
    @GET("/pet/profile")  ////
    suspend fun getAnimalInfo(): Response<AnimalResponse>

    @DELETE("/pet/delete")
    suspend fun deleteAnimalInfo(): Response<AnimalResponse>

    @Multipart
    @PUT("/pet/update")
    suspend fun updateAnimalInfo(
        @Part body: MultipartBody.Part,
        @Part("data") jsonUpdateProfile: RequestBody
    ): Response<String>



    @Multipart
    @POST("/")
    suspend fun runAiProfile(
        @Part body: MultipartBody.Part
    ): Response<String>
}