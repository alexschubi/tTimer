package com.example.ttimer

import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
val getArrayList = ArrayList<Item>()
lateinit var mContext: Context
lateinit var suppFragManager: FragmentManager

class MainActivity : AppCompatActivity()
{
    //Arrays Adapter
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    //TODO subroutine for timer
    //globalscope.launch {
    // runonuithread{}
    // }

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        FirebaseApp.initializeApp(this)
        if(BuildConfig.DEBUG){
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(false)
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
            Log.i("Analytics", "Analytics off")
        } else {
            Log.i("Analytics", "Analytics on")
        }

        mContext = this
        super.onCreate(savedInstanceState)
        suppFragManager = supportFragmentManager
        setContentView(R.layout.activity_main)
        mainPrefs = getPreferences(MODE_PRIVATE)
        suppPrefs = getPreferences(MODE_PRIVATE)
        timer.start()
        Functions().getDB()
    }

    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            Functions().refreshTime()
            //this.recyclerViewItems.adapter?.notifyDataSetChanged() TODO maybe reapply later
        }
        override fun onFinish() {
            Toast.makeText(applicationContext, "AFK?", Toast.LENGTH_SHORT).show()
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }

    open class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            val currentItemString: ArrayList<String> = intent.getStringArrayListExtra("currentItemString") as ArrayList<String>
            Log.d("AlarmManager", "Item ${currentItemString[0]} Timer Reached")
            val notificationUtils = NotificationUtils(context)
            val notification = notificationUtils.getNotificationBuilder(currentItemString).build()
            notificationUtils.getManager().notify((currentItemString[0].toInt()), notification)
            try { PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel()}finally {
                Log.d("PendingIntent", "already canceled")
            }
            pendResult.finish()
        }
    }
    //HIDE KEYBOARD
    private fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}