package com.example.ttimer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.main_toolbar.*
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
lateinit var mContext: Context
lateinit var suppFragManager: FragmentManager
lateinit var suppActionBar: androidx.appcompat.app.ActionBar
lateinit var inputMethodManager: InputMethodManager
val getArrayList = ArrayList<Item>()

class MainActivity : AppCompatActivity()
{
    //Arrays Adapter
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

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

        super.onCreate(savedInstanceState)
        mContext = this
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        suppActionBar = supportActionBar!!
        suppActionBar.setCustomView(R.layout.main_toolbar)
        suppActionBar.setDisplayShowCustomEnabled(true)
        suppFragManager = supportFragmentManager

        setContentView(R.layout.activity_main)
        mainPrefs = getPreferences(MODE_PRIVATE)
        suppPrefs = getPreferences(MODE_PRIVATE)
        timer.start()
        Functions().getDB()

        b_settings.setOnClickListener(){suppFragManager.beginTransaction().replace(R.id.BaseLayout, fragment_settings()).commit()}
    }
    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            Functions().refreshTime()
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
}