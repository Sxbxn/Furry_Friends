package com.k_bootcamp.furry_friends.view.main.checklist

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.databinding.FragmentDayDetailBinding
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.CheckList
import com.k_bootcamp.furry_friends.model.animal.ChecklistRoutine
import com.k_bootcamp.furry_friends.model.animal.RoutineStatus
import com.k_bootcamp.furry_friends.util.etc.LoadingDialog
import com.k_bootcamp.furry_friends.util.etc.initValidate
import com.k_bootcamp.furry_friends.util.etc.shake
import com.k_bootcamp.furry_friends.util.etc.validateEmpty
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.RoutineCheckAdapter
import com.k_bootcamp.furry_friends.view.main.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ChecklistFragment : BaseFragment<ChecklistViewModel, FragmentDayDetailBinding>() {
    override val viewModel: ChecklistViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private lateinit var loading: LoadingDialog
    private var session = Application.prefs.session
    private lateinit var poobStatusStr: String
    private lateinit var checkList: CheckList
    private lateinit var checkListRoutine: ChecklistRoutine
    private lateinit var adapter: RoutineCheckAdapter
    private var eatQuantityStr: String = ""
    private var other: String = ""
    private var date: String = ""
    private lateinit var routineStatusList: List<RoutineStatus>
    private lateinit var mainActivity: MainActivity


    override fun getViewBinding(): FragmentDayDetailBinding =
        FragmentDayDetailBinding.inflate(layoutInflater)

    override fun observeData() {
        viewModel.initRoutines()
        viewModel.routineLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is CheckListState.Done -> {
                    context?.toast("등록이 완료되었습니다. 메인화면으로 돌아갑니다.")
                    mainActivity.showFragment(HomeFragment.newInstance(),"")
                }
                is CheckListState.Loading -> {
                    Log.e("loading", "loading")
                    loading.setVisible()
                }
                is CheckListState.Error -> {
                    loading.setError()
                    when (it.message) {
                        getString(R.string.not_loged_in) -> {
                            binding.infoTextView.text = "로그인 되지 않았어요!"
                        }
                        getString(R.string.not_register_animal) -> {
                            binding.infoTextView.text = "반려동물 등록이 되지 않았어요!"
                        }
                        getString(R.string.exist_routine) -> {
                            requireContext().toast(it.message)
                        }
                        else -> {
                            binding.infoTextView.text = "알 수 없는 오류가 발생했어요"
                        }
                    }
                    Log.e("message", it.message)
                }
                is CheckListState.Success -> {
                    loading.dismiss()
                    initRecyclerView(it.routines)
                }
            }
        }
    }

    override fun initViews() {
        loading = LoadingDialog(requireContext())
        initDateTextView()
        initDialog()
        initButton()
        initSpinner()
        initTextState()
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
        binding.yearTextView.text = "$year."
        binding.monthTextView.text = "${month + 1}."
        binding.dayOfMonthTextView.text = day.toString()
        binding.dayofWeekTextView.text = dayOfWeek
        date = "$year.${month + 1}$day $dayOfWeek"
    }

    private fun initButton() = with(binding) {
        change.isEnabled = session != null
        change.setOnClickListener {
            loading.setVisible()
            CoroutineScope(Dispatchers.IO).launch {
                routineStatusList = viewModel.animalRepo.getAllStatus()
                viewModel.animalRepo.deleteAllStatus()
            }
            loading.setInvisible()
            if (checkValidation()) {
                checkList = CheckList(date, eatQuantityStr, poobStatusStr, other, routineStatusList)
//                checkListRoutine = ChecklistRoutine(routineStatusList)
                submitCheckList(checkList)
            }
        }
    }

    private fun submitCheckList(checkList: CheckList) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.submitCheckList(checkList)
        }

    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            observeData()
        }
    }

    private fun initSpinner() = with(binding) {
        val spinner = poobStatus
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    private fun initRecyclerView(routines: List<RoutineResponse>) {
        adapter = RoutineCheckAdapter(
            routines,
            viewModel,
            ResourcesProviderImpl(requireContext()),
            requireContext()
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

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
    }

    companion object {
        fun newInstance() = ChecklistFragment().apply {

        }

        const val TAG = "CHECKLIST_FRAGMENT"
        var isChecked = false
    }
}