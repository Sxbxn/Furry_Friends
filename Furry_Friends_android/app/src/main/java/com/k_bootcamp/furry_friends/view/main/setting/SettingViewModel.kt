package com.k_bootcamp.furry_friends.view.main.setting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.data.repository.user.UserRepository
import com.k_bootcamp.furry_friends.util.etc.bitmapToFile
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.furry_friends.view.main.home.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
): BaseViewModel() {
    private val _isSuccess = MutableLiveData<SettingState>()
    val isSuccess: LiveData<SettingState>
        get() = _isSuccess
    private val _animalInfoLiveData = MutableLiveData<SettingState>()
    val animalInfoLiveData: LiveData<SettingState>
        get() = _animalInfoLiveData

    fun logout() {
        _isSuccess.value = SettingState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            // 추후 반환값 수정해야할듯
            val response = userRepository.logout()
        }
    }

    fun withdrawUser() {
        _isSuccess.value = SettingState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            // 추후 추가
        }

    }

    fun deleteProfile() {
        _isSuccess.value = SettingState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = animalRepository.deleteAnimalInfo()
            if(response == null) {
                _isSuccess.postValue(SettingState.Error(context.getString(R.string.error)))
            } else {
                _isSuccess.postValue(SettingState.Success(response))
            }
        }
    }

    fun getAnimalInfo() {
        _isSuccess.value = SettingState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val info = animalRepository.getAnimalInfo()
            info?.let {
                _animalInfoLiveData.postValue(
                    SettingState.SuccessGetInfo(
                    it.animalId,
                    it.userId,
                    it.name,
                    it.birthDay,
                    it.weight,
                    it.sex,
                    it.isNeutered,
                    it.imageUrl
                ))
            } ?: kotlin.run {
                _animalInfoLiveData.postValue(SettingState.Error(context.getString(R.string.error_response)))
            }
        }
    }

    fun updateAnimalProfile(body: MultipartBody.Part, jsonUpdateProfile: RequestBody) {
        _isSuccess.value = SettingState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = animalRepository.updateAnimalProfile(body, jsonUpdateProfile)
            if(response == null) {
                _isSuccess.postValue(SettingState.Error(context.getString(R.string.error)))
            } else {
                _isSuccess.postValue(SettingState.Success(response))
            }
        }
    }
    // url에서 이미지 가져오기 (수정 전 이미지를 위해)
    suspend fun getFile(url: String): File {
        val deffered = CoroutineScope(Dispatchers.IO).async {
            getOriginalBitmap(url)
        }.await()
        return bitmapToFile(deffered, context.applicationContext.filesDir.path.toString())
    }

    private fun getOriginalBitmap(url: String): Bitmap =
        URL(url).openStream().use {
            BitmapFactory.decodeStream(it)
        }


}