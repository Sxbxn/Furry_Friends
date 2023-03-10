package com.k_bootcamp.furry_friends.view.main.routine

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.data.repository.animal.AnimalRepository
import com.k_bootcamp.furry_friends.model.animal.Routine
import com.k_bootcamp.furry_friends.notification.AlarmReceiver
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val routineDao: RoutineDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val session = Application.prefs.session
    private var animalId = Application.prefs.animalId
    private val _routineLiveData = MutableLiveData<RoutineState>()
    val routineLiveData: LiveData<RoutineState>
        get() = _routineLiveData
    private var defaultRoutines = listOf("양치", "빗질")
    val animalRepo = animalRepository

    // notification setting
    private lateinit var alarmManager: AlarmManager
    private lateinit var mCalendar: GregorianCalendar
    private lateinit var notificationManager: NotificationManager
    lateinit var builder: NotificationCompat.Builder


    fun initRoutines() {
        _routineLiveData.value = RoutineState.Loading
        if (session == null) {
            _routineLiveData.value = RoutineState.Error(context.getString(R.string.not_loged_in))
        } else {
            // 기본적으로 양치, 빗질, 산책 루틴을 로컬 db에 삽입하여 유지함  - 세션과 동물이 있을때만
            viewModelScope.launch(ioDispatcher) {
                if (animalId < 0) {
                    // 등록이 안되어 있음 -> 안되어있으므로 루틴에 아무것도 없어야함
                    _routineLiveData.postValue(
                        RoutineState.Error(context.getString(R.string.not_register_animal))
                    )
                } else {
                    _routineLiveData.postValue(RoutineState.Loading)
                    // 기본 루틴이 있는지 확인
                    val existRoutines = animalRepository.getRoutinesFromId(animalId)
                    // 해당 동물의 기본 루틴이 설정 되어있는 경우 기본을 없앰
                    if (existRoutines.isNotEmpty()) {
                        defaultRoutines = listOf()
                    }
                    // 기본 루틴을 삽입함
                    defaultRoutines.forEach {
                        try {
                            animalRepository.insertRoutine(
                                Routine(
                                    animal_id = animalId,
                                    session = session,
                                    routineName = it,
                                    isOn = false
                                )
                            )
                        } catch (e: Exception) {
                            _routineLiveData.postValue(RoutineState.Error(context.getString(R.string.error_response)))
                        }
                    }
                    val routines = animalRepository.getRoutinesFromId(animalId)
                    _routineLiveData.postValue(
                        RoutineState.Success(
                            animalId,
                            session,
                            routines
                        )
                    )
                    // 할 필요가 있나?  동기화해야하나?? --> 로그아웃만 잘해주면됨
//                    if (existRoutines.isNotEmpty()) {
//                        getRoutinesFromId()
//                    }
                }
            }
        }
    }

    // 동기화?
    private fun getRoutinesFromId() {
        _routineLiveData.postValue(RoutineState.Loading)
        viewModelScope.launch(ioDispatcher) {
            // 전부 가져와서 id로 필터링한 다음에 동기화
            val routines =
                animalRepository.getAllRoutinesByAnimalId()?.filter { it.animalId == animalId }
            routines?.forEach {
                when (it.weekDay) {
                    "월요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            mon = true
                        )
                        routineDao.updateMonday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }

                    "화요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            tue = true
                        )
                        routineDao.updateTuesday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }
                    "수요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            wed = true
                        )
                        routineDao.updateWednesday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }
                    "목요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            thu = true
                        )
                        routineDao.updateThursday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }
                    "금요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            fri = true
                        )
                        routineDao.updateFriday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }
                    "토요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            sat = true
                        )
                        routineDao.updateSaturday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }
                    "일요일" -> {
                        val routine = Routine(
                            animal_id = it.animalId,
                            routineName = it.routineName,
                            session = session!!,
                            isOn = false,
                            sun = true
                        )
                        routineDao.updateSunday(true, animalId, it.routineName)
                        animalRepository.insertRoutine(routine)
                    }
                }
            }
            val updatedRoutines = animalRepository.getRoutinesFromId(animalId)
            _routineLiveData.postValue(RoutineState.Success(animalId, session!!, updatedRoutines))
        }
    }

    fun addRoutine(routineName: String, session: String?) {
        if (session == null) {
            _routineLiveData.value = RoutineState.Error(context.getString(R.string.not_loged_in))
        } else {
            _routineLiveData.postValue(RoutineState.Loading)
            viewModelScope.launch(ioDispatcher) {
                var flag = true
                // 추가 할 때 루틴이 존재하는 지 확인하고 없으면 넣음  to-do
                val routines = routineDao.getRoutineFromId(animalId)
                for (i in routines.indices) {
                    if (routines[i].routineName == routineName) {
                        flag = false
                        break
                    }
                }
                if (flag) {
                    animalRepository.insertRoutine(
                        Routine(
                            animal_id = animalId,
                            session = session,
                            routineName = routineName,
                            isOn = false
                        )
                    )
                    val updatedRoutines = animalRepository.getRoutinesFromId(animalId)
                    _routineLiveData.postValue(
                        RoutineState.Success(
                            animalId,
                            session,
                            updatedRoutines
                        )
                    )
                } else {
                    _routineLiveData.postValue(RoutineState.Error(context.getString(R.string.exist_routine)))
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setAlarm(routine: Routine, time: String?) {
        initManager()
        cancelAlarm(routine.routineId)
        val mon = routine.mon
        val tue = routine.tue
        val wed = routine.wed
        val thu = routine.thu
        val fri = routine.fri
        val sat = routine.sat
        val sun = routine.sun
        val weekStatus: BooleanArray = booleanArrayOf(false, sun, mon, tue, wed, thu, fri, sat)

        //AlarmReceiver에 값 전달
        val receiverIntent = Intent(context, AlarmReceiver::class.java)


        // 번들로 합치고 인텐트에 넣어서 보내주어야 소실이 안됨.... 이거 때문에 3시간넘게 날려먹었네...
        val bundle = Bundle()
        bundle.putParcelable("registerRoutine", routine)
        bundle.putBooleanArray("dayOfWeek", weekStatus)
        receiverIntent.putExtra("bundle", bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            routine.routineId,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 특정 시간
        val cal = Calendar.getInstance()
        if (time != "00:00" && time != null) {
            val hourMinute = time.split(":")
            cal.set(Calendar.HOUR_OF_DAY, hourMinute[0].toInt())
            cal.set(Calendar.MINUTE, hourMinute[1].toInt())
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 12)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
        }
        val intervalDay: Long = 24 * 60 * 60 * 1000
        var selectTime: Long = cal.timeInMillis
        val currentTime: Long = System.currentTimeMillis()

        //만약 설정한 시간이, 현재 시간보다 작다면 알람이 부정확하게 울리기 때문에 다음주 울리게 설정
        if (currentTime > selectTime) {
            selectTime += intervalDay
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectTime, intervalDay, pendingIntent)
        Log.e("selectTime", selectTime.toString())
    }

    fun cancelAlarm(routineId: Int) {
        initManager()
        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, routineId, receiverIntent, 0)
        alarmManager.cancel(pendingIntent)
    }

    private fun initManager() {
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mCalendar = GregorianCalendar()
    }
}