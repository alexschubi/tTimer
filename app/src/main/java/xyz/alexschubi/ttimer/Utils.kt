package xyz.alexschubi.ttimer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import xyz.alexschubi.ttimer.data.ItemShort
import xyz.alexschubi.ttimer.data.ItemsDatabase
import xyz.alexschubi.ttimer.data.sItem
import java.time.*

// from https://proandroiddev.com/circular-reveal-in-fragments-the-clean-way-f25c8bc95257
fun View.startCircularReveal(oldX: Int, oldY: Int) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                    oldRight: Int, oldBottom: Int) {
            v.removeOnLayoutChangeListener(this)
            val endRadius = Math.hypot(width.toDouble(), height.toDouble())
            Log.d("CircularReveal", "from X$oldX and Y$oldY with radius$endRadius")
            skipBackPress = true
            ViewAnimationUtils.createCircularReveal(v, oldX, oldY, 0f, endRadius.toFloat()).apply {
                interpolator = DecelerateInterpolator(2f)
                duration = 500
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        skipBackPress = false
                        super.onAnimationEnd(animation)
                    }
                })
                start()
            }
        }
    })
}
fun View.exitCircularReveal(exitX: Int, exitY: Int, block: () -> Unit) {
    val startRadius = Math.hypot(this.width.toDouble(), this.height.toDouble())
    Log.d("CircularReveal", "from X$exitX and Y$exitY with radius$startRadius")
    skipBackPress = true
    ViewAnimationUtils.createCircularReveal(this, exitX, exitY, startRadius.toFloat(), 0f).apply {
        duration = 500
        interpolator = DecelerateInterpolator(2f)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                block()
                skipBackPress = false
                super.onAnimationEnd(animation)
            }
        })
        start()
    }
}

fun View.getCenterPosition(): IntArray{
    val posX = left + width/2
    val posY = top + height/2
    return intArrayOf(posX, posY)
}

fun ArrayList<String>.toItemShort(): ItemShort{
    val timestamp: Long? = if (this[2]=="") null else this[2].toLong()
    return ItemShort( this[0].toLong(), this[1], timestamp, this[3], this[4].toBoolean(), this[5].toBoolean())
}
fun ItemShort.toArrayList(): ArrayList<String>{
    val timestamp: String = if (this.TimeStamp==null) "" else this.TimeStamp.toString()
    return arrayListOf<String>(this.Index.toString(), this.Text, timestamp, this.Color, this.Notified.toString(), this.Deleted.toString())
}
fun ItemShort.toSItem(): sItem{//temporary until sItem and Item deleted
    val timespan: String? = if(TimeStamp==null) null else Functions().getSpanString(this.TimeStamp!!.toZonedDateTime().toLocalDateTime())!!
    return sItem(this.Index, this.Text,  this.TimeStamp, timespan, this.Color, this.Notified, this.Deleted)
}
fun sItem.toItemShort(): ItemShort{//temporary until sItem and Item deleted
    return ItemShort(Index, Text, TimeStamp, Color, Notified, Deleted)
}
fun ItemShort.dateTime(): ZonedDateTime?{
    return if (TimeStamp != null) TimeStamp!!.toZonedDateTime() else null
}

fun Item.toSItem(): sItem{
    return sItem(Index.toLong(), Text,
        Date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        Functions().getSpanString(Date), Color, Notified, Deleted
    )
}
fun sItem.toItem(): Item{
    var dateTime: LocalDateTime? = null
    if (TimeStamp!=null)
        dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeStamp!!), ZoneId.systemDefault())
    return Item(Index.toInt(), Text, dateTime, Functions().getSpanString(dateTime), Notified, Deleted, Color)
}
fun sItem.date(): ZonedDateTime?{
    var dateTime: ZonedDateTime? = null
    if(TimeStamp!=null){ dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(TimeStamp!!), ZoneId.systemDefault()) }
    return dateTime
}

interface ExitWithAnimation{
    var posX: Int?
    var posY: Int?
    fun isToBeExitedWithAnimation(): Boolean
}
inline fun FragmentManager.open(block: FragmentTransaction.() -> Unit) {
    beginTransaction().apply{
        block()
        setReorderingAllowed(true)
        commit()
    }
}
inline fun LocalDateTime.toSpan(): String{
    val itemDateTime = this
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

inline fun ZonedDateTime.toMilli(): Long{
    return toInstant().toEpochMilli()
}
inline fun Long.toZonedDateTime(): ZonedDateTime{
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}
fun ItemsDatabase.saveItemShort(item: ItemShort){
    if(item.Index == -1L){
        item.Index = localDB.itemsDAO().getItemsAmount() + 1
        this.itemsDAO().insert(item.toSItem())
    } else {
        this.itemsDAO().update(item.toSItem())
    }
    Log.d("localDB", "save $item")
}