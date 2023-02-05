package com.k_bootcamp.furry_friends.data.repository.animal

import com.k_bootcamp.furry_friends.data.response.animal.SubmitAnimalResponse
import com.k_bootcamp.furry_friends.data.response.user.Session
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

    override suspend fun getAnimalInfo(session: Session?): Animal? = withContext(ioDispatcher) {
        session?.let{
            val response = animalService.getAnimalInfo(session)
            if(response.isSuccessful){
                response.body()
            } else {
                null
            }
        }
    }


}