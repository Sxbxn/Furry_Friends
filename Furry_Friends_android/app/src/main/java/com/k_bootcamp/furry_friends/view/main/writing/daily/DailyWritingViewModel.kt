package com.k_bootcamp.furry_friends.view.main.writing.daily

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.animal.writing.WritingRepository
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import com.k_bootcamp.furry_friends.util.etc.bitmapToFile
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class DailyWritingViewModel @Inject constructor(
    private val writingRepository: WritingRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val _isSuccess = MutableLiveData<DailyState>()
    val isSuccess: LiveData<DailyState>
        get() = _isSuccess
    private var file: File? = null

    // 일상기록 등록하기
    fun submitDailyWriting(body: MultipartBody.Part, jsonDailyWriting: RequestBody) {
        _isSuccess.value = DailyState.Loading
        viewModelScope.launch(ioDispatcher) {
            val response = writingRepository.submitDailyWriting(body, jsonDailyWriting)
            response?.let {
                _isSuccess.postValue(DailyState.Success(it))
            } ?: kotlin.run {
                _isSuccess.postValue(DailyState.Error(context.getString(R.string.failed_submit_daily)))
            }
        }
    }

    // 일상기록 수정하기
    fun updateDailyWriting(
        body: MultipartBody.Part,
        jsonDailyWriting: RequestBody,
        writingId: String
    ) {
        _isSuccess.value = DailyState.Loading
        viewModelScope.launch(ioDispatcher) {
            val response =
                writingRepository.updateDailyWriting(body, jsonDailyWriting, writingId.toInt())
            response?.let {
                _isSuccess.postValue(DailyState.Success(it))
            } ?: kotlin.run {
                _isSuccess.postValue(DailyState.Error(context.getString(R.string.failed_update_animal)))
            }
        }


    }

    // url에서 이미지 가져오기 (수정 전 이미지를 위해)
    suspend fun getFile(url: String): File? {
        val deffered = CoroutineScope(ioDispatcher).async {
            getOriginalBitmap(url)
        }.await()
        return bitmapToFile(deffered, context.applicationContext.filesDir.path.toString())
    }

    private fun getOriginalBitmap(url: String): Bitmap =
        URL(url).openStream().use {
            BitmapFactory.decodeStream(it)
        }
}