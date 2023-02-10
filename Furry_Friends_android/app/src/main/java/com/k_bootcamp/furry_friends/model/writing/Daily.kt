package com.k_bootcamp.furry_friends.model.writing

import com.google.gson.annotations.SerializedName

// 전송 모델
data class Daily(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("currdate")
    val currdate:String
)
