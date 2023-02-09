package com.k_bootcamp.furry_friends.view.main.writing



sealed class TabWritingStatus {
    object Loading: TabWritingStatus()
    object Done: TabWritingStatus()
    data class Success(
        val animalId: Int,
        val session: String
    ): TabWritingStatus()

    data class Error(
        val message: String
    ): TabWritingStatus()
}