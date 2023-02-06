package com.k_bootcamp.furry_friends.data.db.dao

import androidx.room.*
import com.k_bootcamp.furry_friends.model.animal.Routine

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

    // 해당 Routine 을 삭제한다
    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)   // 충돌나면 무시 (기본 3개는 유지되어야 하므로)
    suspend fun insertRoutine(routine: Routine)

}