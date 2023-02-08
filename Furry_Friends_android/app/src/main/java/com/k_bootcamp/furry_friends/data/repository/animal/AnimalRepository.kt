package com.k_bootcamp.furry_friends.data.repository.animal

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineSubmit
import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.data.response.user.SessionResponse
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.model.user.SignInUser
import com.k_bootcamp.furry_friends.util.network.APIResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AnimalRepository {
    // remote service
    // 동물 등록
    suspend fun submitAnimal(body: MultipartBody.Part, json: RequestBody): SubmitAnimalResponse?
    // 정보 가져오기
    suspend fun getAnimalInfo(session: Session?): AnimalResponse?
    // 서버에 저장된 루틴 목록 가져오기
    suspend fun getRoutinesFromIdByServer(animalId: Int): List<RoutineResponse>?
    // 루틴 요일 체크박스 활성화시 데이터 보내기
    suspend fun submitDateRoutine(routine: RoutineResponse): RoutineSubmit?
    // 루틴 요일체크박스 비활성화시 데이터 보내기
    suspend fun deleteDateRoutine(routine: RoutineResponse): RoutineSubmit?
    // 서버에 있는 특정 루틴 지우기
    suspend fun deleteRoutineByServer(routineName: Routine): RoutineSubmit?



    // local service
    // 로컬 db에 루틴 저장 (default 저장용 & 추가 루틴 저장용 -> 서버와 연동)
    suspend fun insertRoutine(routine: Routine)
    // 불러오기
    suspend fun getAllRoutines(): List<Routine>
    suspend fun getRoutinesFromId(animalId: Int): List<Routine>
    suspend fun updateRoutine(isChecked: Boolean, session: String, animalId:Int, routineName:String)
    suspend fun deleteRoutine(routine: Routine)

    suspend fun updateMonday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateTuesday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateWednesday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateThursday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateFriday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateSaturday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateSunday(date:Boolean, animalId:Int, routineName: String )

    suspend fun updateTime(time: String, animalId:Int, routineName: String)

    suspend fun getTimeRoutine(animalId:Int, routineName: String): String
}