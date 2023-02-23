package com.k_bootcamp.furry_friends.view.main.checklist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.response.animal.CheckListResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.data.response.animal.RoutineStatusResponse
import com.k_bootcamp.furry_friends.model.animal.SendRoutine
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.CheckList
import com.k_bootcamp.furry_friends.model.animal.RoutineStatus
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.RoutineCheckAdapter
import com.k_bootcamp.furry_friends.view.adapter.RoutineCheckReadOnlyAdapter
import com.k_bootcamp.furry_friends.view.main.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
// 0으로 접근하면 작성 상태, 1로 접근하면 read only 하게
class ChecklistFragment : BaseFragment<ChecklistViewModel, FragmentDayDetailBinding>() {
    override val viewModel: ChecklistViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private lateinit var loading: LoadingDialog
    private var session = Application.prefs.session
    private var animalId = Application.prefs.animalId
    private lateinit var poobStatusStr: String
    private lateinit var checkList: CheckList
    private lateinit var adapter: RoutineCheckAdapter
    private var eatQuantityStr: String = ""
    private var other: String = ""
    private var date: String = ""
    private lateinit var routineStatusList: List<RoutineStatus>
    private lateinit var mainActivity: MainActivity
    private var args: Bundle? = null
    private var flag = 0

    override fun getViewBinding(): FragmentDayDetailBinding =
        FragmentDayDetailBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun observeData() {
        // 작성 상태
        if(args?.getInt("flag") == 0) {
            viewModel.initRoutines()
            viewModel.routineLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is CheckListState.Done -> {
                        context?.toast("등록이 완료되었습니다. 메인화면으로 돌아갑니다.")
                        loading.dismiss()
                        mainActivity.showFragment(HomeFragment.newInstance(),HomeFragment.TAG)
                    }
                    is CheckListState.Loading -> {
                        loading.setVisible()
                    }
                    is CheckListState.Error -> {
                        loading.setError()
                        binding.recyclerView.toGone()
                        when (it.message) {
                            getString(R.string.not_loged_in) -> {
                                binding.infoTextView.text = "로그인 되지 않았어요!"
                            }
                            getString(R.string.not_register_animal) -> {
                                binding.submitButton.isEnabled = false
                                binding.infoTextView.text = "반려동물 등록이 되지 않았어요!"
                            }
                            getString(R.string.exist_routine) -> {
                                requireContext().toast(it.message)
                            }
                            getString(R.string.no_response) -> {
                                binding.noteCardView.toVisible()
                                binding.editTextAnimalOther.setText("정보 없음")
                                binding.editTextAnimalEat.setText("정보 없음")
                                binding.recyclerView.toGone()
                            }
                            else -> {
                                binding.infoTextView.text = "알 수 없는 오류가 발생했어요"
                            }
                        }
                    }
                    is CheckListState.Success -> {
                        loading.dismiss()
                        binding.recyclerView.toVisible()
                        binding.shimmerLayout.hideShimmer()
                        binding.submitButton.isEnabled = true
                        // 작성 창에 루틴 체크할 수 있게 보여주는데 해당 요일에만 있는 루틴만 걸러서 보여줌
                        // response 는 mon, tue... 문자열 형식으로 반환됨
                        val todayRoutines = it.routines.filter { r-> r.weekDay == getDayOfWeek() }
                        initRecyclerView(todayRoutines)
                    }
                    is CheckListState.ReadDone -> {}
                }
            }
        } else if(args?.getInt("flag") == 1){ // read only
            // 날짜 정보를 넘겨야함
            viewModel.getDatas(date, getDayOfWeekFromDate(date))
            viewModel.routineLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is CheckListState.Done -> {}
                    is CheckListState.ReadDone -> {
                        context?.toast("체크리스트 정보 로딩 완료")
                        loading.dismiss()
                        binding.shimmerLayout.hideShimmer()
                        binding.noteCardView.toVisible()
                        binding.recyclerView.toVisible()
                        if(it.response.checklistDefault != null) {
                            initReadOnlyView(it.response.checklistDefault)
                            if(it.response.checklistRoutine != null)
                                initRecyclerViewReadOnly(it.response.checklistRoutine)
                        } else {
                            binding.noteCardView.toGone()
                            binding.infoTextView.toVisible()
                            binding.infoTextView.text = "등록 된 정보가 없어요..!"
                        }
                    }
                    is CheckListState.Loading -> {
                        loading.setVisible()
                    }
                    is CheckListState.Error -> {
                        loading.setError()
                        binding.noteCardView.toGone()
                        binding.recyclerView.toGone()
                        when (it.message) {
                            getString(R.string.not_loged_in) -> {
                                binding.infoTextView.text = getString(R.string.waiting_you)
                            }
                            getString(R.string.not_register_animal) -> {
                                binding.infoTextView.text = getString(R.string.not_register_animals)
                            }
                            getString(R.string.exist_routine) -> {
                                requireContext().toast(it.message)
                            }
                            else -> {
                                binding.infoTextView.text = getString(R.string.unknown_error)
                            }
                        }
                    }
                    is CheckListState.Success -> {
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun initViews() {
        loading = LoadingDialog(requireContext())
        // 작성 상태
        if(args?.getInt("flag") == 0) {
            initDateTextView()
            initDialog()
            initButton()
            initSpinner()
            initTextState()
        } else if(args?.getInt("flag") == 1) { // read only
            val today = args?.get("date") as GregorianCalendar
            val d = Date(today.timeInMillis)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            date = sdf.format(d).toString()
            initDialog()
        }

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun initReadOnlyView(response: CheckListResponse) = with(binding) {
        val date = args?.get("date") as GregorianCalendar
        val d = Date(date.timeInMillis)
        val sdf = SimpleDateFormat("yyyy.MM.dd.E")
        val dates = sdf.format(d).toString().split(".")
        yearTextView.text = dates[0] + ". "
        monthTextView.text = dates[1] + ". "
        dayOfMonthTextView.text = dates[2]
        dayofWeekTextView.text = dates[3] +"요일"
        submitButton.toGone()
        eatInputLayout.isEnabled = false
        otherInputLayout.isEnabled = false
        poobStatus.isEnabled = false
        editTextAnimalEat.setText(response.eatQuantity)
        editTextAnimalOther.setText(response.note)
        poobStatus.toGone()
        poobStatusTextView.toVisible()
        poobStatusTextView.text = response.poobStatus
    }

    @SuppressLint("SetTextI18n")
    private fun initDateTextView() = with(binding) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> getString(R.string.sunday)
            2 -> getString(R.string.monday)
            3 -> getString(R.string.tuesday)
            4 -> getString(R.string.wednesday)
            5 -> getString(R.string.thursday)
            6 -> getString(R.string.friday)
            7 -> getString(R.string.saturday)
            else -> getString(R.string.error)
        }
        yearTextView.text = "$year."
        monthTextView.text = "${month + 1}."
        dayOfMonthTextView.text = day.toString()
        dayofWeekTextView.text = dayOfWeek
        date = "$year-${month + 1}-$day $dayOfWeek"
    }

    private fun initButton() = with(binding) {
        submitButton.isEnabled = session != null
        submitButton.setOnClickListener {
            loading.setVisible()
            CoroutineScope(Dispatchers.Main).launch {
                routineStatusList = viewModel.getAllStatus()
                viewModel.deleteAllStatus()
            }
            loading.setInvisible()
            if (checkValidation()) {
                flag = 1
                // routineStatusList가 백그라운드에서 돌아 여기가 먼저 실행되기에 강제로 딜레이시켜서 초기화된 상태를 받아오게함
                Handler(Looper.getMainLooper()).postDelayed({
                    val list = date.split("-").toMutableList()
                    if(list[1].length == 1)
                        list[1] = "0" + list[1]
                    date = list.joinToString("-")
                    checkList = CheckList(date, eatQuantityStr, poobStatusStr, other, routineStatusList)
                    submitCheckList(checkList)
                }, 300)
            }
        }
    }

    private fun submitCheckList(checkList: CheckList) = viewModel.submitCheckList(checkList)

    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
            flag = 0
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            if(flag == 1) {
                submitCheckList(checkList)
            } else {
                observeData()
            }
        }
    }

    private fun initSpinner() = with(binding) {
        poobStatus.toVisible()
        poobStatusTextView.toGone()
        poobStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                poobStatusStr = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                poobStatusStr = "정상"
            }
        }
    }

    // 체크 가능한 루틴 어댑터
    private fun initRecyclerView(routines: List<RoutineResponse>) = with(binding) {
        adapter = RoutineCheckAdapter(
            routines,
            viewModel,
            ResourcesProviderImpl(requireContext()),
            requireContext(),
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    //캘린터뷰 접근 리사이클러뷰 설정
    private fun initRecyclerViewReadOnly(routines: List<RoutineStatusResponse>) = with(binding) {
        val adapter = RoutineCheckReadOnlyAdapter(
            routines,
            requireContext()
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun initTextState() = with(binding) {
        editTextAnimalEat.doOnTextChanged { text, _, _, _ ->
            initValidate(eatInputLayout)
            viewModel.eatLiveData.value = text.toString()
            eatQuantityStr = text.toString()
        }

        editTextAnimalOther.doOnTextChanged { text, _, _, _ ->
            initValidate(otherInputLayout)
            viewModel.otherLiveData.value = text.toString()
            other = text.toString()
        }
    }

    private fun checkValidation(): Boolean {
        var check = true
        with(binding) {
            if (!validateEmpty(eatInputLayout, eatQuantityStr)) {
                shake(editTextAnimalEat, requireContext())
                check = false
            }
            if (!validateEmpty(otherInputLayout, other)) {
                shake(editTextAnimalOther, requireContext())
                check = false
            }

            return check
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        args = arguments

    }

    companion object {
        fun newInstance() = ChecklistFragment().apply {

        }

        const val TAG = "CHECKLIST_FRAGMENT"
        var isChecked = false
    }
}