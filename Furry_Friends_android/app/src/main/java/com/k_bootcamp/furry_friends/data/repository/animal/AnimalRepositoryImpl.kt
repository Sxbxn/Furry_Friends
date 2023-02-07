package com.k_bootcamp.furry_friends.data.repository.animal

import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineSubmit
import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.data.service.AnimalService
import com.k_bootcamp.furry_friends.data.service.UserService
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.model.animal.Routine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AnimalRepositoryImpl(
    private val animalService: AnimalService,
    private val routineDao: RoutineDao,
    private val ioDispatcher: CoroutineDispatcher
): AnimalRepository {
    override suspend fun submitAnimal(animal: Animal): SubmitAnimalResponse?= withContext(ioDispatcher){
        val response = animalService.submitAnimal(animal)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getAnimalInfo(session: Session?): AnimalResponse? = withContext(ioDispatcher) {
        session?.let{
            val response = animalService.getAnimalInfo(session)
            if(response.isSuccessful){
                response.body()
            } else {
                null
            }
        }
    }

    override suspend fun getRoutinesFromIdByServer(animalId: Int): List<RoutineResponse>? = withContext(ioDispatcher) {
        val response = animalService.getRoutinesFromId(animalId)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun submitDateRoutine(routine: RoutineResponse): RoutineSubmit? = withContext(ioDispatcher) {
        val response = animalService.submitDateRoutine(routine)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteDateRoutine(routine: RoutineResponse): RoutineSubmit? = withContext(ioDispatcher) {
        val response = animalService.deleteDateRoutine(routine)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteRoutineByServer(routine: Routine): RoutineSubmit? = withContext(ioDispatcher) {
        val response = animalService.deleteRoutineByServer(routine)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun insertRoutine(routine: Routine) = routineDao.insertRoutine(routine)

    override suspend fun getAllRoutines(): List<Routine> = routineDao.getAllRoutines()

    override suspend fun getRoutinesFromId(animalId: Int): List<Routine> = routineDao.getRoutineFromId(animalId)

    override suspend fun updateRoutine(isChecked: Boolean, session: String, animalId:Int, routineName:String) = routineDao.updateRoutine(isChecked, session, animalId, routineName)
    override suspend fun deleteRoutine(routine: Routine) = routineDao.deleteRoutine(routine)
    override suspend fun updateMonday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateMonday(date, animalId, routineName)

    override suspend fun updateTuesday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateTuesday(date, animalId, routineName)
    override suspend fun updateWednesday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateWednesday(date, animalId, routineName)

    override suspend fun updateThursday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateThursday(date, animalId, routineName)

    override suspend fun updateFriday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateFriday(date, animalId, routineName)

    override suspend fun updateSaturday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateSaturday(date, animalId, routineName)

    override suspend fun updateSunday(date: Boolean, animalId: Int, routineName: String) = routineDao.updateSunday(date, animalId, routineName)


    override suspend fun updateTime(time: String, animalId: Int, routineName: String) = routineDao.updateTime(time, animalId, routineName)

    override suspend fun getTimeRoutine(animalId: Int, routineName: String): String = routineDao.getTimeRoutine(animalId, routineName)


}