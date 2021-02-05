package com.example.ttimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

public var delmode: Boolean = false
public var addmode: Boolean = false
public lateinit var mainPrefs: SharedPreferences
public  val getArrayList = ArrayList<Item>()

class MainActivity : AppCompatActivity()
{
    //VARIABLES
    //Global vars
    //public var delmode: Boolean = false
    //public var addmode: Boolean = false
    //Preferences
    //public lateinit var mainPrefs: SharedPreferences
    // VARs VALs
    private var addText: String = ""
    private var addDate: String = ""
    private var addTime: String = ""
    private var addNotified: Boolean = false
    //Arrays Adapter
    //private val getArrayList = ArrayList<Item>()
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RVadapter
    private var notificationManager: NotificationManager? = null


    //TODO subroutine for timer
    //TODO display notifictions if timer reached

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.layer2.visibility = View.INVISIBLE
        //TODO use Fragments
        this.layer1.visibility = View.VISIBLE
        //https://devofandroid.blogspot.com/2018/03/add-back-button-to-action-bar-android.html
        timePicker.setIs24HourView(true)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerViewItems.layoutManager = linearLayoutManager

        mainPrefs = getPreferences(MODE_PRIVATE)
        getDB()
        timer.start()
        //-------------ADDMODE
        b_add.setOnClickListener() {
            this.layer1.visibility = View.INVISIBLE
            this.layer2.visibility = View.VISIBLE
            addmode = true
        }
        //-------------DELMODE
        b_del.setOnClickListener() {
            if(delmode == true){
                b_del.background.setTint(getColor(R.color.button_back))
                getDB()
                timer.start()
                delmode = false
            }else{
                timer.cancel()
                b_del.background.setTint(getColor(R.color.button_select))
                delmode = true
            }
        }
        //Create NotificationChannel
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel = NotificationChannel(
                 applicationContext.packageName,
                 "Timer reached",
                 NotificationManager.IMPORTANCE_DEFAULT
             ).apply {
                 description = "descriptionText"
                 enableLights(true)
                 canShowBadge()
             }
             /* val channel = NotificationManagerCompat.from(this)
                     .createNotificationChannel(NotificationChannel("${applicationContext.packageName}-tTimer",
                         "Ttimer", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "tTimer-Notification-Channel" })*/
             notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             notificationManager?.createNotificationChannel(channel)
         }
    }

    override fun onBackPressed(){
        if (addmode){
            getDB()
            hideKeyboard()
            addmode = false
            this.layer1.visibility = View.VISIBLE
            this.layer2.visibility = View.INVISIBLE
        }
    }

    //TIMER to refresh DB
    private val timer = object: CountDownTimer( 1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            refreshTime()
        }
        override fun onFinish() {
            Toast.makeText(applicationContext, "timer finished", Toast.LENGTH_SHORT).show()
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }


    //-------------------ADDITEM
    fun addItem(view: View) {

        var index = mainPrefs.getInt("index", 0)
        index++

        addText = tb_add_text.text.toString()
        addDate = datePicker.dayOfMonth.toString() + "." + (datePicker.month+1) + "." + datePicker.year
        addTime = timePicker.hour.toString() + ":" + timePicker.minute.toString()

        val addItemString = arrayListOf<String>(
            index.toString(),
            addText,
            datePicker.dayOfMonth.toString(),
            (datePicker.month + 1).toString(),
            datePicker.year.toString(),
            putTime(timePicker.hour),
            putTime(timePicker.minute),
            addNotified.toString()
        )
        //Save in tinyDB
        // https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo
        //converted to Kotlin and works somehow

        putListString("Item $index", addItemString)
        mainPrefs.edit().putInt("index", index).apply()

        //CLOSE addView
        getDB()
        hideKeyboard()
        addmode = false
        this.layer1.visibility = View.VISIBLE
        this.layer2.visibility = View.INVISIBLE
    }

    fun refreshTime() {
        for(item in getArrayList.indices) {
            getSpanString(item)
        }
        Log.d("getArrayList.indices", getArrayList.indices.toString())
        if (getArrayList.isEmpty()){
            Toast.makeText(this, "No Items saved", Toast.LENGTH_SHORT).show()
            }
        adapter = RVadapter(getArrayList)
        recyclerViewItems.adapter = adapter
    }

    private fun getDB() {
        getArrayList.clear()
        var getindex = mainPrefs.getInt("index", 0)
        if (getindex > 0) {
            while (getindex > 0) {

                val getStringItem = getListString("Item $getindex")
                if(getStringItem.isNotEmpty()){
                    val getDateTime: LocalDateTime = getTime(getStringItem)
                    val getItem = Item(getStringItem[0].toInt(), getStringItem[1], getDateTime, "first input", getStringItem[7].toBoolean())
                    getArrayList.add(getItem)
                }
                Log.d("saved Items count", getindex.toString())
                getindex += -1
             }
            refreshTime()
            Log.d("saved Items break", "BREAK")
        }
    }

    fun editItem(index: Int){ //TBD
        layer2.visibility = View.VISIBLE
        layer1.visibility = View.INVISIBLE
        addmode = true

        tb_add_text.setText(getArrayList[index].Text)
        datePicker.updateDate(getArrayList[index].Date.year, getArrayList[index].Date.monthValue, getArrayList[index].Date.dayOfMonth)
        timePicker.hour = getArrayList[index].Date.hour
        timePicker.minute = getArrayList[index].Date.minute

    }

    private fun getSpanString(item: Int) {
        var testOutLine: String = ""
        val currentDateTime = LocalDateTime.now()
        if (getArrayList[item].Date.isAfter(currentDateTime)) {
            when (getArrayList[item].Date.year - currentDateTime.year) {
                0 -> when (getArrayList[item].Date.dayOfYear - currentDateTime.dayOfYear) {
                    0 -> when (getArrayList[item].Date.hour - currentDateTime.hour) {
                        0 -> when (getArrayList[item].Date.minute - currentDateTime.minute) {
                            0 -> testOutLine += "Now"
                            1 -> testOutLine += "1 Minute"
                            else -> testOutLine += (getArrayList[item].Date.minute - currentDateTime.minute).toString() + " Minutes "
                        }
                        1 -> testOutLine += "1 Hour"
                        else -> testOutLine += (getArrayList[item].Date.hour - currentDateTime.hour).toString() + " Hours "
                    }
                    1 -> testOutLine += "tomorrow"
                    else -> testOutLine += (getArrayList[item].Date.dayOfYear - currentDateTime.dayOfYear).toString() + " Days "
                }
                1 -> testOutLine += "Next Year "
                else -> testOutLine += (getArrayList[item].Date.year - currentDateTime.year).toString() + " Years "
            }
        } else {
            testOutLine += "Date is in the past"

            if(!getArrayList[item].Notified) {
                val builder = NotificationCompat.Builder(
                    applicationContext,
                    getArrayList[item].Index.toString()
                ).apply {
                    setChannelId(applicationContext.packageName)
                    setSmallIcon(R.drawable.ttimer_logo)
                    setContentTitle("Timer reached")
                    //setContentText(getArrayList[item].Text)
                    setStyle(NotificationCompat.BigTextStyle().bigText(getArrayList[item].Text))
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setAutoCancel(true)
                }
                NotificationManagerCompat.from(this)
                    .notify(getArrayList[item].Index, builder.build())

                //TODO change notified in prefs
                val currentItemString = getListString("Item ${item+1}")
                if(currentItemString.isNotEmpty()) {
                    Log.d("notify Item", "${currentItemString[0]}  - ${item + 1}")
                    currentItemString[7] = true.toString()
                    putListString("Item ${item + 1}", currentItemString)
                    getArrayList[item].Notified = true
                }
            }else{
                val currentItemString = getListString("Item ${item+1}")
                Log.d("already notified Item", currentItemString[0] + " " + currentItemString[7])
            }

        }
        getArrayList[item].Span = testOutLine
    }

    private fun getTime(getItem: ArrayList<String>): LocalDateTime{
        val getDay: Int = getItem[2].toInt()
        val getMonth: Int = getItem[3].toInt()
        val getYear: Int = getItem[4].toInt()
        val getHour: Int = getItem[5].toInt()
        val getMinute: Int = getItem[6].toInt()
        return LocalDateTime.of(getYear, getMonth, getDay, getHour, getMinute)
    }

    private fun putTime(time: Int): String {
        var stringMinute = ""
        if (time<10){ stringMinute = "0$time" }
        else{ stringMinute = time.toString() }
        return stringMinute
    }

    private fun putListString(key: String?, stringList: ArrayList<String>) {
        val myStringList = stringList.toTypedArray()
        mainPrefs.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
    }
    private fun getListString(key: String?): ArrayList<String> {
        return ArrayList(listOf(*TextUtils.split(mainPrefs.getString(key, ""),"‚‗‚")))
    }

    //HIDE KEYBOARD
    private fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
 //END
}