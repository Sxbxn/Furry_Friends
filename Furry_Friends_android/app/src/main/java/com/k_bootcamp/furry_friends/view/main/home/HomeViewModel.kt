package com.k_bootcamp.furry_friends.view.main.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fc.baeminclone.screen.base.BaseViewModel
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.data.response.user.Session
import com.k_bootcamp.furry_friends.model.animal.Animal
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    @ApplicationContext private val context: Context
): BaseViewModel() {
    private val session = Application.prefs.session?.let { Session(it) }
    private val _animalInfoLiveData = MutableLiveData<HomeState>()
    val animalInfoLiveData: LiveData<HomeState>
        get() = _animalInfoLiveData


    fun getAnimalInfo() {
        // 해당 유저의 등록된 반려동물 정보를 가져와서 반환함
        _animalInfoLiveData.value = HomeState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val info = animalRepository.getAnimalInfo(session)
            info?.let {
                _animalInfoLiveData.postValue(HomeState.Success(
                    it.animalId,
                    it.userId,
                    it.name,
                    it.birthDay,
                    it.weight,
                    it.sex,
                    it.isNeutered
                ))
            } ?: kotlin.run {
                _animalInfoLiveData.postValue(HomeState.Error(context.getString(R.string.error_response)))
            }
        }
    }

}