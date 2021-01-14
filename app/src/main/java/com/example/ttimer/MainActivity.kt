package com.example.ttimer

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.icu.text.DateIntervalFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity()
{
    //VARIABLES
    //Preferences

    lateinit var mainPrefs: SharedPreferences
    var firststart: Boolean = true


    // VARs VALs
    var delmode: Boolean = false
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""
    private val exList = ArrayList<ItemTest>()
    private val adapter = RVadapter(exList)
    //private var index: Int = 0
    var testText: String = ""
    //Arrays
    val arrayListSave = ArrayList<Item>()

    //START
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.layer2.visibility = View.INVISIBLE
        this.layer1.visibility = View.VISIBLE
        timePicker.setIs24HourView(true)

        mainPrefs = getPreferences(MODE_PRIVATE)
        firststart = mainPrefs.getBoolean("firstStart", true)
        getDB()


//-------------ADDING
        b_add.setOnClickListener() {
            Toast.makeText(this, "b_add clicked", Toast.LENGTH_SHORT).show()
            this.layer1.visibility = View.INVISIBLE
            this.layer2.visibility = View.VISIBLE

            //setContentView(R.layout.activity_add)
        }
        //DELMODE
        b_del.setOnClickListener() {
            Toast.makeText(this, "b_del clicked", Toast.LENGTH_SHORT).show()
            if (delmode)
            {
                b_del.background.setTint(getColor(R.color.color_header))
                this.delmode = false
                Toast.makeText(this, "delmode disabled", Toast.LENGTH_SHORT).show()
            } else
            {
                b_del.background.setTint(getColor(R.color.color_items_chosen))
                this.delmode = true
                tv_test_out.text = ""
                tv_test_out2.text = ""
                mainPrefs.edit().clear().apply()
                Toast.makeText(this, "delmode enabled", Toast.LENGTH_SHORT).show()
            }
        }

// TODO recycler View
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }


    //-------------------ADDITEM
    //TODO fragment host erstellen
    fun openAdd(view: View){
        Toast.makeText(this, "Button-ADD clicked", Toast.LENGTH_SHORT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addItem(view: View) {
        //val tinyDB: TinyDB = TinyDB(applicationContext)

        var index = mainPrefs.getInt("index", 0)
        index++



        addText = tb_add_text.text.toString()
        //addDate = calendarView.date.toString()
        addDate = datePicker.dayOfMonth.toString() + "." + (datePicker.month+1) + "." + datePicker.year
        addTime = timePicker.hour.toString() + ":" + timePicker.minute.toString()

        val addItem: Item = Item(
            index,
            addText,
            datePicker.dayOfMonth,
            (datePicker.month + 1),
            datePicker.year,
            timePicker.hour,
            timePicker.minute
        )
        val addItemString = arrayListOf<String>(
            index.toString(),
            addText.toString(),
            datePicker.dayOfMonth.toString(),
            (datePicker.month + 1).toString(),
            datePicker.year.toString(),
            putTime(timePicker.hour),
            putTime(timePicker.minute)
        )
        arrayListSave.add(addItem)//unused?

        //Save in tinyDB
        // https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo
        //converted to Kotlin and works somehow

        putListString("Item $index", addItemString)
        mainPrefs.edit().putInt("index", index).apply()

        //CLOSE addView
        getDB()


        hideKeyboard()
        this.layer1.visibility = View.VISIBLE
        this.layer2.visibility = View.INVISIBLE
        Toast.makeText(this, "b_add_final clicked", Toast.LENGTH_SHORT).show()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDB() {

        var getindex = mainPrefs.getInt("index", 0)
        var testStringSave: String= ""
        var gettestSting = ""
        var gettestSting2: String = ""
        if (getindex > 0){
             while(getindex > 0) {

                 var getItem = getListString("Item $getindex")

                 var getIndex: Int = getItem[0].toInt()
                 var getText: String = getItem[1]
                 var getDay: Int = getItem[2].toInt()
                 var getMonth: Int = getItem[3].toInt()
                 var getYear: Int = getItem[4].toInt()
                 var getHour: Int = getItem[5].toInt()
                 var getMinute: Int = getItem[6].toInt()
                 val getDateTime = LocalDateTime.of(getYear, getMonth, getDay, getHour, getMinute)//TODO save date in Prefs
                 val getCalendar = Calendar.getInstance()

                 //Test1
                 gettestSting += getindex.toString() + getItem[0] + ". [" + getItem[1] + "] \n " + getItem[2] + "." + getItem[3] + "." + getItem[4] + "  " + getItem[5] + ":" + getItem[6] + "\n\n"
                 tv_test_out.text = gettestSting

                 //Calculate remaining Time//TODO make better for year-change
                 var gettestString2l: String = getItem[0] + ". [" + getItem[1] + "] \n "
                 val currentDateTime = LocalDateTime.now()
                 val currentCalendar = Calendar.getInstance()

                 if (getDateTime > currentDateTime){
                     gettestString2l += (getDateTime.dayOfYear - currentDateTime.year).toString()
                 }else{
                     gettestString2l += "NEGATIVE DATE / TIME"
                 }

                 gettestSting2 += gettestString2l + "\n\n"
                 getindex += -1
             }
        }else{
            gettestSting2 = "NO ITEMS SAVED"
        }
        tv_test_out2.text = gettestSting2
    }

    private fun putTime(time: Int): String {
        var stringMinute = ""
        if (time<10){
            stringMinute = "0" + time.toString()
        }else{
            stringMinute = time.toString()
        }
        return stringMinute
    }

    fun deleteItem(view: View){

    }
    fun putListString(key: String?, stringList: ArrayList<String>) {
        val myStringList = stringList.toTypedArray()
        mainPrefs.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
    }
    fun getListString(key: String?): java.util.ArrayList<String> {
        return ArrayList(Arrays.asList(*TextUtils.split(mainPrefs.getString(key, ""),"‚‗‚")))
    }

    //HIDE KEYBOARD
    fun MainActivity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
 //END
}