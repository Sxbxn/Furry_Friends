package com.k_bootcamp.furry_friends.view.main.routine

import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import com.k_bootcamp.furry_friends.databinding.FragmentRoutineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineFragment: BaseFragment<RoutineViewModel, FragmentRoutineBinding>() {

    private var session = Application.prefs.session
    override val viewModel: RoutineViewModel by viewModels()

    override fun getViewBinding(): FragmentRoutineBinding = FragmentRoutineBinding.inflate(layoutInflater)
    override fun observeData() {
    }
    companion object {
        fun newInstance() = RoutineFragment().apply {

        }
        const val TAG = "ROUTINE_FRAGMENT"
    }
}