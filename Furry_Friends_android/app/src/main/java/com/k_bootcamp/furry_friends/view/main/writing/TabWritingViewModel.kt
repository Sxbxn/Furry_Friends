package com.k_bootcamp.furry_friends.view.main.writing

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.writing.WritingRepository
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import com.k_bootcamp.furry_friends.model.writing.DiagnosisModel
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabWritingViewModel @Inject constructor(
    private val writingRepository: WritingRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val session = Application.prefs.session
    private val animalId = Application.prefs.animalId
    private val _tabLiveData = MutableLiveData<TabWritingStatus>()
    val tabLiveData: LiveData<TabWritingStatus>
        get() = _tabLiveData

//    private val _diagnosisLiveData = MutableLiveData<TabWritingStatus>()
//    val diagnosisLiveData: LiveData<TabWritingStatus>
//        get() = _diagnosisLiveData


    fun getDailyList() {
        if (session == null) {
            _tabLiveData.value = TabWritingStatus.Error(context.getString(R.string.not_loged_in))
        } else {
            if (animalId == -999) {
                // 동물이 없음 --> 등록해야함
                _tabLiveData.value =
                    TabWritingStatus.Error(context.getString(R.string.not_register_animal))
            } else {
                _tabLiveData.value = TabWritingStatus.Loading
                // 세션, 동물id값으로 서버에서 일상 기록 목록을 가져옴
                viewModelScope.launch(ioDispatcher) {
                    val response = writingRepository.getDailyList()
                    if (response == null) {
                        _tabLiveData.postValue(TabWritingStatus.Error(context.getString(R.string.error_response)))
                    } else if(response.isEmpty()) {
                        _tabLiveData.postValue(TabWritingStatus.Error(context.getString(R.string.not_register_animal)))
                    } else {
                        _tabLiveData.postValue(
                            TabWritingStatus.SuccessDaily(
                                animalId, session, response
                            )
                        )
                    }
                }
            }
        }
    }

    fun getDiagnosisList() {
        if (session == null) {
            _tabLiveData.value = TabWritingStatus.Error(context.getString(R.string.not_loged_in))
        } else {
            if (animalId == -999) {
                // 동물이 없음 --> 등록해야함
                _tabLiveData.value =
                    TabWritingStatus.Error(context.getString(R.string.not_register_animal))
            } else {
                _tabLiveData.value = TabWritingStatus.Loading
                // 세션, 동물id값으로 서버에서 일상 기록 목록을 가져옴
                viewModelScope.launch(ioDispatcher) {
                    val response = writingRepository.getDiagnosisList()
                    if (response == null) {
                        _tabLiveData.postValue(TabWritingStatus.Error(context.getString(R.string.error_response)))
                    } else if(response.isEmpty()) {
                        _tabLiveData.postValue(TabWritingStatus.Error(context.getString(R.string.not_register_animal)))
                    } else {
                        _tabLiveData.postValue(
                            TabWritingStatus.SuccessDiagnosis(
                                animalId, session, response
                            )
                        )
                    }
                }
            }
        }
    }

    // 삭제로직 구현
    fun deleteWriting(model: Model) = viewModelScope.launch(ioDispatcher) {
        _tabLiveData.postValue(TabWritingStatus.Loading)
        if (model is DailyModel) {
            Log.e("daily 삭제됨", model.toString())
            val response = writingRepository.deleteDailyModel(model.id)
            if(response == null) {
                _tabLiveData.postValue(TabWritingStatus.Error("삭제 실패"))
            } else {
                _tabLiveData.postValue(TabWritingStatus.Done(0))
            }
        } else if(model is DiagnosisModel) {
            Log.e("diagnosis 삭제됨", model.toString())
            val response = writingRepository.deleteDiagnosisModel(model.id)
            if(response == null) {
                _tabLiveData.postValue(TabWritingStatus.Error("삭제 실패"))
            } else {
                _tabLiveData.postValue(TabWritingStatus.Done(1))
            }
        }
    }
}

