package com.k_bootcamp.furry_friends.di

import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepositoryImpl
import com.k_bootcamp.furry_friends.data.repository.user.UserRepository
import com.k_bootcamp.furry_friends.data.repository.user.UserRepositoryImpl
import com.k_bootcamp.furry_friends.data.service.AnimalService
import com.k_bootcamp.furry_friends.data.service.UserService
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        userService: UserService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UserRepository {
        return UserRepositoryImpl(userService, ioDispatcher)
    }
    @Singleton
    @Provides
    fun provideAnimalRepository(
        animalService: AnimalService,
        routineDao: RoutineDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AnimalRepository {
        return AnimalRepositoryImpl(animalService, routineDao, ioDispatcher)
    }
}