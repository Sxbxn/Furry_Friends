package com.k_bootcamp.furry_friends.data.repository.animal

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.data.response.user.SessionResponse
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.model.user.SignInUser
import com.k_bootcamp.furry_friends.util.network.APIResponse

interface AnimalRepository {
    // remote service
    suspend fun submitAnimal(animal: Animal): SubmitAnimalResponse?
    suspend fun getAnimalInfo(session: Session?): AnimalResponse?


    // local service
    // 로컬 db에 루틴 저장 (default 저장용 & 추가 루틴 저장용 -> 서버와 연동)
    suspend fun insertRoutine(routine: Routine)
    // 불러오기
    suspend fun getAllRoutines(): List<Routine>
    suspend fun getRoutinesFromId(animalId: String): List<Routine>
}