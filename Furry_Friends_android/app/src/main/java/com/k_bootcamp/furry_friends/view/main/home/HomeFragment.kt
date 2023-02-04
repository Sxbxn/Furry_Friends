package com.k_bootcamp.furry_friends.view.main.home

import android.view.View
import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import com.k_bootcamp.furry_friends.util.holidayColor
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private var session = Application.prefs.session
    override val viewModel: HomeViewModel by viewModels()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
    override fun observeData() {
    }

    override fun initViews() {
        cardViewBinding()
    }
    private fun cardViewBinding() = with(binding) {
        if(session == null) {
            submit.root.visibility = View.VISIBLE
            animalInfo.root.visibility = View.GONE
            submit.topicTextView.text = getString(R.string.require_login)
            submit.submitButton.text = getString(R.string.log_in_text)
            submit.submitButton.setOnClickListener {
                initLoginButton()
            }
        } else {
            submit.root.visibility = View.GONE
            animalInfo.root.visibility = View.VISIBLE
            viewModel.getAnimalInfo()
        }
        holidayColor(calendarView)
    }
    private fun initLoginButton() {
        startActivity(LoginActivity.newIntent(requireContext()))
    }
    companion object {
        fun newInstance() = HomeFragment().apply {

        }
        const val TAG = "HOME_FRAGMENT"
    }
}