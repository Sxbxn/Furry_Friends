package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface WritingService {

    // 일상 기록 정보 가져오기
    @GET("/")
    suspend fun getDailyList(
        // 인터셉터에 헤더 추가
    ): Response<List<DailyResponse>>

    // 진단 기록 정보 가져오기
    @GET("/")
    suspend fun getDiagnosisList(
        // 인터셉터에 헤더 추가
    ): Response<List<DiagnosisResponse>>

    // 일상 정보 삭제하기
//    @DELETE("/journal/delete")
//    suspend fun deleteDailyModel(
//        @Header("dailyModel") model: String
//    ): Response<String>
    @DELETE("/journal/delete")
    suspend fun deleteDailyModel(
        @Header("index") id: Int,
    ): Response<String>

    // 진단 기록 삭제하기
//    @DELETE("/health/delete")
//    suspend fun deleteDiagnosisModel(
//        @Header("diagnosisModel") model: String
//    ): Response<String>
    @DELETE("/health/delete")
    suspend fun deleteDiagnosisModel(
        @Header("index") id: Int,
    ): Response<String>

    @Multipart
    @POST("journal/factory")
    suspend fun submitDailyWriting(
        @Part body: MultipartBody.Part,
        @Part("dailyWritingJson")jsonDailyWriting: RequestBody
    ): Response<String>

    @Multipart
    @POST("health/factory")
    suspend fun submitDiagnosisWriting(
        @Part body: MultipartBody.Part,
        @Part("diagnosisWritingJson")jsonDailyWriting: RequestBody
    ): Response<String>



}