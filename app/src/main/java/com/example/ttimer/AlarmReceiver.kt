package com.example.ttimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService


open class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val currentItemString: ArrayList<String> = intent.getStringArrayListExtra("currentItemString") as ArrayList<String>
        Log.d("AlarmManager", "Item ${currentItemString?.get(0)} Timer Reached")
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder(currentItemString).build()
        notificationUtils.getManager().notify((currentItemString[0].toInt()), notification)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel()
    }
}