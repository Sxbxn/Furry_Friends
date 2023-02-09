package com.k_bootcamp.furry_friends.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_bootcamp.furry_friends.databinding.ViewholderRoutineCheckBinding
import com.k_bootcamp.furry_friends.model.animal.RoutineStatus
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistViewModel


class RoutineCheckReadOnlyAdapter(
    private val routineList: List<RoutineStatus>,
    val context: Context
) : RecyclerView.Adapter<RoutineCheckReadOnlyAdapter.RoutineCheckViewHolder>() {

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

        fun bind(routine: RoutineStatus) {
            binding.routineName.text = routine.routineName
            binding.chkRoutine.isChecked = routine.status
        }
    }
}