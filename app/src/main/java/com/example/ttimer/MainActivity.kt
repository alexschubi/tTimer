package com.example.ttimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


public var delmode: Boolean = false
public var addmode: Boolean = false
public lateinit var mainPrefs: SharedPreferences
public  val getArrayList = ArrayList<Item>()
lateinit var mContext: Context

class MainActivity : AppCompatActivity()
{
    // VARs VALs
    private var addText: String = ""
    private var addDate: String = ""
    private var addTime: String = ""
    //Arrays Adapter
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    private lateinit var linearLayoutManager: LinearLayoutManager


    //TODO subroutine for timer
    //globalscope.launch {
    // runonuithread{}
    // }

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        mContext = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.layer2.visibility = View.INVISIBLE
        //TODO use Fragments
        this.layer1.visibility = View.VISIBLE
        timePicker.setIs24HourView(true)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerViewItems.layoutManager = linearLayoutManager
        var adapter = RvAdapter(getArrayList)
        recyclerViewItems.adapter = adapter
        ItemTouchHelper(SwipeToDelete(adapter)).attachToRecyclerView(recyclerViewItems)


        mainPrefs = getPreferences(MODE_PRIVATE)
        timer.start()
        Functions().getDB()
        //-------------ADDMODE
        b_add.setOnClickListener() {
            this.layer1.visibility = View.INVISIBLE
            this.layer2.visibility = View.VISIBLE
            addmode = true
        }
        b_add_final.setOnClickListener(){addItem()}
    }
    override fun onBackPressed(){
        if (addmode){
            Functions().getDB()
            recyclerViewItems.adapter?.notifyDataSetChanged()
            hideKeyboard()
            addmode = false
            this.layer1.visibility = View.VISIBLE
            this.layer2.visibility = View.INVISIBLE
        }
    }
    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            Functions().refreshTime()
            recyclerViewItems.adapter?.notifyDataSetChanged()
        }
        override fun onFinish() {
            Toast.makeText(applicationContext, "AFK?", Toast.LENGTH_SHORT).show()
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }
    //-------------------ADDITEM
    private fun addItem() {
        var index = mainPrefs.all.size
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
            Functions().putTime(timePicker.hour),
            Functions().putTime(timePicker.minute),
            false.toString()
        )
        Functions().putListString("Item $index", addItemString)
        if(Functions().getTime(addItemString).isAfter(LocalDateTime.now())){
            makeNotification(addItemString)
        } else {
            Toast.makeText(this, "Item$index is in past", Toast.LENGTH_SHORT).show()
        }
        //CLOSE addView
        Functions().getDB()
        recyclerViewItems.adapter?.notifyDataSetChanged()
        Log.d("SharedPreferences", "added Item$index")
        hideKeyboard()
        addmode = false
        this.layer1.visibility = View.VISIBLE
        this.layer2.visibility = View.INVISIBLE
    }

    /*private fun refreshTime() {
        if (getArrayList.isEmpty()){
            Toast.makeText(this, "No Items saved", Toast.LENGTH_SHORT).show()
        } else {
            for(item in getArrayList.indices) {
                getSpanString(item)
            }
        }
        recyclerViewItems.adapter?.notifyDataSetChanged()
    }*/

    /*private fun getDB() {
        getArrayList.clear()
        var getindex = mainPrefs.all.size
        Log.d("Preferences", "contain: ${mainPrefs.all.size} Items")
        if (getindex > 0) {
            while (getindex > 0) {

                val getStringItem = getListString("Item $getindex")
                if(getStringItem.isNotEmpty()){
                    val getDateTime: LocalDateTime = getTime(getStringItem)
                    val getItem = Item(
                        getStringItem[0].toInt(),
                        getStringItem[1],
                        getDateTime,
                        "first input",
                        getStringItem[7].toBoolean()
                    )
                    getArrayList.add(getItem)
                }
                getindex += -1
             }
            refreshTime()
        }
    }*/

    fun editItem(index: Int){ //TBD
        layer2.visibility = View.VISIBLE
        layer1.visibility = View.INVISIBLE
        addmode = true

        tb_add_text.setText(getArrayList[index].Text)
        datePicker.updateDate(
            getArrayList[index].Date.year,
            getArrayList[index].Date.monthValue,
            getArrayList[index].Date.dayOfMonth
        )
        timePicker.hour = getArrayList[index].Date.hour
        timePicker.minute = getArrayList[index].Date.minute

    }


    /*private fun getSpanString(item: Int) {
        val currentItemString = getListString("Item ${item + 1}")
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
        }
        getArrayList[item].Span = testOutLine
    }*/

    private fun makeNotification(currentItemString: ArrayList<String>) {
        val zonedItemDateTime = Functions().getTime(currentItemString).atZone(ZoneId.systemDefault())
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).putStringArrayListExtra("currentItemString", currentItemString)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            zonedItemDateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
        Log.d("AlarmManager", "doAlarm Item: ${currentItemString[0]} in " +
                    "${(zonedItemDateTime.toInstant().minusMillis
                        (ZonedDateTime.now().toInstant().toEpochMilli())).toEpochMilli()} milliSeconds")
    }
    open class AlarmReceiver : BroadcastReceiver() {
        //TODO multiple alarms dont stack
        //TODO Broadcast receiver have to work in background
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
//Calculating FUNCTIONS
    /*private fun getTime(getItem: ArrayList<String>): LocalDateTime{
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
    fun getListString(key: String?): ArrayList<String> {
        return ArrayList(listOf(*TextUtils.split(mainPrefs.getString(key, ""), "‚‗‚")))
    }*/

    //HIDE KEYBOARD
    private fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
 //END
}