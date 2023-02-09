package com.k_bootcamp.furry_friends.view.main.writing.diagnosis

import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.furry_friends.databinding.FragmentDiagnosisWritingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiagnosisWritingFragment: BaseFragment<DiagnosisWritingViewModel, FragmentDiagnosisWritingBinding>() {
    override val viewModel: DiagnosisWritingViewModel by viewModels()

    override fun getViewBinding(): FragmentDiagnosisWritingBinding = FragmentDiagnosisWritingBinding.inflate(layoutInflater)

    override fun observeData() {

    }
    override fun initViews() {
        initShimmer()
    }
    private fun initShimmer() = with(binding) {
        shimmerLayout.hideShimmer()
    }
    companion object {
        fun newInstance() = DiagnosisWritingFragment().apply {

        }
        const val TAG = "DIAGNOSIS_WRITING_FRAGMENT"
    }

}