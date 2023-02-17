package com.k_bootcamp.furry_friends.view.main.writing

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.baeminclone.screen.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentTabWritingBinding
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import com.k_bootcamp.furry_friends.model.writing.DiagnosisModel
import com.k_bootcamp.furry_friends.util.etc.LoadingDialog
import com.k_bootcamp.furry_friends.util.recyclerview.SwipeToDeleteCallback
import com.k_bootcamp.furry_friends.util.recyclerview.SwipeToEditCallback
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.ModelRecyclerAdapter
import com.k_bootcamp.furry_friends.view.adapter.RoutineAdapter
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.DailyListListener
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.DiagnosisListListener
import com.k_bootcamp.furry_friends.view.main.writing.daily.DailyWritingFragment
import com.k_bootcamp.furry_friends.view.main.writing.diagnosis.DiagnosisWritingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabWritingFragment : BaseFragment<TabWritingViewModel, FragmentTabWritingBinding>() {
    override val viewModel: TabWritingViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private var pos: Int? = 0
    private lateinit var loading: LoadingDialog
    private val session = Application.prefs.session
    private val animalId = Application.prefs.animalId
    private val dailyAdapter by lazy {
        ModelRecyclerAdapter<DailyModel, TabWritingViewModel>(
            mutableListOf(),
            viewModel,
            adapterListener = object : DailyListListener {
                override fun onClickItem(model: DailyModel) {
                    // 클릭하면 일상 기록 화면으로 넘어가고 서버에서 해당 글의 정보를 가져와 바인딩
                    mainActivity.showFragment(DailyWritingFragment.newInstance().apply{
                        // flag == 0 -> read only    모델을 함께 넘겨주어 보여주기
                        arguments = bundleOf(
                            Pair("flag", 0),
                            Pair("url", model.imageUrl),
                            Pair("title", model.title),
                            Pair("date", model.currdate),
                            Pair("content", model.content)
                        )
                    }, "")
                }
            },
            requireContext()
        )
    }
    private val diagnosisAdapter by lazy {
        ModelRecyclerAdapter<DiagnosisModel, TabWritingViewModel>(
            mutableListOf(),
            viewModel,
            adapterListener = object : DiagnosisListListener {
                override fun onClickItem(model: DiagnosisModel) {
                    // 클릭하면 일상 기록 화면으로 넘어가고 서버에서 해당 글의 정보를 가져와 바인딩
                    mainActivity.showFragment(DiagnosisWritingFragment.newInstance().apply {
                        // flag == 0 -> read only
                        arguments = bundleOf(
                            Pair("flag", 0),
                            Pair("url", model.imageUrl),
                            Pair("comment", model.comment),
                            Pair("date", model.currdate),
                            Pair("content", model.content),
                            Pair("kind", model.kind),
                            Pair("affectedArea", model.affectedArea)
                        )
                    }, "")
                }
            },
            requireContext()
        )
    }
    //// 임시 데이터
    val list = mutableListOf<Model>(
        DailyModel(1, CellType.DAILY_CELL, "제목1", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-11"),
        DailyModel(2, CellType.DAILY_CELL, "제목2", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-12"),
        DailyModel(3, CellType.DAILY_CELL, "제목3", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-13"),
        DailyModel(4, CellType.DAILY_CELL, "제목4", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-14"),
        DailyModel(5, CellType.DAILY_CELL, "제목5", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-15"),
        DailyModel(6, CellType.DAILY_CELL, "제목6", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-16"),
        DailyModel(7, CellType.DAILY_CELL, "제목7", "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠", "2023-2-17"),
    )
    val list2 = mutableListOf<Model>(
        DiagnosisModel(1, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠1", "2023-2-11","코멘트1","강아지","안구"),
        DiagnosisModel(2, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠2", "2023-2-12","","강아지","안구"),
        DiagnosisModel(3, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠3", "2023-2-13","코멘트13","강아지","안구"),
        DiagnosisModel(4, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠4", "2023-2-14","코멘트14","강아지","안구"),
        DiagnosisModel(5, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠5", "2023-2-15","","강아지","안구"),
        DiagnosisModel(6, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠6", "2023-2-16","코멘트16","강아지","안구"),
        DiagnosisModel(7, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠7", "2023-2-17","","강아지","안구"),
        DiagnosisModel(8, CellType.DIAGNOSIS_CELL, "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c","컨텐츠8", "2023-2-18","코멘트18","강아지","안구"),

    )

    override fun getViewBinding(): FragmentTabWritingBinding =
        FragmentTabWritingBinding.inflate(layoutInflater)

    override fun observeData() {
        Log.e("pos",pos.toString())
        // 현재 탭에 해당되는 데이터를 가져와서 보여주기
        if (pos == 0) {
            Log.e("일상", "일상")
            viewModel.getDailyList()
            viewModel.tabLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is TabWritingStatus.Loading -> {
                        loading.setVisible()
                        binding.infoTextView.toGone()
                        binding.dailyRecyclerView.toVisible()
                        binding.dailyRecyclerView.showShimmer()
                    }
                    is TabWritingStatus.Error -> {
                        loading.setError()
                        binding.dailyRecyclerView.toGone()
                        binding.infoTextView.toVisible()
                        showErrorMessage(it.message, binding)
                        // 테스트 코드-----임시 데이터 *************************************** success에 들어갈것
//                        binding.diagnosisRecyclerView.toGone()
//                        binding.infoTextView.toGone()
//                        binding.dailyRecyclerView.toVisible()
//                        binding.dailyRecyclerView.hideShimmer()
//                        initRecyclerView(0)
//                        dailyAdapter.submitList(list)
                        // ********************************************
                    }
                    is TabWritingStatus.SuccessDiagnosis -> {
                        loading.dismiss()
                        loading.setError()
                        binding.dailyRecyclerView.hideShimmer()
                    }
                    is TabWritingStatus.SuccessDaily -> {
                        loading.dismiss()
                        binding.diagnosisRecyclerView.toGone()
                        binding.infoTextView.toGone()
                        binding.dailyRecyclerView.toVisible()
                        binding.dailyRecyclerView.hideShimmer()
                        initRecyclerView(0)
                        dailyAdapter.submitList(it.response.map { res -> res.toModel() }.toMutableList())
                    }

                    // 삭제 수정 등록 성공
                    is TabWritingStatus.Done -> {
                        when(it.flag) {
                            // daily
                            0 -> {
                                changeView(0)
                            }
                            // diagnosis
                            1 -> {
                                changeView(1)
                            }
                        }
                    }
                }
            }
        } else if (pos == 1) {
            Log.e("진단", "진단")
            viewModel.getDiagnosisList()
            viewModel.tabLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is TabWritingStatus.Loading -> {
                        loading.setVisible()
                        binding.diagnosisRecyclerView.showShimmer()
                    }
                    is TabWritingStatus.Error -> {
                        loading.setError()
                        binding.diagnosisRecyclerView.toGone()
                        binding.infoTextView.toVisible()
                        showErrorMessage(it.message, binding)
                        // 테스트 코드-----임시 데이터 ***************************************
//                        binding.dailyRecyclerView.toGone()
//                        binding.diagnosisRecyclerView.toVisible()
//                        binding.infoTextView.toGone()
//                        binding.diagnosisRecyclerView.hideShimmer()
//                        initRecyclerView(1)
//                        diagnosisAdapter.submitList(list2)
                        // ********************************************
                    }
                    is TabWritingStatus.SuccessDaily -> {
                        loading.dismiss()
                        loading.setError()
                        binding.diagnosisRecyclerView.hideShimmer()
                    }
                    is TabWritingStatus.SuccessDiagnosis -> {
                        loading.dismiss()
                        binding.dailyRecyclerView.toGone()
                        binding.diagnosisRecyclerView.toVisible()
                        binding.infoTextView.toGone()
                        binding.diagnosisRecyclerView.hideShimmer()
                        initRecyclerView(1)
                        diagnosisAdapter.submitList(it.response.map { res -> res.toModel() }.toMutableList())
                    }
                    // 삭제 수정 등록 성공
                    is TabWritingStatus.Done -> {
                        when(it.flag) {
                            // daily
                            0 -> {
                                changeView(0)
                            }
                            // diagnosis
                            1 -> {
                                changeView(1)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initViews() {
        loading = LoadingDialog(requireContext())
        initDialog()
//        changeView(0)  -- 두 번 통신 호출되어 제거
        initFloatingButton(0) // changeview를 없애어 첫 화면에서 플로팅버튼 초기화가 안되므로 초기화
        tabSelected()
    }

    private fun initRecyclerView(position: Int) {
        if (position == 0) {
            binding.dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.dailyRecyclerView.adapter = dailyAdapter
            val editSwipeHandler = object : SwipeToEditCallback(requireContext()){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = dailyAdapter
                    // 업데이트 flag == 2
                    adapter.notifyEditItem(mainActivity, viewHolder.adapterPosition)
                }
            }

            val editItemTouchHelper= ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(binding.dailyRecyclerView)

            val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val dailyAdapter = dailyAdapter
                    dailyAdapter.removeAt(viewHolder.adapterPosition)
                }
            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.dailyRecyclerView)
        } else if(position == 1) {
            binding.diagnosisRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.diagnosisRecyclerView.adapter = diagnosisAdapter
            val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val diagnosisAdapter = diagnosisAdapter
                    diagnosisAdapter.removeAt(viewHolder.adapterPosition)
                }
            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.diagnosisRecyclerView)
        }
    }

    private fun tabSelected() = with(binding) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                pos = tab?.position
                changeView(pos!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun changeView(position: Int) {
        when (position) {
            0 -> {
                initFloatingButton(position)
                // 첫 진입시 강제 갱신
                observeData()
            }
            1 -> {
                initFloatingButton(position)
                observeData()
            }
        }
    }

    private fun initFloatingButton(position: Int) = with(binding) {
        addRoutineButton.isEnabled = session != null
        when (position) {
            0 -> {
                addRoutineButton.setOnClickListener {
                    mainActivity.showFragment(
                        DailyWritingFragment.newInstance().apply { arguments = bundleOf(Pair("flag", 1))},
                        DailyWritingFragment.TAG
                    // write mode
                    )
                }
            }
            1 -> {
                addRoutineButton.setOnClickListener {
                    mainActivity.showFragment(
                        DiagnosisWritingFragment.newInstance().apply { arguments = bundleOf(Pair("flag", 1))},
                        DiagnosisWritingFragment.TAG
                    )
                }
            }
        }
    }

    private fun showErrorMessage(msg: String, binding: FragmentTabWritingBinding) {
        when (msg) {
            getString(R.string.not_loged_in) -> {
                binding.infoTextView.text = getString(R.string.waiting_you)
            }
            getString(R.string.not_register_animal) -> {
                binding.infoTextView.text = getString(R.string.notice)
            }
            else -> {
                binding.infoTextView.text = getString(R.string.unknown_error)
            }
        }
    }
    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            loading.setInvisible()
            observeData()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }



    companion object {
        fun newInstance() = TabWritingFragment().apply {

        }

        const val DAILY_FLAG = 0
        const val DIAGNOSIS_FLAG = 1
        const val TAG = "TAB_WRITING_FRAGMENT"
    }
}