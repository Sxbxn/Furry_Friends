package com.fc.baeminclone.screen.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

// 액티비티에 상속을 주기위한 틀 (추상화)
abstract class BaseFragment<VM: BaseViewModel, VB: ViewBinding>: Fragment() {
    // 뷰모델
    abstract val viewModel: VM
    // 뷰바인딩
    protected lateinit var binding: VB
    // fetchData의 틀 (Job 반환)
    private lateinit var fetchJob: Job

    abstract fun getViewBinding(): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
    }

    // state 초기화
    open fun initState() {
        arguments?.let{
            viewModel.storeState(it)
        }
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