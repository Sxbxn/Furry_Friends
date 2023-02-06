package com.k_bootcamp.furry_friends.notification

import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.k_bootcamp.furry_friends.view.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext


class AlarmReceiver: BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        createNotificationChannel(context)
        val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        builder = NotificationCompat.Builder(context, CHANNEL_ID)
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent2 = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT)


        builder.apply{
            //알림창 제목
            setContentTitle("알람")
            setContentText("")
            //알림창 아이콘
            setSmallIcon(R.drawable.ic_media_play)
            //알림창 터치시 자동 삭제
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }


        val notification: Notification = builder.build()
        manager.notify(1, notification)
    }

    private fun createNotificationChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION
            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_NAME = "Routine Notification"
        private const val CHANNEL_DESCRIPTION = "루틴 알람"
        private const val CHANNEL_ID = "channel id"
    }


}