package com.k_bootcamp.furry_friends.data.response.writing

import com.google.gson.annotations.SerializedName
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import java.util.*

// 응답 모델
data class DailyResponse(
    @SerializedName("index")
    val id: Int,
    @SerializedName("animal_id")
    val animalId: Int,
    @SerializedName("user_id") //session 값
    val userId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("currdate")
    val currdate:String
) {
    // 리사이클러뷰를 위한 매핑 함수
    fun toModel() = DailyModel(
        id, CellType.DAILY_CELL, title, imageUrl, content, currdate
    )
}


