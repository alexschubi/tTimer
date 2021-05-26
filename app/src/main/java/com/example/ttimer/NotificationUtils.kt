package com.example.ttimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationUtils(base: Context) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    private fun createChannels() {
        val channel = NotificationChannel(
            applicationContext.packageName,
            "Timer reached",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "descriptionText"
            enableLights(true)
            enableVibration(true)
            canShowBadge()
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.createNotificationChannel(channel)
    }

    fun getManager() : NotificationManager {
        if (notificationManager == null) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager as NotificationManager
    }

    fun getNotificationBuilder(currentItemString: ArrayList<String>): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        //NotificationManagerCompat.from(this)
        //    .notify(getArrayList[item].Index, builder.build())
        return NotificationCompat.Builder(
            applicationContext,
            currentItemString[0]
        ).apply {
            setChannelId(applicationContext.packageName)
            setSmallIcon(R.drawable.ttimer_notification_pic)
            setContentIntent(pendingIntent)
            setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.ttimer_logo
                )
            )
            setContentTitle("Timer reached")
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(currentItemString[0] + currentItemString[1])
            )
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
        }
    }
}