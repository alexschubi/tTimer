package com.alexschubi.ttimer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import java.time.format.DateTimeFormatter
import java.util.prefs.Preferences
import kotlin.system.exitProcess

lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
lateinit var mContext: Context
lateinit var suppFragManager: FragmentManager
lateinit var suppActionBar: ActionBar
lateinit var inputMethodManager: InputMethodManager
lateinit var firebaseAnalytics: FirebaseAnalytics
lateinit var firebaseCrashlytics: FirebaseCrashlytics

var prefNotificationsenabled: Boolean = true
var prefSyncEnabled: Boolean = false
var prefSyncConnection: String = ""
var prefSendFirebaseenabled: Boolean = false

val getArrayList = ArrayList<Item>()

class MainActivity : AppCompatActivity()
{
    //Arrays Adapter
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    //START
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        suppActionBar = supportActionBar!!
        suppActionBar.setCustomView(R.layout.main_toolbar)
        suppActionBar.setDisplayShowCustomEnabled(true)
        suppActionBar.customView.b_settings.visibility = View.VISIBLE
        suppActionBar.customView.b_back.visibility = View.GONE
        suppFragManager = supportFragmentManager
        //TODO use navigation-back instead of direct transitions

        setContentView(R.layout.activity_main)
        mainPrefs = getPreferences(MODE_PRIVATE)
        suppPrefs = getPreferences(MODE_PRIVATE)

        FirebaseApp.initializeApp(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(mContext)
        firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        Functions().applyFirebase()

        timer.start()
        Functions().getDB()

        b_settings.setOnClickListener(){
            NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_ItemList_to_fragment_settings)
            suppActionBar.customView.b_settings.visibility = View.GONE
            suppActionBar.customView.b_back.visibility = View.VISIBLE
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        suppActionBar.customView.b_settings.visibility = View.VISIBLE
        suppActionBar.customView.b_back.visibility = View.GONE
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.b_back -> {
                NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_fragment_settings_to_ItemList)
                suppActionBar.customView.b_settings.visibility = View.VISIBLE
                suppActionBar.customView.b_back.visibility = View.GONE
                return true
            }
        }
        return super.onContextItemSelected(item)
    }*/
}