package com.k_bootcamp.furry_friends.view.main.routine

import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.databinding.FragmentHomeBinding
import com.k_bootcamp.furry_friends.databinding.FragmentRoutineBinding
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.adapter.RoutineAdapter
import com.k_bootcamp.furry_friends.view.main.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoutineFragment : BaseFragment<RoutineViewModel, FragmentRoutineBinding>() {

    private val session = Application.prefs.session
    private val animalViewModel: HomeViewModel by viewModels()
    override val viewModel: RoutineViewModel by viewModels()
    private lateinit var adapter: RoutineAdapter

    override fun getViewBinding(): FragmentRoutineBinding =
        FragmentRoutineBinding.inflate(layoutInflater)

    override fun observeData() {
        viewModel.initRoutines()
        viewModel.routineLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is RoutineState.Loading -> {
                    Log.e("loading", "loading")
                    binding.recyclerView.toVisible()
                    binding.recyclerView.showShimmer()
                    binding.infoTextView.toGone()
                }
                is RoutineState.Error -> {
                    binding.recyclerView.hideShimmer()
                    binding.recyclerView.toGone()
                    binding.infoTextView.toVisible()
                    when(it.message) {
                        getString(R.string.not_loged_in) -> {
                            binding.infoTextView.text = "로그인 되지 않았어요!"
                        }
                        getString(R.string.not_register_animal) -> {
                            binding.infoTextView.text = "반려동물 등록이 되지 않았어요!"
                        }
                        else -> {
                            binding.infoTextView.text = "알 수 없는 오류가 발생했어요"
                        }
                    }
                    Log.e("message", it.message)
                }
                is RoutineState.Success -> {
                    Log.e("success", "success")
                    binding.recyclerView.hideShimmer()
                    binding.infoTextView.toGone()
                    binding.recyclerView.toVisible()
                    initRecyclerView(it.routines)
                }
            }
        }
    }

    override fun initViews() {
        initButton()
    }
    private fun initRecyclerView(routines: List<Routine>) {
        adapter = RoutineAdapter(routines, viewModel, ResourcesProviderImpl(requireContext()))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
    private fun initButton() = with(binding) {
        addRoutineButton.setOnClickListener {
            val editText = EditText(requireContext())
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.add_routine))
                .setView(editText)
                .setIcon(R.drawable.ic_routine)
                .setPositiveButton(getString(R.string.submit)) { _, _ ->
                    viewModel.addRoutine(editText.text.toString(), session)
                }.show()
        }
    }
    companion object {
        fun newInstance() = RoutineFragment().apply {

        }

        const val TAG = "ROUTINE_FRAGMENT"
    }
}