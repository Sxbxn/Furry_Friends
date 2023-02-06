package com.k_bootcamp.furry_friends.view.main.routine

import com.k_bootcamp.furry_friends.model.animal.Routine

sealed class RoutineState{
    object Loading: RoutineState()

    data class Success(
        val animalId: Int,
        val session: String,
        val routines: List<Routine>
    ): RoutineState()

    data class Error(
        val message: String
    ): RoutineState()
}
