package com.k_bootcamp.furry_friends.view.main.TabWriting

import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.furry_friends.databinding.FragmentTabWritingBinding
import com.k_bootcamp.furry_friends.view.main.writing.TabWritingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabWritingFragment: BaseFragment<TabWritingViewModel, FragmentTabWritingBinding>() {
    override val viewModel: TabWritingViewModel by viewModels()
    override fun getViewBinding(): FragmentTabWritingBinding = FragmentTabWritingBinding.inflate(layoutInflater)
    override fun observeData() {
    }
    companion object {
        fun newInstance() = TabWritingFragment().apply {

        }
        const val TAG = "TAB_WRITING_FRAGMENT"
    }
}