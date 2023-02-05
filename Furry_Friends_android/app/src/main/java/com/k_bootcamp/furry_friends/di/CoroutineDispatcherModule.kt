package com.k_bootcamp.furry_friends.di

import android.content.Context
import com.k_bootcamp.furry_friends.util.etc.DefaultDispatcher
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import com.k_bootcamp.furry_friends.util.etc.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutineDispatcherModule {
    // 타입이 같은 주입은 duplicate error를 일으키키에 어노테이션 quailifier 한정자로 qnsfl
    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}