package xyz.alexschubi.ttimer

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import xyz.alexschubi.ttimer.data.ItemsDatabase
import xyz.alexschubi.ttimer.data.suppPreferences
import xyz.alexschubi.ttimer.itemlist.fragment_item_list
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
lateinit var mapplication: Application
lateinit var mainActivity: MainActivity
lateinit var inputMethodManager: InputMethodManager
lateinit var firebaseAnalytics: FirebaseAnalytics
lateinit var firebaseCrashlytics: FirebaseCrashlytics
lateinit var localDB: ItemsDatabase
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE dd.MM.yyyy HH:mm")

class MainActivity : AppCompatActivity() {

    //TODO undo button after delete
    //TODO rewrite notifications for db

    override fun onCreate(savedInstanceState: Bundle?) {

        mainActivity = this
        mapplication = this.application
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        localDB = ItemsDatabase.getDatabase(this)!!
        localDB.preferenceesDAO().insert(
            suppPreferences(1, "default", false, null, false, true, "2.0", 0, true)
        )
        //TODO last

        //TODO use navigation-back instead of direct transitions
        mainPrefs = getPreferences(MODE_PRIVATE)
        suppPrefs = getPreferences(MODE_PRIVATE)
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(mapplication)
        firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        Functions().applyFirebase()

        super.onCreate(savedInstanceState)
        Functions().applyTheme()
        setContentView(R.layout.activity_main)
        supportFragmentManager.open { replace(R.id.container, fragment_item_list.newInstance()) }
    }

    override fun onBackPressed() { //TODO fix animation call wen back pressed
        with(supportFragmentManager.findFragmentById(R.id.container)) {
            // Check if the current fragment implements the [ExitWithAnimation] interface or not
            // Also check if the [ExitWithAnimation.isToBeExitedWithAnimation] is `true` or not
            if ((this as? ExitWithAnimation)?.isToBeExitedWithAnimation() == true) { //TODO not triggered
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

    open class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.d("NotificationReceiver", "triggered")
            mainPrefs =
                context.getSharedPreferences("mainPrefs", AppCompatActivity.MODE_PRIVATE)
            suppPrefs =
                context.getSharedPreferences("suppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("ItemArray")!!
            Log.d("NotificationReceiver", "got Item " + editItemArray)
            var editItem = Functions().ItemFromArray(editItemArray)
            val notification = NotificationUtils(context).getNotificationBuilder(editItemArray).build()
            NotificationUtils(context).getManager().notify(editItem.Index, notification)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("NotificationSnoozeReceiver", "trigged")
            mainPrefs =
                context.getSharedPreferences("mainPrefs", AppCompatActivity.MODE_PRIVATE)
            suppPrefs =
                context.getSharedPreferences("suppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            editItem.Date = LocalDateTime.now().plusMinutes(10)
            Functions().saveSItemToDB(editItem.toSItem())
            NotificationUtils(context).cancelNotification(editItem)
            NotificationUtils(context).makeNotification(editItem)
            pendResult.finish()
        }
    }
    open class NotificationDismissReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("NotificationDismissReceiver", "trigged")
            mainPrefs =
                context.getSharedPreferences("mainPrefs", AppCompatActivity.MODE_PRIVATE)
            suppPrefs =
                context.getSharedPreferences("suppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            editItem.Deleted = true
            Functions().saveSItemToDB(editItem.toSItem())
            NotificationUtils(context).cancelNotification(editItem)
            pendResult.finish()
        }
    }
}