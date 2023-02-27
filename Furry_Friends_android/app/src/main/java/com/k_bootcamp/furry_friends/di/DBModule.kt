package com.k_bootcamp.furry_friends.di

import android.content.Context
import androidx.room.Room
import com.k_bootcamp.furry_friends.data.db.RoutineDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DBModule {
    @Singleton
    @Provides
    fun provideRoutineDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            RoutineDatabase::class.java,
            "routine_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRoutineDao(appDatabase: RoutineDatabase) = appDatabase.routineDao()
}