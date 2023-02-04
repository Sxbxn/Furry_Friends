package com.k_bootcamp.furry_friends.view.main.home

import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<HomeViewModel, FragmentHomeBinding>() {
    override val viewModel: HomeViewModel by viewModels()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
    override fun observeData() {
    }
    companion object {
        fun newInstance() = HomeFragment().apply {

        }
        const val TAG = "HOME_FRAGMENT"
    }
}