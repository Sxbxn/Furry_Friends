package com.k_bootcamp.furry_friends.view.main.home

import android.content.Context
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.util.holidayColor
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistFragment
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private lateinit var mainActivity: MainActivity
    private var session = Application.prefs.session
    override val viewModel: HomeViewModel by viewModels()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
    override fun observeData() {
        viewModel.getAnimalInfo()
        viewModel.animalInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.Success -> {
                    val month = Calendar.getInstance().get(Calendar.YEAR)
                    val animalMonth = it.birthDay.split(".")[0].toInt()
                    binding.progressbar.toGone()
                    binding.animalInfo.root.toVisible()
                    binding.animalInfo.animalName.text = it.name
                    binding.animalInfo.animalAge.text = (month - animalMonth).toString()
                    binding.animalInfo.animalSex.text = it.sex
                    binding.animalInfo.ivMember.load(it.imageUrl)
                    // TO-DO 이미지, 할 일은 나중에 더 얘기해보고 넣기
                }
                is HomeState.Error -> {
                    if (session == null) {
                        cardViewText(binding, R.string.require_login, R.string.log_in_text)
                        binding.submit.submitButton.setOnClickListener {
                            initLoginButton()
                        }
                    } else {
                        if (viewModel.animalInfoLiveData.value == null) {
                            cardViewText(binding, R.string.submit_animal, R.string.submit)
                            binding.submit.submitButton.setOnClickListener {
                                // 등록 페이지로 넘어가기
                                mainActivity.showFragment(
                                    SubmitAnimalFragment.newInstance(),
                                    SubmitAnimalFragment.TAG
                                )
                            }
                        } else {
                            binding.progressbar.toGone()
                            binding.submit.root.toVisible()
                            binding.submit.topicTextView.text =
                                getString(R.string.load_fail_animal_info)
                            binding.submit.submitButton.text = getString(R.string.retry)
                            binding.submit.submitButton.setOnClickListener {
                                observeData()
                            }
                        }
                    }
                }
                is HomeState.Loading -> {
                    binding.progressbar.toVisible()
                    binding.submit.root.toGone()
                    binding.animalInfo.root.toGone()
                }
            }
        }
    }

    override fun initViews() {
        Log.e("session", session.toString())
        holidayColor(binding.calendarView)
        initCalendarView()
    }

    private fun initCalendarView() = with(binding) {
        calendarView.setOnDateChangedListener { widget, date, selected ->
            Log.e("date", date.toString())
            val fragment = ChecklistFragment()
            fragment.arguments = bundleOf(Pair("flag", 1), Pair("date", date.calendar))
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment).commitAllowingStateLoss()
//                // 1로 접근하면 보여주기만
//                ChecklistFragment.newInstance()
//                    .apply { arguments = bundleOf(Pair("flag", 1), Pair("date", date.calendar)) }
//

        }
    }


    private fun cardViewText(
        binding: FragmentHomeBinding,
        @StringRes id1: Int,
        @StringRes id2: Int
    ) {
        binding.submit.root.toVisible()
        binding.animalInfo.root.toGone()
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