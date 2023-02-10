package com.k_bootcamp.furry_friends.data.response.writing

import com.google.gson.annotations.SerializedName
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.writing.DiagnosisModel
import java.util.*

data class DiagnosisResponse(
    @SerializedName("index")
    val id: Int,
    @SerializedName("animal_id")
    val animalId: Int,
    @SerializedName("user_id") //session 값
    val userId: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("currdate")
    val currdate:String,
    @SerializedName("comment")
    val comment: String,
){
    fun toModel() = DiagnosisModel(
        id, CellType.DIAGNOSIS_CELL, imageUrl, content, currdate, comment
    )
}
