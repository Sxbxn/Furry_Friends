package com.k_bootcamp.furry_friends.view.main.setting

sealed class SettingState {
    object Loading: SettingState()

    data class Success(
        val response: String
    ): SettingState()

    data class SuccessGetInfo(
        val id: Int,
        val userId: String,
        val name: String,
        val birthDay: String,
        val weight: Float,
        val sex: String,
        val isNeutered: Boolean,
        val imageUrl: String
    ): SettingState()

    data class Error(
        val message: String
    ): SettingState()
}
