package com.k_bootcamp.furry_friends.model.animal

import com.google.gson.annotations.SerializedName

data class ChecklistRoutine (
    @SerializedName("routines")
    val routineList: List<RoutineStatus>
)
