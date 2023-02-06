package com.k_bootcamp.furry_friends.view.main.routine

import android.content.Context
import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseViewModel
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.view.main.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.main.home.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val routineDao: RoutineDao,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val session = Application.prefs.session
    private var animalId: Int? = null
    private val _routineLiveData = MutableLiveData<RoutineState>()
    val routineLiveData: LiveData<RoutineState>
        get() = _routineLiveData
    private var defaultRoutines = listOf("양치", "빗질", "산책")
    val animalRepo = animalRepository
    val rDao = routineDao


    fun initRoutines() {
        _routineLiveData.value = RoutineState.Loading
        if (session == null) {
            _routineLiveData.value = RoutineState.Error(context.getString(R.string.not_loged_in))
        } else {
            // 기본적으로 양치, 빗질, 산책 루틴을 로컬 db에 삽입하여 유지함  - 세션과 동물이 있을때만
            viewModelScope.launch(Dispatchers.IO) {
                val info = animalRepository.getAnimalInfo(Session(session))
                animalId = info?.animalId
                animalId = 1   //////////////
                if (animalId == null) {
                    // 등록이 안되어 있음 -> 안되어있으므로 루틴에 아무것도 없어야함
                    _routineLiveData.postValue(
                        RoutineState.Error(context.getString(R.string.not_register_animal))
                    )
                } else {
                    _routineLiveData.postValue(RoutineState.Loading)
                    val existRoutines = animalRepository.getRoutinesFromId(animalId!!)
                    // 해당 동물의 기본 루틴이 설정 되어있는 경우 기본을 없앰
                    if (existRoutines.isNotEmpty()) {
                        defaultRoutines = listOf()
                    }
                    defaultRoutines.forEach {
                        try {
                            animalRepository.insertRoutine(
                                Routine(
                                    animalId = animalId!!,
                                    session = session,
                                    routineName = it,
                                    isOn = false
                                )
                            )
                        } catch (e: Exception) {
                            _routineLiveData.postValue(RoutineState.Error(context.getString(R.string.error_response)))
                        }
                        val routines = animalRepository.getRoutinesFromId(animalId!!)
                        _routineLiveData.postValue(
                            RoutineState.Success(
                                animalId!!,
                                session,
                                routines
                            )
                        )
                    }
                    getRoutinesFromId()
                }
            }
        }
    }

    private fun getRoutinesFromId() {
        _routineLiveData.postValue(RoutineState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val routines = animalRepository.getRoutinesFromIdByServer(animalId!!)
            routines?.forEach {
                animalRepository.insertRoutine(
                    Routine(
                        animalId = it.animalId,
                        routineName = it.routineName,
                        session = session!!,
                        isOn = false
                    )
                )
            }
            val updatedRoutines = animalRepository.getRoutinesFromId(animalId!!)
            _routineLiveData.postValue(RoutineState.Success(animalId!!, session!!, updatedRoutines))
        }
    }

    fun addRoutine(routineName: String, session: String?) {
        if (session == null) {
            _routineLiveData.value = RoutineState.Error(context.getString(R.string.not_loged_in))
        } else {
            _routineLiveData.postValue(RoutineState.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                animalRepository.insertRoutine(
                    Routine(
                        animalId = animalId!!,
                        session = session,
                        routineName = routineName,
                        isOn = false
                    )
                )
                val updatedRoutines = animalRepository.getRoutinesFromId(animalId!!)
                _routineLiveData.postValue(
                    RoutineState.Success(
                        animalId!!,
                        session,
                        updatedRoutines
                    )
                )
            }
        }
    }
}