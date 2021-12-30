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
import xyz.alexschubi.ttimer.data.sItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.Year
import java.time.ZoneId

// from https://proandroiddev.com/circular-reveal-in-fragments-the-clean-way-f25c8bc95257
fun View.startCircularReveal(oldX: Int, oldY: Int) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                    oldRight: Int, oldBottom: Int) {
            v.removeOnLayoutChangeListener(this)

            val diffX: Double = right.toDouble()/2 + oldX
            val diffY: Double = bottom.toDouble()/2 + oldY
            val radius = Math.hypot(diffX, diffY).toInt()
            Log.d("CircularReveal", "from X$diffX and Y$diffY with radius$radius")
            ViewAnimationUtils.createCircularReveal(v, oldX, oldY, 0f, radius.toFloat()).apply {
                interpolator = DecelerateInterpolator(1f)
                duration = 10000
                start()
            }
        }
    })
}
/**
 * Animate fragment exit using given parameters as animation end point. Runs the given block of code
 * after animation completion.
 *
 * @param exitX: Animation end point X coordinate.
 * @param exitY: Animation end point Y coordinate.
 * @param block: Block of code to be executed on animation completion.
 */
fun View.exitCircularReveal(exitX: Int, exitY: Int, block: () -> Unit) {
    val startRadius = Math.hypot(this.width.toDouble(), this.height.toDouble())
    ViewAnimationUtils.createCircularReveal(this, exitX, exitY, startRadius.toFloat(), 0f).apply {
        duration = 10000
        interpolator = DecelerateInterpolator(1f)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                block()
                super.onAnimationEnd(animation)
            }
        })
        start()
    }
}
/**
 * @return the position of the current [View]'s center in the screen
 */
fun View.findLocationOfCenterOnTheScreen(): IntArray {
    val positions = intArrayOf(0, 0)
    getLocationInWindow(positions)
    // Get the center of the view
    positions[0] = positions[0] + width / 2
    positions[1] = positions[1] + height / 2
    return positions
}
fun Item.toSItem(): sItem{
    return sItem(Index,
        Text,
        Date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        Functions().getSpanString(Date),
        Color,
        Notified,
        Deleted
    )
}
fun sItem.toItem(): Item{
    var dateTime: LocalDateTime? = null
    if (TimeStamp!=null)
        dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeStamp!!), ZoneId.systemDefault())
    return Item(Index,
        Text,
        dateTime,
        Functions().getSpanString(dateTime),
        Notified,
        Deleted,
        Color
    )
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