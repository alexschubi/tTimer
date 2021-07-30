package com.alexschubi.ttimer

import android.text.TextUtils
import android.util.Log
import java.time.LocalDateTime
import android.view.View
import androidx.preference.PreferenceManager
import java.util.*

class Functions {

    fun getTime(getItem: ArrayList<String>): LocalDateTime? {
        var tempDateTime: LocalDateTime? = null
        if (getItem[3].toIntOrNull() == null) {
            tempDateTime = null
        } else {
            val getDay: Int = getItem[2].toInt()
            val getMonth: Int = getItem[3].toInt()
            val getYear: Int = getItem[4].toInt()
            val getHour: Int = getItem[5].toInt()
            val getMinute: Int = getItem[6].toInt()
            tempDateTime = LocalDateTime.of(getYear, getMonth, getDay, getHour, getMinute)
        }
        return tempDateTime
    }
    /*fun putTime(time: Int?): String {
        var stringMinute = ""
        if (time != null) {
            stringMinute = if (time<10){
                "0$time"
            } else{
                time.toString()
            }
        }
        return stringMinute
    }*/
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
                    val getItem = Item(
                        getStringItem[0].toInt(),
                        getStringItem[1],
                        getTime(getStringItem),
                        null,
                        getStringItem[7].toBoolean(),
                        false
                    )
                    getArrayList.add(getItem)
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
                if (getArrayList[item].Date != null) {
                    getArrayList[item].Span = getSpanString(getArrayList[item].Date!!)
                    Log.d("Item","got Span of Item $item @ "+ getArrayList[item].Span)
                }
            }
        }
    }
//TODO better span texting
    fun getSpanString(itemDateTime: LocalDateTime): String{
        var testOutLine: String = ""
        val currentDateTime = LocalDateTime.now()
            if (itemDateTime.isAfter(currentDateTime)) {
                when (itemDateTime.year - currentDateTime.year) {
                    0 -> when (itemDateTime.dayOfYear - currentDateTime.dayOfYear) {
                        0 -> when (itemDateTime.hour - currentDateTime.hour) {
                            0 -> when (itemDateTime.minute - currentDateTime.minute) {
                                0 -> testOutLine += "Now"
                                1 -> testOutLine += "1 Minute"
                                else -> testOutLine += (itemDateTime.minute - currentDateTime.minute).toString() + " Minutes "
                            }
                            1 -> testOutLine += "next Hour"
                            else -> testOutLine += (itemDateTime.hour - currentDateTime.hour).toString() + " Hours "
                        }
                        1 -> testOutLine += "tomorrow"
                        else -> testOutLine += (itemDateTime.dayOfYear - currentDateTime.dayOfYear).toString() + " Days "
                    }
                    1 -> testOutLine += "Next Year "
                    else -> testOutLine += (itemDateTime.year - currentDateTime.year).toString() + " Years "
                }
            } else {
                testOutLine += "gone"
            }
        return testOutLine
    }

    fun showKeyboard(view: View) {
        inputMethodManager.showSoftInput(view, 0)
    }
    fun hideKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun applyFirebase(){
        prefNotificationsenabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("pref_notificaions_enabled", true)
        prefSyncEnabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("pref_sync_enable", true)
        prefSendFirebaseenabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("pref_firebase_enabled", false)
        prefSyncConnection = PreferenceManager.getDefaultSharedPreferences(mContext).getString("pref_sync_connection", "").toString()
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(prefSendFirebaseenabled)
        firebaseAnalytics.setAnalyticsCollectionEnabled(prefSendFirebaseenabled)
        Log.i("Analytics", "Analytics " + prefSendFirebaseenabled.toString())
    }
}