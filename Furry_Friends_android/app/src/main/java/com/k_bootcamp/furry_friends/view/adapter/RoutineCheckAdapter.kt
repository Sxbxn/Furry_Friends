package com.k_bootcamp.furry_friends.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.databinding.ViewholderRoutineBinding
import com.k_bootcamp.furry_friends.databinding.ViewholderRoutineCheckBinding
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.model.animal.RoutineStatus
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistFragment
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistViewModel
import com.k_bootcamp.furry_friends.view.main.routine.RoutineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RoutineCheckAdapter(
    private val routineList: List<RoutineResponse>,
    private val viewModel: ChecklistViewModel,
    val resourcesProvider: ResourcesProviderImpl,
    val context: Context
) : RecyclerView.Adapter<RoutineCheckAdapter.RoutineCheckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineCheckViewHolder =
        RoutineCheckViewHolder(
            ViewholderRoutineCheckBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RoutineCheckViewHolder, position: Int) {
        holder.bind(routineList[position])
    }

    override fun getItemCount(): Int = routineList.size

    inner class RoutineCheckViewHolder(private val binding: ViewholderRoutineCheckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(routine: RoutineResponse) {
            binding.routineName.text = routine.routineName
            initCheckBox(binding, routine, adapterPosition)
        }
    }

    private fun initCheckBox(
        binding: ViewholderRoutineCheckBinding,
        routine: RoutineResponse,
        position: Int
    ) {
        binding.chkRoutine.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    // 내부 루틴db의 체크 상태를 업데이트
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.rDao.insertRoutineStatus(
                            RoutineStatus(
                                0,
                                routine.routineId,
                                routine.routineName,
                                getDate(),
                                isChecked
                            )
                        )
                    }
                }
                false -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.rDao.deleteRoutineStatus(
                                routine.routineId,
                                routine.routineName,
                                getDate(),
                            )
                    }
                }
            }
        }
    }


    private fun getDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> context.getString(R.string.sunday)
            2 -> context.getString(R.string.monday)
            3 -> context.getString(R.string.tuesday)
            4 -> context.getString(R.string.wednesday)
            5 -> context.getString(R.string.thursday)
            6 -> context.getString(R.string.friday)
            7 -> context.getString(R.string.saturday)
            else -> context.getString(R.string.error)
        }

        return "$year ${month + 1} $day $dayOfWeek"
    }
}