package com.k_bootcamp.furry_friends.view.main.writing.diagnosis

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.writing.WritingRepository
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class DiagnosisWritingViewModel @Inject constructor(
    private val writingRepository: WritingRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val _isSuccess = MutableLiveData<DiagnosisState>()
    val isSuccess: LiveData<DiagnosisState>
        get() = _isSuccess

    fun submitDiagnosisWriting(body: MultipartBody.Part, jsonDailyWriting: RequestBody) {
        _isSuccess.value = DiagnosisState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = writingRepository.submitDiagnosisWriting(body, jsonDailyWriting)
            response?.let {
                _isSuccess.postValue(DiagnosisState.Success(it))
            } ?: kotlin.run {
                _isSuccess.postValue(DiagnosisState.Error(context.getString(R.string.failed_submit_animal)))
            }
        }
    }
}