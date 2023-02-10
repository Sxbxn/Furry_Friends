package com.k_bootcamp.furry_friends.model.writing

import com.google.gson.annotations.SerializedName
import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.Model

data class DailyModel(
    @SerializedName("id")
    override val id: Int,
    @SerializedName("type")
    override val type: CellType = CellType.DAILY_CELL,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("currdate")
    val currdate:String
): Model(id, type)
