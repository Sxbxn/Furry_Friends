package com.k_bootcamp.furry_friends.di


import android.content.Context
import com.k_bootcamp.furry_friends.util.network.AuthInterceptor
import com.k_bootcamp.furry_friends.util.network.NoConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class HttpClientModule {

    @Singleton
    @Provides
    fun providesHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(AuthInterceptor())
            .addInterceptor(NoConnectionInterceptor(context))
//            .connectTimeout(10, TimeUnit.MINUTES)
//            .writeTimeout(10,TimeUnit.MINUTES)
//            .readTimeout(10, TimeUnit.MINUTES)
            .build()
    }
}