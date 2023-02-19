package com.k_bootcamp.furry_friends.view.main.routine

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentRoutineBinding
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.util.recyclerview.SwipeToDeleteCallback
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.RoutineAdapter
import com.k_bootcamp.furry_friends.view.main.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RoutineFragment : BaseFragment<RoutineViewModel, FragmentRoutineBinding>() {

    private val session = Application.prefs.session
    private val animalViewModel: HomeViewModel by viewModels()
    override val viewModel: RoutineViewModel by viewModels()
    private lateinit var adapter: RoutineAdapter
    private lateinit var mainActivity: MainActivity

    override fun getViewBinding(): FragmentRoutineBinding =
        FragmentRoutineBinding.inflate(layoutInflater)

    override fun observeData() {
        viewModel.initRoutines()
        viewModel.routineLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is RoutineState.Loading -> {
                    binding.infoTextView.toGone()
                    binding.recyclerView.toVisible()
                    binding.recyclerView.showShimmer()
                }
                is RoutineState.Error -> {
                    binding.recyclerView.hideShimmer()
                    binding.recyclerView.toGone()
                    binding.infoTextView.toVisible()
                    binding.addRoutineButton.isEnabled = false

                    when (it.message) {
                        getString(R.string.not_loged_in) -> {
                            binding.infoTextView.text = getString(R.string.waiting_you)
                        }
                        getString(R.string.not_register_animal) -> {
                            binding.infoTextView.text = getString(R.string.not_register_animals)
                        }
                        getString(R.string.exist_routine) -> {
                            requireContext().toast(it.message+"입니다.")
                            observeData()
                        }
                        else -> {
                            binding.infoTextView.text = getString(R.string.unknown_error)
                        }
                    }
                }
                is RoutineState.Success -> {
                    binding.recyclerView.hideShimmer()
                    binding.infoTextView.toGone()
                    binding.recyclerView.toVisible()
                    binding.addRoutineButton.isEnabled = true
                    initRecyclerView(it.routines)
                }
            }
        }
    }

    override fun initViews() {
        initButton()
    }

    private fun initRecyclerView(routines: List<Routine>) {
        adapter = RoutineAdapter(
            routines.toMutableList(),
            viewModel,
            ResourcesProviderImpl(requireContext()),
            requireContext()
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as RoutineAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.recyclerView)

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
                    if(editText.text.isEmpty()) {
                        requireContext().toast("루틴이름을 입력해주세요!")
                    } else {
                        viewModel.addRoutine(editText.text.toString(), session)
                    }
                }.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    companion object {
        fun newInstance() = RoutineFragment().apply {

        }

        const val TAG = "ROUTINE_FRAGMENT"
    }
}