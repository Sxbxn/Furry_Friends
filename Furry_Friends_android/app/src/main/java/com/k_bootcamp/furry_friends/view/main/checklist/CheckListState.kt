package com.k_bootcamp.furry_friends.view.main.checklist

import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse

sealed class CheckListState{
    object Loading: CheckListState()
    object Done: CheckListState()
    data class Success(
        val animalId: Int,
        val session: String,
        val routines: List<RoutineResponse>
    ): CheckListState()

    data class Error(
        val message: String
    ): CheckListState()
}
