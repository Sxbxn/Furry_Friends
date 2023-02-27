package com.k_bootcamp.furry_friends.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.k_bootcamp.furry_friends.extension.toast
import dagger.hilt.android.qualifiers.ApplicationContext

class NetworkStatus(val context:Context) {
    fun getConnectivityStatus():Boolean{
        val mgr:ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if(mgr.isDefaultNetworkActive){
            true
        }else{
            Toast.makeText(context,"인터넷 연결 안됨, 인터넷 연결 상태를 확인해주세요",Toast.LENGTH_SHORT).show()
            false
        }
    }
}

//class NetworkStatus(private val lifecycleOwner: AppCompatActivity):
//    ConnectivityManager.NetworkCallback(),
//    DefaultLifecycleObserver {
//
//    private val connectivityManager = lifecycleOwner.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//    /** Network를 감지할 Capabilities 선언 **/
//    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
//        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//        .build()
//
//
//    /** 네트워크 상태 체크 **/
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun initNetworkCheck() {
//        val activeNetwork = connectivityManager.activeNetwork
//        if(activeNetwork != null) {
//            Log.e("network", "네트워크 연결되어 있음")
//        } else {
//            lifecycleOwner.toast("연결이 끊겼습니다. 네트워크 연결을 확인해주세요")
//        }
//    }
//
//    /** Network 모니터링 서비스 시작 **/
//    fun enable() {
//        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
//        connectivityManager.registerNetworkCallback(networkRequest, this)
//    }
//
//    /* Network 모니터링 서비스 해제
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun disable() {
//        connectivityManager.unregisterNetworkCallback(this)
//        lifecycleOwner.lifecycle.removeObserver(this)
//    } */
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(owner: LifecycleOwner) {
//        super.onCreate(owner)
//        enable()
//        initNetworkCheck()
//    }
//
//    override fun onDestroy(owner: LifecycleOwner) {
//        super.onDestroy(owner)
//        // Network 모니터링 서비스 종료
//        connectivityManager.unregisterNetworkCallback(this)
//        lifecycleOwner.lifecycle.removeObserver(this)
//    }
//
//    /** Network가 Available 상태이면 Call **/
//    override fun onAvailable(network: Network) {
//        super.onAvailable(network)
//        Log.e("network" ,"networkAvailable()")
//    }
//
//    /** Network가 Available 상태에서 Unavailable로 변경되면 Call **/
//    override fun onLost(network: Network) {
//        super.onLost(network)
//        lifecycleOwner.toast("연결이 끊겼습니다. 네트워크 연결을 확인해주세요")
//    }
//
//}