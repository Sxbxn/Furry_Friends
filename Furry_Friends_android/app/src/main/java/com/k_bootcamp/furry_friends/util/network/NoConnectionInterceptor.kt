package com.k_bootcamp.furry_friends.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.k_bootcamp.furry_friends.extension.toast
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class NoConnectionInterceptor(
    val context: Context
) : Interceptor {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    override fun intercept(chain: Interceptor.Chain): Response {
        val handler = Handler(Looper.getMainLooper())
        return if (!isConnectionOn()) {
            throw NoConnectivityException(context)
        } else {
            chain.proceed(chain.request())
        }
    }

    private fun preAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val activeNetwork = connectivityManager.isDefaultNetworkActive
        if (activeNetwork)
            return true
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun isConnectionOn(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.M
        ) {
            postAndroidMInternetCheck(connectivityManager)
        } else {
            preAndroidMInternetCheck(connectivityManager)
        }
    }

    class NoConnectivityException(val context: Context) : IOException() {
        override val message: String
            get() = "인터넷 연결이 끊겼습니다. WIFI나 데이터 연결을 확인해주세요"
        init{
            context.toast("인터넷 연결이 끊겼습니다. WIFI나 데이터 연결을 확인해주세요")
        }
        fun toast() = context.toast("인터넷 연결이 끊겼습니다. WIFI나 데이터 연결을 확인해주세요")
    }


}