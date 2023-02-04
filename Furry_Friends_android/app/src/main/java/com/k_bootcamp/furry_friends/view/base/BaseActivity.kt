package com.fc.baeminclone.screen.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

// 액티비티에 상속을 주기위한 틀 (추상화)
abstract class BaseActivity<VM: BaseViewModel, VB: ViewBinding>: AppCompatActivity() {
    // 뷰모델
    abstract val viewModel: VM
    // 뷰바인딩
    protected lateinit var binding: VB
    // fetchData의 틀 (Job 반환)
    private lateinit var fetchJob: Job

    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        initState()
    }

    // state 초기화
    open fun initState() {
        initViews()
        fetchJob = viewModel.fetchData()
        observeData()
    }

    // 뷰 초기설정
    open fun initViews(){

    }

    // LiveData를 구독하기 위한 함수(옵저빙)
    abstract fun observeData()

    override fun onDestroy() {
        if(fetchJob.isActive) {
            fetchJob.cancel()
        }
        super.onDestroy()
    }
}