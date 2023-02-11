package com.k_bootcamp.furry_friends.util.network

import android.util.Log
import com.k_bootcamp.Application
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("login",  Application.prefs.session ?: "")
                .addHeader("curr_animal", Application.prefs.animalId.toString())
                .build()
        Log.e("headers",request.headers.toString())
        Log.e("bodies", request.body?.contentType().toString())
        return chain.proceed(request)
    }
}