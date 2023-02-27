package com.k_bootcamp.furry_friends.view.main.writing.daily

sealed class DailyState {
    object Loading: DailyState()

    data class Success(
        val response: String
    ): DailyState()

    data class Error(
        val message: String
    ): DailyState()
}
