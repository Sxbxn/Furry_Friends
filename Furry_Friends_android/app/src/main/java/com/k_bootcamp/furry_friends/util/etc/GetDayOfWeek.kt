package com.k_bootcamp.furry_friends.util.etc

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

fun getDayOfWeek(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        1 -> "일요일"
        2 -> "월요일"
        3 -> "화요일"
        4 -> "수요일"
        5 -> "목요일"
        6 -> "금요일"
        7 -> "토요일"
        else -> "에러"
    }
}


@SuppressLint("SimpleDateFormat")
fun getDayOfWeekFromDate(mDate:String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val date = sdf.parse(mDate)
    val cal = Calendar.getInstance()
    cal.time = date!!
    return when(cal.get(Calendar.DAY_OF_WEEK)) {
        1 -> "6"  //일
        2 -> "0"  // 월...
        3 -> "1"
        4 -> "2"
        5 -> "3"
        6 -> "4"
        7 -> "5"
        else -> "-999"
    }
}