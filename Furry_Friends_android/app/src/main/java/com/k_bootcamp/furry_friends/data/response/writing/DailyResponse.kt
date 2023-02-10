package com.k_bootcamp.furry_friends.data.response.writing

import com.google.gson.annotations.SerializedName
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import java.util.*

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
    fun toModel() = DailyModel(
        id, CellType.DAILY_CELL, title, imageUrl, content, currdate
    )
}


