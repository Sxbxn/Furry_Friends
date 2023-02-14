package com.k_bootcamp.furry_friends.data.repository.animal

import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.ReadOnlyCheckListResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.model.animal.SendRoutine
import com.k_bootcamp.furry_friends.model.animal.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface AnimalRepository {
    // remote service
    // 동물 등록
    suspend fun submitAnimal(body: MultipartBody.Part, json: RequestBody): AnimalResponse?
//    suspend fun submitAnimal(body: MultipartBody.Part, json: RequestBody): SubmitAnimalResponse?
    // 정보 가져오기
    suspend fun getAnimalInfo(): AnimalResponse?
    // 서버에 저장된 루틴 목록 가져오기
    suspend fun getRoutinesFromIdByServer(): List<SendRoutine>?
    // 루틴 요일 체크박스 활성화시 데이터 보내기
    suspend fun submitDateRoutine(routine: SendRoutine): String?
//    suspend fun submitDateRoutine(routine: RoutineResponse): RoutineSubmit?
    // 루틴 요일체크박스 비활성화시 데이터 보내기
    suspend fun deleteDateRoutine(routine: SendRoutine): String?
//    suspend fun deleteDateRoutine(routine: RoutineResponse): RoutineSubmit?
    // 서버에 있는 특정 루틴 지우기
    suspend fun deleteRoutineByServer(routineName: Routine): String?
//    suspend fun deleteRoutineByServer(routineName: Routine): RoutineSubmit?
    // 현재요일에 기록된 루틴 가져오기
    suspend fun getRoutinesFromDate(): List<SendRoutine>?
    // 서버에서 특정 동물(헤더)의 모든 루틴 가져오기
    suspend fun getAllRoutinesByAnimalId(/*헤더*/): List<RoutineResponse>?
    // 금일 체크리스트 등록(저장)하기
    suspend fun submitDailyChecklist(checkList: CheckList): String?
    // 캘린더에서 접근하여 해당일자 데이터 가져오기
//    suspend fun getChecklistDatas(date: String, weekday: String): CheckList?
    suspend fun getChecklistDatas(date: String, weekday: String): ReadOnlyCheckListResponse?
    // 동물 프로필 삭제
    suspend fun deleteAnimalInfo(): String?
    // 동물 프로필 수정
    suspend fun updateAnimalProfile(body: MultipartBody.Part, jsonUpdateProfile: RequestBody): String?
    // 동물 프로필 선택을 위한 모든 동물 리스트 가져오기
    suspend fun getAllAnimalInfo(): List<AnimalResponse>?



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

    suspend fun getAllStatus(): List<RoutineStatus>

    suspend fun deleteAllStatus()


}