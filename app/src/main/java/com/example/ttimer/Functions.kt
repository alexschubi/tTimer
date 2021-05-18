package com.example.ttimer

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import java.time.LocalDateTime
import android.content.*
import java.security.AccessController.getContext
import java.util.*

class Functions {
    fun getTime(getItem: ArrayList<String>): LocalDateTime {
        val getDay: Int = getItem[2].toInt()
        val getMonth: Int = getItem[3].toInt()
        val getYear: Int = getItem[4].toInt()
        val getHour: Int = getItem[5].toInt()
        val getMinute: Int = getItem[6].toInt()
        return LocalDateTime.of(getYear, getMonth, getDay, getHour, getMinute)
    }
    fun putTime(time: Int?): String {
        var stringMinute = ""
        if (time != null) {
            stringMinute = if (time<10){
                "0$time"
            } else{
                time.toString()
            }
        }
        return stringMinute
    }
    fun putListString(PrefKey: String?, stringList: ArrayList<String>) {
        val myStringList = stringList.toTypedArray()
        mainPrefs.edit().putString(PrefKey, TextUtils.join("‚‗‚", myStringList)).apply()
    }
    fun getListString(PrefKey: String?): ArrayList<String> {
        return ArrayList(listOf(*TextUtils.split(mainPrefs.getString(PrefKey, ""), "‚‗‚")))
    }

    fun getDB() {
        getArrayList.clear()
        var getindex = suppPrefs.getInt("ItemAmount", 0)
        Log.d("Preferences", "${mainPrefs.all.size} Items saved")
        Log.d("Preferences", suppPrefs.getInt("ItemAmount", 0).toString() + " Items registered")
        if (getindex >= 0) {
            while (getindex > 0) {
                val getStringItem = getListString("Item $getindex")
                if(!getStringItem[8].toBoolean()){
                    val getDateTime: LocalDateTime = getTime(getStringItem)
                    val getItem = Item(
                        getStringItem[0].toInt(),
                        getStringItem[1],
                        getDateTime,
                        "first input",
                        getStringItem[7].toBoolean(),
                        false
                    )
                    getArrayList.add(getItem)
                } else{
                    //getArrayList.add(Item(getindex, "", LocalDateTime.now(), "", true, true))
                }
                getindex--
            }
            refreshTime()
        }
        Log.d("getArrayList", getArrayList.toString())
    }
    fun refreshTime() {
        if (getArrayList.isEmpty()){
            Log.d("Preferences", "No Items saved")
        } else {
            for(item in getArrayList.indices) {
                getSpanString(item)
            }
        }
    }

    private fun getSpanString(item: Int) {
        val currentItemString = getListString("Item ${item + 1}")
        var testOutLine: String = ""
        val currentDateTime = LocalDateTime.now()
        if (getArrayList[item].Date == null) {
            if (getArrayList[item].Date!!.isAfter(currentDateTime)) {
                when (getArrayList[item].Date!!.year - currentDateTime.year) {
                    0 -> when (getArrayList[item].Date!!.dayOfYear - currentDateTime.dayOfYear) {
                        0 -> when (getArrayList[item].Date!!.hour - currentDateTime.hour) {
                            0 -> when (getArrayList[item].Date!!.minute - currentDateTime.minute) {
                                0 -> testOutLine += "Now"
                                1 -> testOutLine += "1 Minute"
                                else -> testOutLine += (getArrayList[item].Date!!.minute - currentDateTime.minute).toString() + " Minutes "
                            }
                            1 -> testOutLine += "1 Hour"
                            else -> testOutLine += (getArrayList[item].Date!!.hour - currentDateTime.hour).toString() + " Hours "
                        }
                        1 -> testOutLine += "tomorrow"
                        else -> testOutLine += (getArrayList[item].Date!!.dayOfYear - currentDateTime.dayOfYear).toString() + " Days "
                    }
                    1 -> testOutLine += "Next Year "
                    else -> testOutLine += (getArrayList[item].Date!!.year - currentDateTime.year).toString() + " Years "
                }
            } else {
                testOutLine += "Date is in the past"
            }
        }

        getArrayList[item].Span = testOutLine
    }
}