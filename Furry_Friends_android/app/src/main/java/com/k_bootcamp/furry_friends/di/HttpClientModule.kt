package com.k_bootcamp.furry_friends.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class HttpClientModule {

    @Singleton
    @Provides
    fun providesHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            })
//            .connectTimeout(10, TimeUnit.MINUTES)
//            .writeTimeout(10,TimeUnit.MINUTES)
//            .readTimeout(10, TimeUnit.MINUTES)
            .build()
    }

}