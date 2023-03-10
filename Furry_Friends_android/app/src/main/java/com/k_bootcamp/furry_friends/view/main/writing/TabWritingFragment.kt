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
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import com.k_bootcamp.furry_friends.model.writing.DiagnosisModel
import com.k_bootcamp.furry_friends.util.etc.LoadingDialog
import com.k_bootcamp.furry_friends.util.recyclerview.SwipeToDeleteCallback
import com.k_bootcamp.furry_friends.util.recyclerview.SwipeToEditCallback
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.ModelRecyclerAdapter
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
                    // ???????????? ?????? ?????? ???????????? ???????????? ???????????? ?????? ?????? ????????? ????????? ?????????
                    mainActivity.showFragment(DailyWritingFragment.newInstance().apply{
                        // flag == 0 -> read only    ????????? ?????? ???????????? ????????????
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
                    // ???????????? ?????? ?????? ???????????? ???????????? ???????????? ?????? ?????? ????????? ????????? ?????????
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

    override fun getViewBinding(): FragmentTabWritingBinding =
        FragmentTabWritingBinding.inflate(layoutInflater)

    override fun observeData() {
        // ?????? ?????? ???????????? ???????????? ???????????? ????????????
        if (pos == 0) {
            Log.e("??????", "??????")
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
                        loading.dismiss()
                        binding.dailyRecyclerView.toGone()
                        binding.infoTextView.toVisible()
                        showErrorMessage(it.message, binding)
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

                    // ?????? ?????? ?????? ??????
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
            viewModel.getDiagnosisList()
            viewModel.tabLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is TabWritingStatus.Loading -> {
                        loading.setVisible()
                        binding.diagnosisRecyclerView.showShimmer()
                    }
                    is TabWritingStatus.Error -> {
                        loading.dismiss()
                        binding.diagnosisRecyclerView.toGone()
                        binding.infoTextView.toVisible()
                        showErrorMessage(it.message, binding)

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
                    // ?????? ?????? ?????? ??????
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
//        changeView(0)  -- ??? ??? ?????? ???????????? ??????
        initFloatingButton(0) // changeview??? ????????? ??? ???????????? ??????????????? ???????????? ???????????? ?????????
        tabSelected()
    }

    private fun initRecyclerView(position: Int) {
        if (position == 0) {
            binding.dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.dailyRecyclerView.adapter = dailyAdapter
            val editSwipeHandler = object : SwipeToEditCallback(requireContext()){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = dailyAdapter
                    // ???????????? flag == 2
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
                // ??? ????????? ?????? ??????
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
                loading.setError()
                binding.infoTextView.text = getString(R.string.unknown_error)
            }
        }
    }
    private fun initDialog() {
        // ?????? ??????
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // ?????? ????????????
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