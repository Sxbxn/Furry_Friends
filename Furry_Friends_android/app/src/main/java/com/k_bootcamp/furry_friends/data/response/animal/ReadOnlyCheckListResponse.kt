package com.k_bootcamp.furry_friends.data.response.animal

import com.google.gson.annotations.SerializedName

data class ReadOnlyCheckListResponse(
    @SerializedName("default")
    val checklistDefault: CheckListResponse?,
    @SerializedName("routine")
    val checklistRoutine: List<RoutineStatusResponse>?
)
