package com.k_bootcamp.furry_friends.view.main.home.submitanimal

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fc.baeminclone.screen.base.BaseViewModel
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.data.repository.user.UserRepository
import com.k_bootcamp.furry_friends.model.animal.Animal

import com.k_bootcamp.furry_friends.view.main.signin.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubmitAnimalViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    @ApplicationContext private val context: Context
): BaseViewModel() {
    private val _isSuccess = MutableLiveData<SubmitAnimalState>()
    val isSuccess: LiveData<SubmitAnimalState>
        get() = _isSuccess

    val nameLiveData: MutableLiveData<String> = MutableLiveData()
    val birthDayLiveData: MutableLiveData<String> = MutableLiveData()
    val weightLiveData: MutableLiveData<String> = MutableLiveData()

    fun submitAnimal(animal: Animal) {
        _isSuccess.value = SubmitAnimalState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = animalRepository.submitAnimal(animal)
            response?.let{
                _isSuccess.postValue(SubmitAnimalState.Success(it.isOk))
            } ?: kotlin.run {
                _isSuccess.postValue(SubmitAnimalState.Error(context.getString(R.string.failed_submit_animal)))
            }
        }
    }

}