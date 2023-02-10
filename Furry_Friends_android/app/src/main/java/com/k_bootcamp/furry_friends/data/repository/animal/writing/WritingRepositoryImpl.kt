package com.k_bootcamp.furry_friends.data.repository.animal.writing

import com.k_bootcamp.furry_friends.data.response.writing.DailyResponse
import com.k_bootcamp.furry_friends.data.response.writing.DiagnosisResponse
import com.k_bootcamp.furry_friends.data.service.WritingService
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.model.writing.Daily
import com.k_bootcamp.furry_friends.model.writing.Diagnosis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

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
}