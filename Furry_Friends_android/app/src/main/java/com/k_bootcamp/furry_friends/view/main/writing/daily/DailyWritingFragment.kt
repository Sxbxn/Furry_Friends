package com.k_bootcamp.furry_friends.view.main.writing.daily

import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.furry_friends.databinding.FragmentDayWritingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyWritingFragment: BaseFragment<DailyWritingViewModel, FragmentDayWritingBinding>() {
    override val viewModel: DailyWritingViewModel by viewModels()

    override fun getViewBinding(): FragmentDayWritingBinding = FragmentDayWritingBinding.inflate(layoutInflater)
    override fun observeData() {

    }

    override fun initViews() {
        initShimmer()
    }
    private fun initShimmer() = with(binding) {
        shimmerLayout.hideShimmer()
    }
    companion object {
        fun newInstance() = DailyWritingFragment().apply {

        }
        const val TAG = "DAILY_WRITING_FRAGMENT"
    }


}