package xyz.alexschubi.ttimer

import android.app.AlarmManager
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import xyz.alexschubi.ttimer.data.ItemsDatabase
import java.time.ZoneId
import java.time.ZonedDateTime

class NotificationUtils(nContext: Context) : ContextWrapper(nContext) {
    private var notificationManager: NotificationManager? = null
    lateinit var mContext: Context
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
            mContext = nContext
            localDB = ItemsDatabase.getDatabase(nContext)!!
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

    fun getNotificationBuilder(editItem: ArrayList<String>): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity.NotificationReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, editItem[0].toInt(), intent, 0)

        val snoozeIntent = Intent(this, MainActivity.NotificationSnoozeReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, editItem[0])
            putExtra("currentItem", editItem)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(this, editItem[0].toInt(), snoozeIntent, 0)

        val dismissIntent = Intent(this, MainActivity.NotificationDismissReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, editItem[0])
            putExtra("currentItem", editItem)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(this, editItem[0].toInt(), dismissIntent, 0)

        return NotificationCompat.Builder(
            applicationContext,
            editItem[0].toString()
        ).apply {
            setChannelId(applicationContext.packageName)
            setSmallIcon(R.drawable.ttimer_notification_pic)
            setContentIntent(pendingIntent)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.ttimer_logo
                )
            )
            setContentTitle("Timer reached")
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(editItem[1])
            )
            setDeleteIntent(dismissPendingIntent)
            addAction(R.drawable.ic_baseline_more_time_24, "Delay 10min", snoozePendingIntent)
            addAction(R.drawable.ic_baseline_delete_outline_24, "Dismiss", dismissPendingIntent)
            setGroup("tTimer")
            setGroupSummary(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
            build()

        }
    }

    fun makeNotification(editItem: Item) { //TODO use work-manager
        val zonedItemDateTime = editItem.Date!!.atZone(ZoneId.systemDefault())
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mContext, MainActivity.NotificationReceiver::class.java).putExtra("ItemArray", Functions().getItemArray(editItem))
        val pendingIntent = PendingIntent.getBroadcast(mContext, editItem.Index, intent, PendingIntent.FLAG_ONE_SHOT)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            zonedItemDateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
        Log.i("AlarmManager", "doAlarm Item: ${editItem.Index} in " +
                "${(zonedItemDateTime.toInstant().minusMillis(ZonedDateTime.now().toInstant().toEpochMilli())).toEpochMilli()} milliSeconds with" + pendingIntent.toString())
    }
    fun cancelNotification(cancelItem: Item) {
        Log.d("cancelNotification", "got Item $cancelItem for canceling")
        val intent = Intent(mContext, MainActivity.NotificationReceiver::class.java).putExtra("ItemArray", Functions().getItemArray(cancelItem))
        PendingIntent.getBroadcast(mContext, cancelItem.Index, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel()
        getManager().cancel(cancelItem.Index)
        Log.i("Notification", "canceled old pendingInent")
    }

}