package com.k_bootcamp.furry_friends.data.repository.animal.writing

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.model.writing.Daily
import com.k_bootcamp.furry_friends.model.writing.Diagnosis

interface WritingRepository {
    // 일상 기록 가져오기
    suspend fun getDailyList(): List<DailyResponse>?
    // 진단 기록 가져오기
    suspend fun getDiagnosisList(): List<DiagnosisResponse>?

    // 일상 기록 삭제하기
    suspend fun deleteDailyModel(modelId: Int): String?
    // 진단 기록 삭제하기
    suspend fun deleteDiagnosisModel(modelId: Int): String?

}