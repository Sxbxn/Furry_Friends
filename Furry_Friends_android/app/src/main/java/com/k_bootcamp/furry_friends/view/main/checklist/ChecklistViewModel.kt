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
import com.k_bootcamp.furry_friends.model.animal.CheckList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val routineDao: RoutineDao,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val calendar = Calendar.getInstance()
    private val session = Application.prefs.session
//    private var animalId = Application.prefs.animalId   나중에 쓸거
    var animalId: Int? = null
    private val _routineLiveData = MutableLiveData<CheckListState>()
    val routineLiveData: LiveData<CheckListState>
        get() = _routineLiveData
    val eatLiveData: MutableLiveData<String> = MutableLiveData()
    val otherLiveData: MutableLiveData<String> = MutableLiveData()
    val animalRepo = animalRepository
    val rDao = routineDao

    fun initRoutines() {
        _routineLiveData.value = CheckListState.Loading
        if (session == null) {
            _routineLiveData.value = CheckListState.Error(context.getString(R.string.not_loged_in))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                animalId = 1   ////////////////////////////////// 임시
                // 현재 요일의 루틴을 가져옴
                val routines = animalRepository.getRoutinesFromDate()
                if (routines == null) {
                    _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.error)))
                } else {
                    _routineLiveData.postValue(
                        CheckListState.Success(
                            animalId!!,
                            session,
                            routines
                        )
                    )
                }
//                val info = animalRepository.getAnimalInfo(Session(session))
//                animalId = info?.animalId
//                if (animalId == null) {
//                    // 등록이 안되어 있음 -> 안되어있으므로 루틴에 아무것도 없어야함
//                    _routineLiveData.postValue(
//                        CheckListState.Error(context.getString(R.string.not_register_animal))
//                    )
//                } else {
//                    // 루틴 페이지에서 요일 체크하면 서버에도 전송이 되므로 데이터는 로컬 -  데이터는 같음이 보장 됨
//                    //
//                    _routineLiveData.postValue(CheckListState.Loading)
//                    viewModelScope.launch(Dispatchers.IO) {
//                        // 실패하면 error 상태로 변경
//                        val routines = animalRepository.getRoutinesFromIdByServer(animalId!!)
//                        if(routines == null) {
//                            _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.error)))
//                        } else{
//                            _routineLiveData.postValue(
//                                CheckListState.Success(
//                                    animalId!!,
//                                    session,
//                                    routines
//                                )
//                            )
//                        }
//
//                    }
//                }
            }
        }
    }

    fun submitCheckList(checkList: CheckList) {
        _routineLiveData.postValue(CheckListState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val response = animalRepository.submitDailyChecklist(checkList)
            if(response == null) {
                _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.error_response)))
            } else {
                _routineLiveData.postValue(CheckListState.Done)
            }
        }
    }

    fun getDatas(date: String): CheckList? {
        var response: CheckList? = null
        _routineLiveData.postValue(CheckListState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            response = animalRepository.getChecklistDatas(date)
            if(response == null) {
                _routineLiveData.postValue(CheckListState.Error(context.getString(R.string.error_response)))
            } else {
                _routineLiveData.postValue(CheckListState.Done)
            }
        }
        return response
    }
}