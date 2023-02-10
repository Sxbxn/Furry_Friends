package com.k_bootcamp.furry_friends.model.writing

import com.google.gson.annotations.SerializedName
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.Model

data class DiagnosisModel(
    override val id: Int,
    override val type: CellType = CellType.DIAGNOSIS_CELL,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("currdate")
    val currdate:String,
    @SerializedName("comment")
    val comment: String,
): Model(id, type)
