package com.k_bootcamp.furry_friends.view.main.home

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import com.k_bootcamp.furry_friends.util.holidayColor
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private lateinit var mainActivity: MainActivity
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
            cardViewText(this, R.string.require_login, R.string.log_in_text)
            submit.submitButton.setOnClickListener {
                initLoginButton()
            }
        } else {
            val animal = viewModel.getAnimalInfo()
            if(animal == null) {
                cardViewText(this, R.string.submit_animal, R.string.submit)
                submit.submitButton.setOnClickListener {
                    // 등록 페이지로 넘어가기
                    mainActivity.showFragment(SubmitAnimalFragment.newInstance(), SubmitAnimalFragment.TAG)
                }
            } else {
                submit.root.visibility = View.GONE
                animalInfo.root.visibility = View.VISIBLE
                observeData()
            }
        }
        holidayColor(calendarView)
    }
    private fun cardViewText(binding: FragmentHomeBinding, @StringRes id1: Int, @StringRes id2: Int){
        binding.submit.root.visibility = View.VISIBLE
        binding.animalInfo.root.visibility = View.GONE
        binding.submit.topicTextView.text = getString(id1)
        binding.submit.submitButton.text = getString(id2)
    }
    private fun initLoginButton() {
        startActivity(LoginActivity.newIntent(requireContext()))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    companion object {
        fun newInstance() = HomeFragment().apply {

        }
        const val TAG = "HOME_FRAGMENT"
    }
}