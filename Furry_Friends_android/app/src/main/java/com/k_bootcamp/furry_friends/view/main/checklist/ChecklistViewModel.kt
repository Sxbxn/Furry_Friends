package com.k_bootcamp.furry_friends.view.main.checklist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.data.response.animal.ReadOnlyCheckListResponse
import com.k_bootcamp.furry_friends.model.animal.CheckList
import com.k_bootcamp.furry_friends.model.animal.RoutineStatus
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val routineDao: RoutineDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val session = Application.prefs.session
    private var animalId = Application.prefs.animalId

    private val _routineLiveData = MutableLiveData<CheckListState>()
    val routineLiveData: LiveData<CheckListState>
        get() = _routineLiveData
    val eatLiveData: MutableLiveData<String> = MutableLiveData()
    val otherLiveData: MutableLiveData<String> = MutableLiveData()
    val rDao = routineDao

    // 체크리스트에서 해당 요일의 루틴을 가져오는 함수
    fun initRoutines() {
        _routineLiveData.value = CheckListState.Loading
        if (session == null) {
            _routineLiveData.value = CheckListState.Error(context.getString(R.string.not_loged_in))
        } else if(animalId == -999) {
            _routineLiveData.value = CheckListState.Error(context.getString(R.string.not_register_animal))
        } else {
            viewModelScope.launch(ioDispatcher) {
                // 현재 요일의 루틴을 가져옴
                val routines = animalRepository.getAllRoutinesByAnimalId()
                if (routines == null) {
                    _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.error)))
                } else {
                    _routineLiveData.postValue(
                        CheckListState.Success(
                            animalId,
                            session,
                            routines
                        )
                    )
                }
            }
        }
    }

    // 체크리스트 저장하는 함수
    fun submitCheckList(checkList: CheckList) {
        _routineLiveData.postValue(CheckListState.Loading)
        viewModelScope.launch(ioDispatcher) {
            val response = animalRepository.submitDailyChecklist(checkList)
            if (response == null) {
                _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.error_response)))
            } else {
                _routineLiveData.postValue(CheckListState.Done)
            }
        }
    }

    // 해당 요일의 체크리스트를 가져오는 함수 - 캘린더뷰에서 접근  -> get /check/checklist 에서 받아와서 파싱
    fun getDatas(date: String, weekday: String) {
        _routineLiveData.postValue(CheckListState.Loading)
        viewModelScope.launch(ioDispatcher) {
            val response = animalRepository.getChecklistDatas(date, weekday)
            if (response == null) {
                _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.no_response)))
            }
            else {
                _routineLiveData.postValue(CheckListState.ReadDone(response))
            }
        }
    }

    suspend fun getAllStatus(): List<RoutineStatus> {
        val deffered = CoroutineScope(ioDispatcher).async {
            routineDao.getAllStatus()
        }.await()
        return deffered
    }

    suspend fun deleteAllStatus() =
        viewModelScope.launch(ioDispatcher) { routineDao.deleteAllStatus() }
}