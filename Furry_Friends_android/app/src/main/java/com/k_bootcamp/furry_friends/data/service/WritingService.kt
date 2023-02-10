package com.k_bootcamp.furry_friends.data.service

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.model.writing.Daily
import com.k_bootcamp.furry_friends.model.writing.Diagnosis
import retrofit2.Response
import retrofit2.http.*

interface WritingService {

    // 일상 기록 정보 가져오기
    @GET("/")
    suspend fun getDailyList(
        // 헤더 직접 추가 or 인터셉터에 헤더 추가
    ): Response<List<DailyResponse>>

    // 진단 기록 정보 가져오기
    @GET("/")
    suspend fun getDiagnosisList(
        // 헤더 직접 추가 or 인터셉터에 헤더 추가
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
}