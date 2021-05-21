package com.example.ttimer

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_item_list.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

public lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
public  val getArrayList = ArrayList<Item>()
lateinit var mContext: Context
lateinit var suppFragManager: FragmentManager

class MainActivity : AppCompatActivity()
{
    // VARs VALs
    private var addText: String = ""
    private var addDate: String = ""
    private var addTime: String = ""
    //Arrays Adapter
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    //TODO subroutine for timer
    //globalscope.launch {
    // runonuithread{}
    // }

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
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
        //TODO multiple alarms dont stack
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            val currentItemString: ArrayList<String> = intent.getStringArrayListExtra("currentItemString") as ArrayList<String>
            Log.d("AlarmManager", "Item ${currentItemString?.get(0)} Timer Reached")
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