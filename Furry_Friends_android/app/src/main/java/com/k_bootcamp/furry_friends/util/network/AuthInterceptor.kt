package com.k_bootcamp.furry_friends.util.network

import android.util.Log
import com.k_bootcamp.Application
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("user_id",  Application.prefs.session ?: "")
                .addHeader("animal_id", Application.prefs.animalId.toString())
                .build()
        Log.e("headers",request.headers.toString())
        Log.e("bodies", request.body?.contentType().toString())
        return chain.proceed(request)
    }
}