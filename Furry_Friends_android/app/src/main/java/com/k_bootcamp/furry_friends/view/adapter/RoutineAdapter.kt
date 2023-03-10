package com.k_bootcamp.furry_friends.view.adapter

import android.app.TimePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_bootcamp.furry_friends.model.animal.SendRoutine
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
                // ?????? ?????? ??????
                val updatedRoutine = routine.copy(
                    isOn = isChecked
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.animalRepo.updateRoutine(
                        updatedRoutine.isOn,
                        updatedRoutine.session,
                        updatedRoutine.animal_id,
                        updatedRoutine.routineName
                    ) // ?????? ?????? ??????
                    alarmRoutine =
                        viewModel.animalRepo.getRoutinesFromId(updatedRoutine.animal_id)[adapterPosition]
                    Log.e("alarmroutine", alarmRoutine.toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        when (isChecked) {
                            true -> {
//                                viewModel.cancelAlarm(alarmRoutine.routineId)
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
            // db?????? ????????????
            viewModel.animalRepo.deleteRoutine(routineList[position])
            // ???????????? ?????? ????????? ????????????
            val response = viewModel.animalRepo.deleteRoutineByServer(routineList[position])
            response?.let {
                // ?????? ??????
            }
            CoroutineScope(Dispatchers.Main).launch {
                // ??????????????? ????????????
                routineList.removeAt(position)
                // ????????? ????????????
                viewModel.cancelAlarm(routineList[position].routineId)
                // ??????????????????.
                notifyItemRemoved(position)
            }
        }
    }

    private fun deleteDateRoutine(routine: SendRoutine) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = viewModel.animalRepo.deleteDateRoutine(routine)
            response?.let {
                // ????????? ????????? ??? ???????
            }
        }
    }

    private fun submitDateRoutine(routine: SendRoutine) {
        CoroutineScope(Dispatchers.IO).launch {
//            val response = animalRepository.submitDateRoutine(routine)
            val response = viewModel.animalRepo.submitDateRoutine(routine)
            response?.let {
                // ????????? ????????? ??? ???????
                
            }
        }
    }

    private fun initCheckBox(binding: ViewholderRoutineBinding, routine: Routine, position: Int) {
        // true?????? ?????? ?????? ?????????, false?????? ?????? ?????? ?????????
        binding.chkMon.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
                routine.routineName,
                "0"
            )
            when (isChecked) {
                true -> {
                    // ????????? ?????????
                    submitDateRoutine(sendRoutine)
                    // ?????? db ?????? ??????
                    updateMonday(true, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
                false -> {
                    deleteDateRoutine(sendRoutine)
                    // ?????? ??????
                    updateMonday(false, sendRoutine)
                    binding.routineControl.isOn = false
                    viewModel.cancelAlarm(sendRoutine.routineId)
                }
            }
        }
        binding.chkTue.setOnCheckedChangeListener { _, isChecked ->
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
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
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
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
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
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
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
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
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
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
            val sendRoutine = SendRoutine(
                routine.routineId,
                routine.animal_id,
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

    private fun updateMonday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateMonday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateTuesday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateTuesday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateWednesday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateWednesday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateThursday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateThursday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateFriday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateFriday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateSaturday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateSaturday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    private fun updateSunday(isChecked: Boolean, routine: SendRoutine) =
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.animalRepo.updateSunday(
                isChecked,
                routine.animalId,
                routine.routineName
            )
        }

    // ?????? ?????? ??????
    private fun getTime(binding: ViewholderRoutineBinding, routine: Routine, position: Int) {
        val curCalendar = Calendar.getInstance()
        val timePicker =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.animalRepo.updateTime(
                        "$hour:$minute",
                        routine.animal_id,
                        routine.routineName
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        if (minute < 10) binding.timeSelect.text = "$hour:0$minute"
                        else binding.timeSelect.text = "$hour:$minute"
                        alarmRoutine =
                            viewModel.animalRepo.getRoutinesFromId(routine.animal_id)[position]
                        // ????????? ????????? ??? ????????? ?????? ????????? ?????? ???????????? ?????? ?????? ??????
                        if (alarmRoutine.isOn) {
//                            viewModel.cancelAlarm(alarmRoutine.routineId)
                            viewModel.setAlarm(
                                alarmRoutine,
                                if (minute < 10) "$hour:0$minute"
                                else "$hour:$minute"
                            )
                            Log.e("timeSelected isOn1", alarmRoutine.toString())
                        } else {
                            val handler = Handler(Looper.getMainLooper());
                            handler.postDelayed({
                                context.toast("??????????????? ??????????????? ????????? ???????????? ?????????.")
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