package com.k_bootcamp.furry_friends.model.user

import com.google.gson.annotations.SerializedName

data class LoginUser(
    @SerializedName("user_id")
    val id: String,
    @SerializedName("pw")
    val pwd: String
)
