package com.k_bootcamp.furry_friends.data.repository.animal

import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.service.AnimalService
import com.k_bootcamp.furry_friends.data.service.UserService
import com.k_bootcamp.furry_friends.model.animal.Animal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AnimalRepositoryImpl(
    private val animalService: AnimalService,
    private val ioDispatcher: CoroutineDispatcher
): AnimalRepository {
    override suspend fun submitAnimal(animal: Animal): SubmitAnimalResponse?= withContext(ioDispatcher){
        val response = animalService.submitAnimal(animal)
        if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }


}