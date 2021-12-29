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
import xyz.alexschubi.ttimer.itemlist.fragment_item_list
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
lateinit var mContext: Context //TODO dont use
lateinit var mapplication: Application
lateinit var mainActivity: MainActivity
lateinit var inputMethodManager: InputMethodManager
lateinit var firebaseAnalytics: FirebaseAnalytics
lateinit var firebaseCrashlytics: FirebaseCrashlytics
lateinit var localDB: ItemsDatabase
lateinit var suppActionBar : ActionBar
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE dd.MM.yyyy HH:mm")

class MainActivity : AppCompatActivity() {

    //TODO undo button after delete
    //TODO Bug when cxhanging theme sortmode is shown in settings
    //TODO bug when adding a new item sometimes not adding for real
    //TODO rewrite notifications for db

    override fun onCreate(savedInstanceState: Bundle?) {

        mContext = this
        mainActivity = this
        mapplication = this.application
        localDB = ItemsDatabase.getDatabase(this)!!
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        //TODO use navigation-back instead of direct transitions
        suppActionBar = supportActionBar!!
        suppActionBar.setDisplayShowCustomEnabled(true)

        mainPrefs = getPreferences(MODE_PRIVATE)
        suppPrefs = getPreferences(MODE_PRIVATE)
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(mContext)
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
                        suppActionBar.setCustomView(R.layout.list_toolbar)
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
            mContext = context
            Log.d("NotificationReceiver", "triggered")
            mainPrefs =
                context.getSharedPreferences("mainPrefs", AppCompatActivity.MODE_PRIVATE)
            suppPrefs =
                context.getSharedPreferences("suppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("ItemArray")!!
            Log.d("NotificationReceiver", "got Item " + editItemArray)
            var editItem = Functions().ItemFromArray(editItemArray)
            val notification = NotificationUtils().getNotificationBuilder(editItemArray).build()
            NotificationUtils().getManager().notify(editItem.Index, notification)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            mContext = context
            Log.i("NotificationSnoozeReceiver", "trigged")
            mainPrefs =
                context.getSharedPreferences("mainPrefs", AppCompatActivity.MODE_PRIVATE)
            suppPrefs =
                context.getSharedPreferences("suppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            editItem.Date = LocalDateTime.now().plusMinutes(10)
            Functions().saveItem(editItem)
            NotificationUtils().cancelNotification(editItem)
            NotificationUtils().makeNotification(editItem)
            pendResult.finish()
        }
    }
    open class NotificationDismissReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            mContext = context
            Log.i("NotificationDismissReceiver", "trigged")
            mainPrefs =
                context.getSharedPreferences("mainPrefs", AppCompatActivity.MODE_PRIVATE)
            suppPrefs =
                context.getSharedPreferences("suppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            editItem.Deleted = true
            Functions().saveItem(editItem)
            NotificationUtils().cancelNotification(editItem)
            pendResult.finish()
        }
    }
}