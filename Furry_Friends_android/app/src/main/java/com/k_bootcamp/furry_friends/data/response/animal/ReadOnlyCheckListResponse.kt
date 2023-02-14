package com.k_bootcamp.furry_friends.data.response.animal

data class ReadOnlyCheckListResponse(
    val checklistDefault: CheckListResponse,
    val checklistRoutine: List<RoutineStatusResponse>
)
