package com.alexschubi.ttimer

import android.text.TextUtils
import android.util.Log
import java.time.LocalDateTime
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.main_toolbar.view.*
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class Functions {

    fun getTimeOfArray(getItem: ArrayList<String>): LocalDateTime? {
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
        if (getindex >= 0) {
            while (getindex > 0) {
                val getStringItem = getListString("Item $getindex")
                if(!getStringItem[8].toBoolean()){
                    val getItem = Item(
                        getStringItem[0].toInt(),
                        getStringItem[1],
                        getTimeOfArray(getStringItem),
                        null,
                        getStringItem[7].toBoolean(),
                        false,
                        getStringItem[9]
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
            Log.d("Preferences.refresh", "No Items saved")
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
                                else -> testOutLine += (itemDateTime.minute - currentDateTime.minute).toString() + " Minutes"
                            }
                            1 -> testOutLine += ((itemDateTime.minute - currentDateTime.minute) + 60).toString() + " Minutes"
                            else -> testOutLine += (itemDateTime.hour - currentDateTime.hour).toString() + " Hours"
                        }
                        1 -> testOutLine += ((itemDateTime.hour - currentDateTime.hour) + 24).toString() + " Hours"
                        else -> testOutLine += (itemDateTime.dayOfYear - currentDateTime.dayOfYear).toString() + " Days"
                    }
                    1 -> when(Year.now().isLeap) {//Leap-Year
                        true -> testOutLine += ((itemDateTime.dayOfYear - currentDateTime.dayOfYear) + 366).toString() + " Days"
                        false -> testOutLine += ((itemDateTime.dayOfYear - currentDateTime.dayOfYear) + 365).toString() + " Days"
                    }
                    else -> testOutLine += (itemDateTime.year - currentDateTime.year).toString() + " Years"
                }
            } else {
                testOutLine += "gone"
            }
        return testOutLine
    }

    fun saveItem(editItem: Item){
        if(editItem.Index == -1) {
            editItem.Index = suppPrefs.getInt("ItemAmount", 0) + 1
            suppPrefs.edit().putInt("ItemAmount",editItem.Index).apply()
        }
        Log.d("Preferences.save", "ItemAmount is " + suppPrefs.getInt("ItemAmount", 0).toString())
        putListString("Item ${editItem.Index}", getItemArray(editItem))
    }
    fun getItemArray(editItem: Item): ArrayList<String> {
        val addItemString: ArrayList<String>
        if(editItem.Date == null) {
            addItemString = arrayListOf<String>(
                editItem.Index.toString(),
                editItem.Text,
                "",//Day
                "",//Month
                "",//Year
                "",//Hour
                "",//Minute
                false.toString(), //Notified
                false.toString(), //Deleted
                editItem.Color
            )
        } else {
            addItemString = arrayListOf<String>(
                editItem.Index.toString(),
                editItem.Text,
                editItem.Date!!.dayOfMonth.toString(),
                editItem.Date!!.monthValue.toString(),
                editItem.Date!!.year.toString(),
                editItem.Date!!.hour.toString(),
                editItem.Date!!.minute.toString(),
                false.toString(),
                false.toString(),
                editItem.Color
            )
        }
        Log.d("Preferences.save", "added Item: " + addItemString.toString())
        return addItemString
    }
    fun sortList(getList: ArrayList<Item>, getSortMode: Int): ArrayList<Item> {
        var sortMode = getSortMode
        when (getSortMode) {
            0 -> {}
            1 -> {
                getList.sortBy { it.Color}
                suppActionBar.customView.tv_sortMode.text = "sortBy Color"
            }
            2 -> {
                getList.sortBy { it.Date}
                suppActionBar.customView.tv_sortMode.text = "sortBy Date"
            }
            3 -> {
                getList.sortedWith(compareBy({it.Date}, {it.Color}))
                suppActionBar.customView.tv_sortMode.text = "sortBy Date>Color"
            }
            4 -> {
                getList.sortedWith(compareBy({it.Color}, {it.Date}))
                suppActionBar.customView.tv_sortMode.text = "sortBy Color>Date"
            }
            5 -> {
                getList.sortBy { it.Date}
                suppActionBar.customView.tv_sortMode.text = "sortBy "
            }
            6 -> {
                getList.sortBy { it.Date}
                suppActionBar.customView.tv_sortMode.text = "sortBy "
            }
            7 -> {
                getList.sortBy { it.Index}
                suppActionBar.customView.tv_sortMode.text = "sortBy"
                sortMode = 0
            }
            else -> sortMode = 0
        }
        suppPrefs.edit().putInt("sortMode", sortMode).apply()
        return getList
    }

    fun showKeyboard(view: View) {
        inputMethodManager.showSoftInput(view, 0)
    }
    fun hideKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun applyFirebase(){
        val prefNotificationsenabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("pref_notificaions_enabled", true)
        val prefSyncEnabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("pref_sync_enable", true)
        val prefSyncConnection = PreferenceManager.getDefaultSharedPreferences(mContext).getString("pref_sync_connection", "").toString()

        val prefSendFirebaseenabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("pref_firebase_enabled", false)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(prefSendFirebaseenabled)
        firebaseAnalytics.setAnalyticsCollectionEnabled(prefSendFirebaseenabled)
        Log.i("Analytics", "Analytics " + prefSendFirebaseenabled.toString())
    }
    fun applyTheme(){
        val prefTheme = PreferenceManager.getDefaultSharedPreferences(mContext).getString("pref_theme", "followSystem")
        when(prefTheme){
            "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "followSystem" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "followBattery" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
        Log.i("Theme", "set to $prefTheme")
    }

}