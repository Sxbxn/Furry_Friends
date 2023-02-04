package com.k_bootcamp.furry_friends

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    private fun holidayColor() {
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
        calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator()        )
    }
    inner class SundayDecorator: DayViewDecorator {
        val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(Color.RED))
        }
    }
    inner class SaturdayDecorator: DayViewDecorator {
        val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }
}