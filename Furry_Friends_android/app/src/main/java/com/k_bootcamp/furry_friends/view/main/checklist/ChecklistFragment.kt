package com.k_bootcamp.furry_friends.view.main.checklist

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChecklistFragment: BaseFragment<ChecklistViewModel, FragmentDayDetailBinding>() {
    override val viewModel: ChecklistViewModel by viewModels()


    override fun getViewBinding(): FragmentDayDetailBinding = FragmentDayDetailBinding.inflate(layoutInflater)

    override fun observeData() {

    }
    companion object {
        fun newInstance() = ChecklistFragment().apply {

        }
        const val TAG = "CHECKLIST_FRAGMENT"
    }
}