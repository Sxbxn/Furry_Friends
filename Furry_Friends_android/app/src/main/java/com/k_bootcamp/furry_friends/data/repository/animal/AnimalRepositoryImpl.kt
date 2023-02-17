package com.k_bootcamp.furry_friends.data.repository.animal

import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.data.response.animal.ReadOnlyCheckListResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.model.animal.SendRoutine
import com.k_bootcamp.furry_friends.data.service.AnimalService
import com.k_bootcamp.furry_friends.model.animal.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AnimalRepositoryImpl(
    private val animalService: AnimalService,
    private val routineDao: RoutineDao,
    private val ioDispatcher: CoroutineDispatcher
): AnimalRepository {
    override suspend fun submitAnimal(body: MultipartBody.Part, json: RequestBody): AnimalResponse?= withContext(ioDispatcher){
        val response = animalService.submitAnimal(body, json)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getAnimalInfo(): AnimalResponse? = withContext(ioDispatcher) {
        val response = animalService.getAnimalInfo()
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }

    override suspend fun getRoutinesFromIdByServer(): List<SendRoutine>? = withContext(ioDispatcher) {
        val response = animalService.getRoutinesFromId()
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun submitDateRoutine(routine: SendRoutine): String? = withContext(ioDispatcher) {
        val response = animalService.submitDateRoutine(routine)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteDateRoutine(routine: SendRoutine): String? = withContext(ioDispatcher) {
        val response = animalService.deleteDateRoutine(routine)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteRoutineByServer(routine: Routine): String? = withContext(ioDispatcher) {
        val response = animalService.deleteRoutineByServer(routine)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    // deprecated
    override suspend fun getRoutinesFromDate(): List<SendRoutine>? = withContext(ioDispatcher) {
        val response = animalService.getRoutinesFromDate()
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getAllRoutinesByAnimalId(): List<RoutineResponse>? = withContext(ioDispatcher) {
        val response = animalService.getAllRoutinesByAnimalId()
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun submitDailyChecklist(checkList: CheckList): String? =  withContext(ioDispatcher) {
        val response = animalService.submitDailyChecklist(checkList)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
//    override suspend fun getChecklistDatas(date: String, weekday: String): CheckList? =  withContext(ioDispatcher) {
    override suspend fun getChecklistDatas(date: String, weekday: String): ReadOnlyCheckListResponse? =  withContext(ioDispatcher) {
        val response = animalService.getChecklistDatas(date, weekday)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteAnimalInfo(): AnimalResponse? =  withContext(ioDispatcher) {
        val response = animalService.deleteAnimalInfo()
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun updateAnimalProfile(body: MultipartBody.Part, jsonUpdateProfile: RequestBody): String? =  withContext(ioDispatcher) {
        val response = animalService.updateAnimalInfo(body, jsonUpdateProfile)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getAllAnimalInfo(): List<AnimalResponse>? = withContext(ioDispatcher) {
        val response = animalService.getAllAnimalInfo()
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun runAiProfile(image: MultipartBody.Part): String? = withContext(ioDispatcher) {
        val response = animalService.runAiProfile(image)
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

    override suspend fun getAllStatus(): List<RoutineStatus> = routineDao.getAllStatus()

    override suspend fun deleteAllStatus() = routineDao.deleteAllStatus()


}