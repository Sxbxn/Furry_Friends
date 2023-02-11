package com.k_bootcamp.furry_friends.data.repository.animal.writing

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody


interface WritingRepository {
    // 일상 기록 가져오기
    suspend fun getDailyList(): List<DailyResponse>?
    // 진단 기록 가져오기
    suspend fun getDiagnosisList(): List<DiagnosisResponse>?

    // 일상 기록 삭제하기
    suspend fun deleteDailyModel(modelId: Int): String?
    // 진단 기록 삭제하기
    suspend fun deleteDiagnosisModel(modelId: Int): String?
    // 일상 기록 등록하기
    suspend fun submitDailyWriting(body: MultipartBody.Part, jsonDailyWriting: RequestBody): String?
    // 진단기록 등록하기
    suspend fun submitDiagnosisWriting(body: MultipartBody.Part, jsonDailyWriting: RequestBody): String?

    //일상기록 수정하기
    suspend fun updateDailyWriting(body: MultipartBody.Part, jsonDailyWriting: RequestBody, writingId: Int): String?

}