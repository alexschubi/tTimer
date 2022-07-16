package xyz.alexschubi.ttimer

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import xyz.alexschubi.ttimer.data.ItemShort
import xyz.alexschubi.ttimer.data.ItemsDatabase
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.data.suppPreferences
import xyz.alexschubi.ttimer.livedata.LiveDataRecyclerViewFragment
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


lateinit var mapplication: Application
lateinit var mainActivity: MainActivity
lateinit var inputMethodManager: InputMethodManager
lateinit var firebaseAnalytics: FirebaseAnalytics
lateinit var firebaseCrashlytics: FirebaseCrashlytics
lateinit var localDB: ItemsDatabase
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE dd.MM.yyyy HH:mm")
var skipBackPress: Boolean = false

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivity = this
        mapplication = this.application
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        localDB = ItemsDatabase.getDatabase(this)!!
        localDB.preferencesDAO().insert(
            suppPreferences(1, "followSystem", false, null, false, true, "2.0", 0, true, "EE dd.MM.yyyy HH:mm")
        )
        Log.d("localDB.Items", localDB.itemsDAO().getAll().toString())
        Log.d("localDB.Preferences", localDB.preferencesDAO().getLast().toString())

        FirebaseApp.initializeApp(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(mapplication)
        firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        Functions().applyFirebase()
        Functions().applyTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get openItem and open AddFragment
        val bundle: Bundle? = intent.getBundleExtra("Bundle")
        if (bundle != null){
            val openShortItem: ItemShort? = bundle.getParcelable<ItemShort>("ItemShort")
            Log.d("MainActivity", "Intent Extras: $openShortItem")
            supportFragmentManager.open { replace(R.id.container, LiveDataRecyclerViewFragment.newInstance(openShortItem)) }
        } else {
            supportFragmentManager.open { replace(R.id.container, LiveDataRecyclerViewFragment.newInstance(null)) }
        }
    }

    override fun onBackPressed() { // from https://proandroiddev.com/circular-reveal-in-fragments-the-clean-way-f25c8bc95257
        if(!skipBackPress){
            with(supportFragmentManager.findFragmentById(R.id.container)) {
                if ((this as? ExitWithAnimation)?.isToBeExitedWithAnimation() == true) {
                    if (this.posX == null || this.posY == null) {
                        super.onBackPressed()
                    } else {
                        this.view?.exitCircularReveal(this.posX!!, this.posY!!) {
                            super.onBackPressed()
                        } ?: super.onBackPressed()
                    }
                } else {
                    super.onBackPressed()
                }
            }
        }
    }

    open class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("NotificationReceiver", "triggered")
            val bundle = intent.getBundleExtra("Bundle")
            val shortItem = bundle!!.getParcelable<ItemShort>("ItemShort")!!
            Log.d("MainActivity", "Intent Extras: $shortItem")
            val notification = NotificationUtils(context).getNotificationBuilder(shortItem).build()
            NotificationUtils(context).getManager().notify(shortItem.Index.toInt(), notification)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeShortReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("Notification", "SnoozeShortReceiver triggered")
            val bundle = intent.getBundleExtra("Bundle")
            val shortItem = bundle!!.getParcelable<ItemShort>("ItemShort")!!
            NotificationUtils(context).cancelNotification(shortItem)
            shortItem.TimeStamp = ZonedDateTime.now().plusMinutes(30).toMilli()
            Functions().saveSItemToDB(shortItem.toSItem())
            NotificationUtils(context).makeNotification(shortItem)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeLongReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("Notification", "SnoozeLongReceiver triggered")
            val bundle = intent.getBundleExtra("Bundle")
            val shortItem = bundle!!.getParcelable<ItemShort>("ItemShort")!!
            NotificationUtils(context).cancelNotification(shortItem)
            shortItem.TimeStamp = ZonedDateTime.now().plusDays(1).toMilli()
            Functions().saveSItemToDB(shortItem.toSItem())
            NotificationUtils(context).makeNotification(shortItem)
            pendResult.finish()
        }
    }
    open class NotificationDeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("Notification", "DeleteReceiver triggered")
            val bundle = intent.getBundleExtra("Bundle")
            val shortItem = bundle!!.getParcelable<ItemShort>("ItemShort")!!
            NotificationUtils(context).cancelNotification(shortItem)
            shortItem.Deleted = true
            Functions().saveSItemToDB(shortItem.toSItem())
            Log.i("localDB", "deleted item ${shortItem.Index}")
            pendResult.finish()
        }
    }
}