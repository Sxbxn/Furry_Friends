package com.k_bootcamp.furry_friends.view.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.util.etc.holidayColor
import com.k_bootcamp.furry_friends.util.network.NetworkStatus
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.SelectAnimalAdapter
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.SelectAnimalListListener
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistFragment
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import com.k_bootcamp.furry_friends.view.main.setting.SettingFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private lateinit var mainActivity: MainActivity
    private var session = Application.prefs.session
    private var animalId = Application.prefs.animalId
    // hilt를 통한 viewModel 주입
    override val viewModel: HomeViewModel by viewModels()
    private lateinit var dialogViewGroup: ViewGroup
    private lateinit var dialogView: View
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectAdapter: SelectAnimalAdapter
    private val writing by lazy{
        listOf(
            getString(R.string.sun_writing),
            getString(R.string.mon_writing),
            getString(R.string.tue_writing),
            getString(R.string.wed_writing),
            getString(R.string.thu_writing),
            getString(R.string.fri_writing),
            getString(R.string.sat_writing),
        )
    }
    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
    @SuppressLint("SetTextI18n")
    override fun observeData() {
//        if(NetworkStatus(requireContext()).getConnectivityStatus()){
            // 먼저 홈화면이 실행되면 해당유저의 모든 동물 리스트를 가져온다.
            // 동물 리스트가 비어있으면 (등록 된 동물이 없음) -> 등록하라고 창을 보여준다.
            viewModel.getAllAnimalInfo()
            viewModel.animalInfoListLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is HomeState.Success -> {}
                    is HomeState.Error -> {
                        if (session == null) {
                            cardViewText(binding, R.string.require_login, R.string.log_in_text)
                            binding.userTextView.text = getString(R.string.hello_null)
                            binding.submit.submitButton.setOnClickListener {
                                initLoginButton()
                            }
                        } else {
                            when(it.message) {
                                getString(R.string.not_register_animal) -> {
                                    binding.userTextView.text = String.format(resources.getString(R.string.hello), session)
                                    cardViewText(binding, R.string.submit_animal, R.string.submit)
                                    binding.submit.submitButton.setOnClickListener {
                                        // 등록 페이지로 넘어가기
                                        mainActivity.showFragment(
                                            SubmitAnimalFragment.newInstance(),
                                            SubmitAnimalFragment.TAG
                                        )
                                    }
                                }
                                else -> {
                                    binding.progressbar.toGone()
                                    binding.submit.root.toVisible()
                                    binding.submit.topicTextView.text =
                                        getString(R.string.load_fail_animal_info)
                                    binding.submit.submitButton.text = getString(R.string.retry)
                                    binding.userTextView.text = String.format(resources.getString(R.string.hello), session ?: "Unknown")
                                    binding.submit.submitButton.setOnClickListener {
                                        requireContext().toast("재시도가 되지 않는다면 재로그인을 해주세요!")
                                        observeData()
                                    }
                                }
                            }
                        }
                    }
                    is HomeState.Loading -> {
                        binding.progressbar.toVisible()
                        binding.submit.root.toGone()
                        binding.animalInfo.root.toGone()
                    }
                    is HomeState.SuccessList -> {
                        val month = Calendar.getInstance().get(Calendar.YEAR)
                        val mainAnimal = it.infoList.first { animal -> animal.animalId == animalId }
                        val animalMonth = mainAnimal.birthDay.split(".")[0].toInt()
                        // 한 마리 일때는 변경 불가능
                        when(it.infoList.size) {
                            1 -> binding.animalInfo.change.isEnabled = false
                            else -> {
                                // 변경버튼 활성화하고, 리사이클러뷰에 데이터를 줌
                                binding.animalInfo.change.isEnabled = true
                                initAnimalCardViewButton(it.infoList)
                            }
                        }
                        binding.progressbar.toGone()
                        binding.userTextView.text = String.format(resources.getString(R.string.hello), session)
                        binding.animalInfo.root.toVisible()
                        binding.animalInfo.animalName.text = mainAnimal.name
                        binding.animalInfo.animalAge.text = (month - animalMonth).toString() + "살"
                        binding.animalInfo.animalSex.text = mainAnimal.sex
                        binding.animalInfo.writing.text = writing[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1]
//                        binding.animalInfo.ivMember.load(mainAnimal.imageUrl)
                        Glide.with(binding.animalInfo.ivMember).load(mainAnimal.imageUrl)
                            .placeholder(R.drawable.add_screen_image_placeholder).into(binding.animalInfo.ivMember)
                        binding.animalInfo.add.setOnClickListener {
                            mainActivity.showFragment(SubmitAnimalFragment(), SubmitAnimalFragment.TAG)
                        }
                    }
                }
            }
//        }
    }


    override fun initViews() = with(binding) {
        holidayColor(calendarView)
        initCalendarView()
        goToSettings.setOnClickListener {
            mainActivity.showFragment(SettingFragment.newInstance(), SettingFragment.TAG)
        }
    }

    private fun initCalendarView() = with(binding) {
        calendarView.setOnDateChangedListener { _, date, _ ->
            val fragment = ChecklistFragment()
            fragment.arguments = bundleOf(Pair("flag", 1), Pair("date", date.calendar))
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment).commitAllowingStateLoss()
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

    private fun initAnimalCardViewButton(infoList: List<AnimalResponse>) = with(binding.animalInfo) {
        change.setOnClickListener {
            // 변경하는 창을 띄워서 보여줌
            // 보여주려면 해당 사용자의 모든 동물 정보를 긁어와서 보여주아야함
            alertDialogAnimalSelect(infoList)
        }
    }
    private fun alertDialogAnimalSelect(infoList: List<AnimalResponse>) {
        // 기본 바인딩 설정
        dialogView = layoutInflater.inflate(R.layout.dialog_select_animal, null, false)
        dialogViewGroup = (dialogView as ViewGroup)
        progressBar = dialogView.findViewById(R.id.progressBar)
        recyclerView = dialogView.findViewById(R.id.dialogRecyclerView)

        // 다이얼로그
        val d = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_animal))
            .setView(dialogView)
            .create()
        initDialogRecyclerView(infoList, d)
        d.show()
        // 중요 !! 다이얼로그 뷰를 지워주지 않으면 뷰가 중첩되어 에러가 발생함  -> 어쩔 수 없이 findViewById 써야함
        dialogViewGroup.removeView(dialogView)

    }

    private fun initDialogRecyclerView(infoList: List<AnimalResponse>, d: AlertDialog) = with(recyclerView) {
        layoutManager = GridLayoutManager(requireContext(), 2)
        selectAdapter = SelectAnimalAdapter(infoList, viewModel, requireContext())
        adapter = selectAdapter
        selectAdapter.setItemOnClickListener(object: SelectAnimalListListener{
            override fun onClickItem(position: Int) {
                d.dismiss()
                // 누르면 동물아이디가 바뀜
                Application.prefs.animalId = infoList[position].animalId
                // 바뀐 화면으로 대체
                mainActivity.showFragment(newInstance(), TAG)
            }
        })
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