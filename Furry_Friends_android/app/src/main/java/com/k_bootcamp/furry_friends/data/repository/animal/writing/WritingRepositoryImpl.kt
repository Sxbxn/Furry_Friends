package com.k_bootcamp.furry_friends.data.repository.animal.writing

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import com.k_bootcamp.furry_friends.data.service.WritingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class WritingRepositoryImpl(
    private val writingService: WritingService,
    private val ioDispatcher: CoroutineDispatcher
): WritingRepository {
    override suspend fun getDailyList(): List<DailyResponse>? = withContext(ioDispatcher) {
        val response = writingService.getDailyList()
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }

    override suspend fun getDiagnosisList(): List<DiagnosisResponse>? = withContext(ioDispatcher) {
        val response = writingService.getDiagnosisList()
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteDailyModel(modelId: Int): String? = withContext(ioDispatcher) {
        val response = writingService.deleteDailyModel(modelId)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }

    override suspend fun deleteDiagnosisModel(modelId: Int): String? = withContext(ioDispatcher) {
        val response = writingService.deleteDiagnosisModel(modelId)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }

    override suspend fun submitDailyWriting(
        body: MultipartBody.Part,
        jsonDailyWriting: RequestBody
    ): String? = withContext(ioDispatcher) {
        val response = writingService.submitDailyWriting(body, jsonDailyWriting)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }

    override suspend fun submitDiagnosisWriting(
        body: MultipartBody.Part,
        jsonDiagnosisWriting: RequestBody
    ): String? = withContext(ioDispatcher) {
        val response = writingService.submitDiagnosisWriting(body, jsonDiagnosisWriting)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    }


}