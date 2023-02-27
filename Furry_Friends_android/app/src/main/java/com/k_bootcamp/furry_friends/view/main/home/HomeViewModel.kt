package com.k_bootcamp.furry_friends.view.main.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val session = Application.prefs.session
    private val animalId = Application.prefs.animalId
    private val _animalInfoLiveData = MutableLiveData<HomeState>()
    val animalInfoLiveData: LiveData<HomeState>
        get() = _animalInfoLiveData
    private val _animalInfoListLiveData = MutableLiveData<HomeState>()
    val animalInfoListLiveData: LiveData<HomeState>
        get() = _animalInfoListLiveData

    fun getAllAnimalInfo() {
        // 해당 유저의 등록된 모든 반려동물 정보를 가져와서 반환함
        // 헤더로 사용자id와 동물id를 같이 보내지만 사용자id로만 필터링해서 보여주어야함 (서버 동작)
        _animalInfoListLiveData.value = HomeState.Loading
        Log.e("log", _animalInfoListLiveData.value.toString())
        viewModelScope.launch(ioDispatcher) {
            if (session == null) {
                _animalInfoListLiveData.postValue(HomeState.Error(context.getString(R.string.error_response)))
            } else {
                val infoList = animalRepository.getAllAnimalInfo()
               infoList?.let {
                    if (infoList.isNullOrEmpty()) {
                        _animalInfoListLiveData.postValue(HomeState.Error(context.getString(R.string.not_register_animal)))
                    } else {
                        _animalInfoListLiveData.postValue(HomeState.SuccessList(infoList))
                    }
                } ?: kotlin.run {
                    _animalInfoListLiveData.postValue(HomeState.Error(context.getString(R.string.error_response)))
                }
            }
        }
    }

}