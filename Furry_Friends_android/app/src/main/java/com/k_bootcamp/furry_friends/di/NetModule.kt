package com.k_bootcamp.furry_friends.di

import android.content.Context
import com.k_bootcamp.furry_friends.data.service.AnimalService
import com.k_bootcamp.furry_friends.data.service.UserService
import com.k_bootcamp.furry_friends.data.service.WritingService
import com.k_bootcamp.furry_friends.data.url.Url
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.util.network.AuthInterceptor
import com.k_bootcamp.furry_friends.util.network.NoConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetModule {

    @Singleton
    @Provides
    fun providesUserService(okHttpClient: OkHttpClient): UserService {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Url.BASE_URL)
            .client(okHttpClient)
            .build().create(UserService::class.java)
    }

    @Singleton
    @Provides
    fun providesAnimalService(
        okHttpClient: OkHttpClient,
    ): AnimalService {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Url.BASE_URL)
            .client(okHttpClient)
            .build().create(AnimalService::class.java)
    }

    @Singleton
    @Provides
    fun providesWritingService(
        okHttpClient: OkHttpClient,
    ): WritingService {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Url.BASE_URL)
            .client(okHttpClient)
            .build().create(WritingService::class.java)
    }
}