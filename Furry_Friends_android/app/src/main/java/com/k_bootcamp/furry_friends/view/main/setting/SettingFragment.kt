package com.k_bootcamp.furry_friends.view.main.setting

import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.furry_friends.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment: BaseFragment<SettingViewModel, FragmentSettingBinding>() {
    override val viewModel: SettingViewModel by viewModels()

    override fun getViewBinding(): FragmentSettingBinding = FragmentSettingBinding.inflate(layoutInflater)
    override fun observeData() {
    }
    companion object {
        fun newInstance() = SettingFragment().apply {

        }
        const val TAG = "SETTING_FRAGMENT"
    }
}