package com.k_bootcamp.furry_friends.model.user

import com.google.gson.annotations.SerializedName

data class SignInUser(
    @SerializedName("user_id")
    val id: String,
    @SerializedName("pw")
    val pwd: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("type")
    val userType: Int = 0
)