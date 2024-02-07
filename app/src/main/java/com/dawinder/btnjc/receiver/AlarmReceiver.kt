package com.dawinder.btnjc.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import com.dawinder.btnjc.R
import com.dawinder.btnjc.ui.activities.MainActivity


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // get NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // create notification
        val notification = NotificationCompat.Builder(context, "alarm_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // notification icon
            .setContentTitle("Alarm Notification") // notification title
            .setContentText("Your alarm is ringing!") // notification content
            .setPriority(NotificationCompat.PRIORITY_HIGH) //  set notification priority
            .setContentIntent(pendingIntent) // set action when click notification
            .setAutoCancel(true) // auto cancel when click notification
            .build()

        //send notification
        notificationManager.notify(1, notification)
    }
}