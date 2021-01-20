package com.example.ttimer

import android.content.Context
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

public var delmode: Boolean = false
public var addmode: Boolean = false
public lateinit var mainPrefs: SharedPreferences

class MainActivity : AppCompatActivity()
{
    //VARIABLES
    //Global vars
    //public var delmode: Boolean = false
    //public var addmode: Boolean = false
    //Preferences
    //public lateinit var mainPrefs: SharedPreferences
    // VARs VALs
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""
    //Arrays Adapter
    private val getArrayList = ArrayList<Item>()
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RVadapter

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.layer2.visibility = View.INVISIBLE //TODO use fragments
        //https://devofandroid.blogspot.com/2018/03/add-back-button-to-action-bar-android.html
        this.layer1.visibility = View.VISIBLE
        timePicker.setIs24HourView(true)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerViewItems.layoutManager = linearLayoutManager

        mainPrefs = getPreferences(MODE_PRIVATE)
        getDB()
        timer.start()
        //-------------ADDING
        b_add.setOnClickListener() {
            this.layer1.visibility = View.INVISIBLE
            this.layer2.visibility = View.VISIBLE
            addmode = true

        }
        //DELMODE
        b_del.setOnClickListener() {
            if(delmode == true){
                b_del.background.setTint(getColor(R.color.button_back))
                getDB()
                delmode = false
            }else{
                b_del.background.setTint(getColor(R.color.button_select))
                delmode = true
            }

            /*mainPrefs.edit().clear().apply()
            getDB()
            //TODO delete single item
            Toast.makeText(this, "Dataset Cleared", Toast.LENGTH_SHORT).show()*/
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
    private val timer = object: CountDownTimer( 1 * 60 * 60 * 1000, 1 * 20 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            refreshTime()
        }
        override fun onFinish() {
            Toast.makeText(applicationContext, "timer finished", Toast.LENGTH_SHORT).show()
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }


    // TODO recycler View

    //-------------------ADDITEM

    fun addItem(view: View) {

        var index = mainPrefs.getInt("index", 0)
        index++



        addText = tb_add_text.text.toString()
        addDate = datePicker.dayOfMonth.toString() + "." + (datePicker.month+1) + "." + datePicker.year
        addTime = timePicker.hour.toString() + ":" + timePicker.minute.toString()

        val addItemString = arrayListOf<String>(
            index.toString(),
            addText.toString(),
            datePicker.dayOfMonth.toString(),
            (datePicker.month + 1).toString(),
            datePicker.year.toString(),
            putTime(timePicker.hour),
            putTime(timePicker.minute)
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

    public fun refreshTime() {
        for(item in getArrayList.indices) {
            getSpanString(item)
        }
        if (getArrayList.isEmpty()){
            Toast.makeText(this, "No Items saved", Toast.LENGTH_SHORT)
            }
        adapter = RVadapter(getArrayList, delmode)
        recyclerViewItems.adapter = adapter
    }

    fun getDB() {
        getArrayList.clear()
        var getindex = mainPrefs.getInt("index", 0)
        if (getindex > 0) {
            while (getindex > 0) {

                var getStringItem = getListString("Item $getindex")
                Toast.makeText(this, getStringItem.toString(), Toast.LENGTH_LONG).show()
                Toast.makeText(this, getindex.toString(), Toast.LENGTH_SHORT).show()
                val getDateTime: LocalDateTime = getTime(getStringItem)
                val getItem = Item(getindex, getStringItem[1], getDateTime, "first input")
                getArrayList.add(getItem)
                refreshTime()
                 getindex += -1
             }
        }
    }
    fun getSpanString(item: Int) {
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
    }

    private fun getTime(getItem: ArrayList<String>): LocalDateTime{
        var getDay: Int = getItem[2].toInt()
        var getMonth: Int = getItem[3].toInt()
        var getYear: Int = getItem[4].toInt()
        var getHour: Int = getItem[5].toInt()
        var getMinute: Int = getItem[6].toInt()
        return LocalDateTime.of(getYear, getMonth, getDay, getHour, getMinute)
    }

    private fun putTime(time: Int): String {
        var stringMinute = ""
        if (time<10){ stringMinute = "0" + time.toString() }
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
    fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
 //END
}