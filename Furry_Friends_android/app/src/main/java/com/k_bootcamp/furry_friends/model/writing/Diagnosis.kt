package com.k_bootcamp.furry_friends.model.writing

import com.google.gson.annotations.SerializedName

// 전송 모델
data class Diagnosis(
    @SerializedName("content")
    val content: String,
    @SerializedName("currdate")
    val currdate:String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("affected_area")
    val affectedArea: String
)
