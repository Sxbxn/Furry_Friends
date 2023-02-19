package com.k_bootcamp.furry_friends.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_bootcamp.furry_friends.data.service.AnimalService
import com.k_bootcamp.furry_friends.data.service.UserService
import com.k_bootcamp.furry_friends.data.service.WritingService
import com.k_bootcamp.furry_friends.data.url.Url
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetModule {

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun providesUserService(okHttpClient: OkHttpClient): UserService {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
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
            .addConverterFactory(GsonConverterFactory.create(gson))
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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Url.BASE_URL)
            .client(okHttpClient)
            .build().create(WritingService::class.java)
    }
}