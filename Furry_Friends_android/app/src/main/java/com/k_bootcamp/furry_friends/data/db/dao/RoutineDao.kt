package com.k_bootcamp.furry_friends.data.db.dao

import androidx.room.*
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.model.animal.RoutineStatus

@Dao
interface RoutineDao {
    //전체 Routine 가져오기
    @Query("SELECT * FROM routine")
    suspend fun getAllRoutines(): List<Routine>

    // 해당 동물의 Routine 을 가져온다
    @Query("SELECT * FROM routine WHERE animalId = :animalId")
    suspend fun getRoutineFromId(animalId: Int): List<Routine>

    @Query("UPDATE routine SET isOn = :isChecked WHERE session = :session AND animalId = :animalId AND routineName = :routineName")
    suspend fun updateRoutine(isChecked: Boolean, session: String, animalId:Int, routineName: String)


    // 루틴 설정 관련
    // 해당 Routine 을 삭제한다
    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)   // 충돌나면 무시 (기본 3개는 유지되어야 하므로)
    suspend fun insertRoutine(routine: Routine)

    @Query("UPDATE routine set mon = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateMonday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set tue = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateTuesday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set wed = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateWednesday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set thu = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateThursday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set fri = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateFriday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set sat = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateSaturday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set sun = :date where animalId = :animalId AND routineName = :routineName")
    suspend fun updateSunday(date:Boolean, animalId:Int, routineName: String )

    @Query("UPDATE routine set time = :time where animalId = :animalId AND routineName = :routineName")
    suspend fun updateTime(time: String, animalId:Int, routineName: String)

    @Query("SELECT time FROM routine where animalId = :animalId AND routineName = :routineName")
    suspend fun getTimeRoutine(animalId:Int, routineName: String): String



    // status 관련
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineStatus(status: RoutineStatus)

    @Query("DELETE FROM routineStatus WHERE routineId =:routineId and routineName=:routineName and date=:date")
    suspend fun deleteRoutineStatus(routineId: Int, routineName: String, date: String)

    @Query("SELECT * FROM routineStatus")
    suspend fun getAllStatus(): List<RoutineStatus>

    @Query("DELETE FROM routineStatus")
    suspend fun deleteAllStatus()

}