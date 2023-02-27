package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface WritingService {

    // 일상 기록 정보 가져오기
    @GET("/journal/journals")
    suspend fun getDailyList(
        // 인터셉터에 헤더 추가
    ): Response<List<DailyResponse>>

    // 일상기록 등록하기
    @Multipart
    @POST("journal/factory")
    suspend fun submitDailyWriting(
        @Part body: MultipartBody.Part,
        @Part("data") jsonDailyWriting: RequestBody
    ): Response<String>

    // 일상기록 수정하기
    @Multipart
    @PUT("journal/update")
    suspend fun updateDailyWriting(
        @Header("index") id: Int,
        @Part body: MultipartBody.Part,
        @Part("data") updatedJsonDailyWriting: RequestBody
    ): Response<String>

    // 일상 정보 삭제하기
    @DELETE("/journal/delete")
    suspend fun deleteDailyModel(
        @Header("index") id: Int,
    ): Response<String>


    // 진단 기록 정보 가져오기
    @GET("/health/records")
    suspend fun getDiagnosisList(
        // 인터셉터에 헤더 추가
    ): Response<List<DiagnosisResponse>>

    // 진단기록 등록하기
    @Multipart
    @POST("/health/factory")
    suspend fun submitDiagnosisWriting(
        @Part body: MultipartBody.Part,
        @Part("data") jsonDailyWriting: RequestBody
    ): Response<String>

    // 진단 기록 삭제하기
    @DELETE("/health/delete")
    suspend fun deleteDiagnosisModel(
        @Header("index") id: Int,
    ): Response<String>
}