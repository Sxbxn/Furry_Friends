package com.k_bootcamp.furry_friends.view.main.writing.diagnosis

sealed class DiagnosisState {
    object Loading: DiagnosisState()

    data class Success(
        val response: String
    ): DiagnosisState()

    data class Error(
        val message: String
    ): DiagnosisState()
}
