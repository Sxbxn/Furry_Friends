package com.k_bootcamp.furry_friends.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.databinding.ViewholderRoutineBinding
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.main.routine.RoutineViewModel
import kotlinx.coroutines.*


class RoutineAdapter(
    private val routineList: List<Routine>,
    private val viewModel: RoutineViewModel,
    val resourcesProvider: ResourcesProviderImpl
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder =
        RoutineViewHolder(
            ViewholderRoutineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(routineList[position])
    }

    override fun getItemCount(): Int = routineList.size

    inner class RoutineViewHolder(private val binding: ViewholderRoutineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(routine: Routine) {
            binding.routineName.text = routine.routineName
            binding.routineControl.isOn = routine.isOn
            binding.chkMon.isChecked = routine.mon
            binding.chkTue.isChecked = routine.tue
            binding.chkWed.isChecked = routine.wed
            binding.chkThu.isChecked = routine.thu
            binding.chkFri.isChecked = routine.fri
            binding.chkSat.isChecked = routine.sat
            binding.chkSun.isChecked = routine.sun
            Log.e("text", routine.routineName)
            binding.routineControl.setOnToggledListener { toggleableView, isChecked ->
                // 토글 상태 저장
                val updatedRoutine = routine.copy(
                    isOn = isChecked
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.rDao.updateRoutine(
                        updatedRoutine.isOn,
                        updatedRoutine.session,
                        updatedRoutine.animalId,
                        updatedRoutine.routineName
                    )
                }
            }
            initCheckBox(binding, routine)
        }

    }

    private fun deleteDateRoutine(routine: RoutineResponse) {
        CoroutineScope(Dispatchers.IO).launch {
//            val response = animalRepository.deleteDateRoutine(routine)
            val response = viewModel.animalRepo.deleteDateRoutine(routine)
            response?.let {
                // 보내고 받으면 뭐 할지?
            }
        }
    }

    private fun submitDateRoutine(routine: RoutineResponse) {
        CoroutineScope(Dispatchers.IO).launch {
//            val response = animalRepository.submitDateRoutine(routine)
            val response = viewModel.animalRepo.submitDateRoutine(routine)
            response?.let {
                // 보내고 받으면 뭐 할지?
            }
        }
    }

    private fun initCheckBox(binding: ViewholderRoutineBinding, routine: Routine) {
        // true이면 저장 정보 보내기, false이면 삭제 정보 보내기
        binding.chkMon.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "0"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    Log.e("mon", "true")
                    // 상태 저장
                    updateMonday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    Log.e("mon", "false")
                    // 상태 저장
                    updateMonday(false, sendRoutine)
                }
            }
        }
        binding.chkTue.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "1"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    updateTuesday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateTuesday(false, sendRoutine)
                }
            }
        }
        binding.chkWed.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "2"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    updateWednesday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateWednesday(false, sendRoutine)
                }
            }
        }
        binding.chkThu.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "3"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    updateThursday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateThursday(false, sendRoutine)
                }
            }
        }
        binding.chkFri.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "4"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    updateFriday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateFriday(false, sendRoutine)
                }
            }
        }
        binding.chkSat.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "5"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    updateSaturday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateSaturday(false, sendRoutine)
                }
            }
        }
        binding.chkSun.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = RoutineResponse(
                routine.routineId,
                routine.animalId,
                routine.routineName,
                "6"
            )
            when (isChecked) {
                true -> {
                    submitDateRoutine(sendRoutine)
                    updateSunday(true, sendRoutine)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateSaturday(false, sendRoutine)
                }
            }
        }
    }

    private fun updateMonday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateMonday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateTuesday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateTuesday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateWednesday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateWednesday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateThursday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateThursday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateFriday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateFriday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateSaturday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateSaturday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateSunday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.rDao.updateSunday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }
}