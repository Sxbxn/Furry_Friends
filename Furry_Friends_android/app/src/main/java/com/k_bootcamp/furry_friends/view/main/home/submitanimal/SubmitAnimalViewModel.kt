package com.k_bootcamp.furry_friends.view.main.home.submitanimal

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    fun submitAnimal(body: MultipartBody.Part, json: RequestBody) {
        _isSuccess.value = SubmitAnimalState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = animalRepository.submitAnimal(body, json)
            response?.let{
                _isSuccess.postValue(SubmitAnimalState.Success(it))
            } ?: kotlin.run {
                _isSuccess.postValue(SubmitAnimalState.Error(context.getString(R.string.failed_submit_animal)))
            }
        }
    }

}