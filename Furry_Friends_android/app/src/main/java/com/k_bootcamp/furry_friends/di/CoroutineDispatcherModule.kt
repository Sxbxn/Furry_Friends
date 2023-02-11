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
    // 아래 Dispatcher들은 다 같은 CoroutineDispatcher 타입임
    // 타입이 같은 주입은 duplicate error를 일으키키에 어노테이션 qualifier 한정자로 분리
    // qualifier annotation을 만들먼 hilt에서 매번 사용할 수 있는 각자의 바인딩을 만들 수 있음

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