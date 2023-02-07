package com.k_bootcamp.furry_friends.view.adapter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_bootcamp.furry_friends.data.response.animal.RoutineResponse
import com.k_bootcamp.furry_friends.databinding.ViewholderRoutineBinding
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl
import com.k_bootcamp.furry_friends.view.main.routine.RoutineViewModel
import kotlinx.coroutines.*
import java.util.*


class RoutineAdapter(
    private val routineList: MutableList<Routine>,
    private val viewModel: RoutineViewModel,
    val resourcesProvider: ResourcesProviderImpl,
    val context: Context
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {
    lateinit var alarmRoutine: Routine
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
            binding.timeSelect.text = routine.time
            binding.timeSelect.setOnClickListener {
                getTime(binding, routine, adapterPosition)
            }
            binding.routineControl.setOnToggledListener { _, isChecked ->
                // 토글 상태 저장
                val updatedRoutine = routine.copy(
                    isOn = isChecked
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.animalRepo.updateRoutine(
                        updatedRoutine.isOn,
                        updatedRoutine.session,
                        updatedRoutine.animalId,
                        updatedRoutine.routineName
                    ) // 토글 상태 저장
                    alarmRoutine =
                        viewModel.animalRepo.getRoutinesFromId(updatedRoutine.animalId)[adapterPosition]
                    Log.e("alarmroutine", alarmRoutine.toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        when (isChecked) {
                            true -> {
                                viewModel.setAlarm(alarmRoutine, alarmRoutine.time)
                            }
                            false -> {
                                viewModel.cancelAlarm(alarmRoutine.routineId)
                            }
                        }
                    }
                }
            }
            initCheckBox(binding, routine, adapterPosition)
        }
    }

    fun removeAt(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // db에서 삭제하고
            viewModel.animalRepo.deleteRoutine(routineList[position])
            // 서버에서 모든 루틴을 삭제하고
            val response = viewModel.animalRepo.deleteRoutineByServer(routineList[position])
            response?.let {
                // 반환 확인
            }
            CoroutineScope(Dispatchers.Main).launch {
                // 로컬에서도 삭제하고
                routineList.removeAt(position)
                // 새로고침한다.
                notifyItemRemoved(position)
            }
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

    private fun initCheckBox(binding: ViewholderRoutineBinding, routine: Routine, position: Int) {
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
                    // 서버로 보내기
                    submitDateRoutine(sendRoutine)
                    // 로컬 db 상태 저장
                    updateMonday(true, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    // 상태 저장
                    updateMonday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
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
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateTuesday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
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
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateWednesday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
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
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateThursday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
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
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateFriday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
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
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateSaturday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
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
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    updateSunday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
            }
        }
    }

    private fun updateMonday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateMonday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateTuesday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateTuesday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateWednesday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateWednesday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateThursday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateThursday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateFriday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateFriday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateSaturday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateSaturday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateSunday(isChecked: Boolean, routine: RoutineResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateSunday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    // 알람 시간 설정
    private fun getTime(binding: ViewholderRoutineBinding, routine: Routine, position: Int) {
        val curCalendar = Calendar.getInstance()
        val timePicker =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.animalRepo.updateTime(
                        "$hour:$minute",
                        routine.animalId,
                        routine.routineName
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        if (minute < 10) binding.timeSelect.text = "$hour:0$minute"
                        else binding.timeSelect.text = "$hour:$minute"
                        alarmRoutine =
                            viewModel.animalRepo.getRoutinesFromId(routine.animalId)[position]
                        // 시간을 바꿨을 때 토글이 켜져 있다면 바꾼 시간으로 다시 알람 세팅
                        if (alarmRoutine.isOn) {
                            viewModel.setAlarm(
                                alarmRoutine,
                                if (minute < 10) "$hour:0$minute"
                                else "$hour:$minute"
                            )
                            Log.e("timeSelected isOn1", alarmRoutine.toString())
                        } else {
                            val handler = Handler(Looper.getMainLooper());
                            handler.postDelayed({
                                context.toast("토글버튼이 꺼져있어요 알람이 등록되지 않아요.")
                            }, 0)
                            Log.e("timeSelected isOn2", alarmRoutine.toString())
                        }
                    }

                }
            }
        val timePickerDialog = TimePickerDialog(
            context,
            timePicker,
            curCalendar.get(Calendar.HOUR_OF_DAY),
            curCalendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }
}