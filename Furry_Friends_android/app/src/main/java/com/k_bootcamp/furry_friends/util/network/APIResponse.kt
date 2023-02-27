package com.k_bootcamp.furry_friends.util.network

// state로 해보다가 이상하면 이걸로 변경 (데이터 타입 분리가 아니라 제네릭으로 통으로 받음)
sealed class APIResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null): APIResponse<T>(data)
    class Loading<T>(data: T? = null): APIResponse<T>(data)
    class Error<T>(message: String, data: T? = null): APIResponse<T>(data, message)
}