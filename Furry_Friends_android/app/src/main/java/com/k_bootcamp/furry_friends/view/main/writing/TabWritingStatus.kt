package com.k_bootcamp.furry_friends.view.main.writing

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse

sealed class TabWritingStatus {
    object Loading: TabWritingStatus()
    data class Done(
        val flag: Int
    ): TabWritingStatus()
    data class SuccessDaily(
        val animalId: Int,
        val session: String,
        val response: List<DailyResponse>
    ): TabWritingStatus()

    data class SuccessDiagnosis(
        val animalId: Int,
        val session: String,
        val response: List<DiagnosisResponse>
    ): TabWritingStatus()

    data class Error(
        val message: String
    ): TabWritingStatus()
}