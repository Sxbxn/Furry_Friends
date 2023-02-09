package com.k_bootcamp.furry_friends.view.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel(){

    // state를 저장하는 번들
    protected var stateBundle: Bundle? = null

    // 데이터를 가져오는 공통 코루틴 함수  상속받아 사용
    open fun fetchData(): Job = viewModelScope.launch {

    }

    // bundle 저장 함수   프래그먼트 라이프사이클 종료 전 까지 유지시킴
    open fun storeState(stateBundle: Bundle) {
        this.stateBundle = stateBundle
    }


}