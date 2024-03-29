package xyz.alexschubi.ttimer

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import xyz.alexschubi.ttimer.data.sItem
import java.time.LocalDateTime
import java.time.Year

class  Functions {

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

    fun ItemFromArray(getStringItem: ArrayList<String>): Item {
        val getItem = Item(
            getStringItem[0].toInt(),
            getStringItem[1],
            getTimeOfArray(getStringItem),
            null,
            getStringItem[7].toBoolean(),
            getStringItem[8].toBoolean(),
            getStringItem[9]
        )

        return getItem
    }

    fun getSpanString(itemDateTime: LocalDateTime?): String?{
        if(itemDateTime==null) return null
        var testOutLine = ""
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
                testOutLine += "past"
            }
        return testOutLine
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
                editItem.Deleted.toString(), //Deleted
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
                editItem.Deleted.toString(),
                editItem.Color
            )
        }
        Log.d("Preferences.save", "added Item: " + addItemString.toString())
        return addItemString
    }

    fun sortMutableList(itemList: MutableList<sItem>, sortMode: Int): MutableList<sItem> {
        var sortedList: MutableList<sItem> = itemList
        when(sortMode){
            0 -> {sortedList = itemList.sortedBy { it.Index }.toMutableList() }
            1 -> {sortedList = itemList.sortedBy { it.Index }.reversed().toMutableList()}
            2 -> {sortedList = itemList.sortedBy { it.Color }.toMutableList() }
            3 -> {sortedList = itemList.sortedBy { it.Color }.reversed().toMutableList() }
            4 -> {sortedList = itemList.sortedBy { it.TimeStamp }.toMutableList() }
            5 -> {sortedList = itemList.sortedBy { it.TimeStamp }.reversed().toMutableList() }
            6 -> {}
            7 -> {}
            8 -> {}
            9 -> {}
        } //TODO more sort modes
        return sortedList
    }

    fun getSortedLiveData(sortMode: Int?): MutableList<sItem> { //TODO easier sorting
        var sortingMode = 0
        sortingMode = sortMode ?: localDB.preferencesDAO().getLast().SortMode
        var orderBySQL = "Index ASC"
        when(sortingMode){
            0 ->  {orderBySQL = "Index ASC"}
            1 ->  {orderBySQL = "Index DESC"}
            2 ->  {orderBySQL = "Color ASC"}
            3 ->  {orderBySQL = "Color DESC"}
            4 ->  {orderBySQL = "Date ASC"}
            5 ->  {orderBySQL = "Date DESC"}
            6 ->  {orderBySQL = "Date, Color ASC"}
            7 ->  {orderBySQL = "Date, Color DESC"}
            8 ->  {orderBySQL = "Color, Date ASC"}
            9 ->  {orderBySQL = "Color, Date DESC"}
            10 -> {orderBySQL = "Date IS NULL ASC, Date IS NOT NULL ASC, Color ASC"}
            11 -> {orderBySQL = "Date IS NULL DESC, Date IS NOT NULL DESC, Color DESC"}
            12 -> {orderBySQL = "Date IS NOT NULL ASC, Date IS NULL ASC, Color ASC"}
            13 -> {orderBySQL = "Date IS NOT NULL DESC, Date IS NULL DESC, Color DESC"}
            14 -> {orderBySQL = "Color ASC, Date IS NOT NULL DESC, Date IS NULL DESC"}
            15 -> {orderBySQL = "Color DESC, Date IS NOT NULL DESC, Date IS NULL DESC"}
            16 -> {orderBySQL = "Color ASC, Date IS NULL DESC, Date IS NOT NULL DESC"}
            17 -> {orderBySQL = "Color DESC, Date IS NULL DESC, Date IS NOT NULL DESC"}
            18 -> {orderBySQL = "Date IS NULL ASC, Date IS NOT NULL ASC"}
            19 -> {orderBySQL = "Date IS NULL DESC, Date IS NOT NULL DESC"}
            20 -> {orderBySQL = "Date IS NOT NULL ASC, Date IS NULL ASC"}
            21 -> {orderBySQL = "Date IS NOT NULL DESC, Date IS NULL DESC"}
        }
        return localDB.itemsDAO().getLiveDataItemsSorted(orderBySQL)
    }


    fun applyFirebase(){
        val prefSendFirebaseEnabled = localDB.preferencesDAO().getLast().FirebaseEnabled
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(prefSendFirebaseEnabled)
        firebaseAnalytics.setAnalyticsCollectionEnabled(prefSendFirebaseEnabled)
        Log.i("Analytics", "Analytics " + prefSendFirebaseEnabled.toString())
    }
    fun applyTheme(){
        val prefTheme = localDB.preferencesDAO().getLast().Theme
        when(prefTheme){
            "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "followSystem" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "followBattery" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
        Log.i("Theme", "set to $prefTheme")
    }
    fun applyNotificationSwitch(){
        val prefNotification = localDB.preferencesDAO().getLast().Notifications
        when (prefNotification) {
            true -> {}//TODO deactivate all notifications
            false -> {}//TODO activate all notifications
        }
    }

    fun saveSItemToDB(item: sItem){
        if(item.Index == -1L){
            item.Index = localDB.itemsDAO().getItemsAmount() + 1
            localDB.itemsDAO().insert(item)
        } else {
            localDB.itemsDAO().update(item)
        }
        Log.d("localDB", "save $item")
    }
    fun deleteItemFromDB(itemIndex: Long){
        val editItem = localDB.itemsDAO().get(itemIndex)
        if (editItem != null) {
            editItem.Deleted = true
            localDB.itemsDAO().update(editItem)
            Log.d("localDB", "changed Item $editItem")
           NotificationUtils(mapplication).cancelNotification(editItem.toItemShort())
        } else {
            Log.d("localDB", "could not get sItem")
        }
    }
    fun getSortedItemsWithSpan(): MutableList<sItem> {
        var items = getSortedLiveData(null)
        items.forEach {
            if(it.TimeStamp != null ) {
                it.Span = getSpanString(it.date()!!.toLocalDateTime())
            }
        }
        return items
    }
}
