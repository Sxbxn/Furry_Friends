package com.k_bootcamp.furry_friends.view.main.writing

import android.content.Context
import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.k_bootcamp.furry_friends.databinding.FragmentTabWritingBinding
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.writing.daily.DailyWritingFragment
import com.k_bootcamp.furry_friends.view.main.writing.diagnosis.DiagnosisWritingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TabWritingFragment: BaseFragment<TabWritingViewModel, FragmentTabWritingBinding>() {
    override val viewModel: TabWritingViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    override fun getViewBinding(): FragmentTabWritingBinding = FragmentTabWritingBinding.inflate(layoutInflater)
    override fun observeData() {
        // 현재 탭에 해당되는 데이터를 가져와서 보여주기
    }


    override fun initViews() {
        changeView(0)
        tabSelected()
    }
    private fun tabSelected() = with(binding) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos = tab?.position
                changeView(pos!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun changeView(position: Int) {
        when (position) {
            0 -> {
                initFloatingButton(position)
                getDailyList()
            }
            1 -> {
                initFloatingButton(position)
                getDiagnosisList()
            }
        }
    }
    private fun initFloatingButton(position: Int) = with(binding) {
        when (position) {
            0 -> {
                addRoutineButton.setOnClickListener {
                    mainActivity.showFragment(DailyWritingFragment.newInstance(), DailyWritingFragment.TAG)
                }
            }
            1 -> {
                addRoutineButton.setOnClickListener {
                    mainActivity.showFragment(DiagnosisWritingFragment.newInstance(), DiagnosisWritingFragment.TAG)
                }
            }
        }
    }

    private fun getDailyList() = CoroutineScope(Dispatchers.IO).launch {

    }
    private fun getDiagnosisList() = CoroutineScope(Dispatchers.IO).launch {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    companion object {
        fun newInstance() = TabWritingFragment().apply {

        }
        const val TAG = "TAB_WRITING_FRAGMENT"
    }
}