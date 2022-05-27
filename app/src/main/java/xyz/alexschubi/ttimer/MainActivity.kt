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
import xyz.alexschubi.ttimer.data.ItemsDatabase
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.data.suppPreferences
import xyz.alexschubi.ttimer.itemlist.fragment_item_list
import xyz.alexschubi.ttimer.livedata.LiveDataRecyclerViewFragment
import java.time.LocalDateTime
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

    //TODO undo button after delete
    companion object {
        private var openSItem: sItem? = null
        @JvmStatic
        fun newInstance(openWithItem: sItem? = null): MainActivity
                = MainActivity().apply {
            if(openWithItem != null){
                openSItem = openWithItem
            }
        }
    }


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
        if(openSItem != null){
            supportFragmentManager.open { replace(R.id.container, fragment_item_list.newInstance(openSItem)) }
        }else{
            supportFragmentManager.open { replace(R.id.container, fragment_item_list.newInstance()) }
        } //Useless if else case?

        //TODO use LiveData and remove normal RecyclerView
        supportFragmentManager.open { replace(R.id.container, LiveDataRecyclerViewFragment.newInstance()) }
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

            val editItemArray = intent.extras!!.getStringArrayList("ItemArray")!!
            Log.d("NotificationReceiver", "got Item " + editItemArray)
            var editItem = Functions().ItemFromArray(editItemArray)
            val notification = NotificationUtils(context).getNotificationBuilder(editItemArray).build()
            NotificationUtils(context).getManager().notify(editItem.Index, notification)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeShortReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("Notification", "SnoozeShortReceiver triggered")
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            NotificationUtils(context).cancelNotification(editItem)
            editItem.Date = LocalDateTime.now().plusMinutes(10)
            Functions().saveSItemToDB(editItem.toSItem())
            NotificationUtils(context).makeNotification(editItem)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeLongReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("Notification", "SnoozeLongReceiver triggered")
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            NotificationUtils(context).cancelNotification(editItem)
            editItem.Date = LocalDateTime.now().plusHours(3)
            Functions().saveSItemToDB(editItem.toSItem())
            NotificationUtils(context).makeNotification(editItem)
            pendResult.finish()
        }
    }
    open class NotificationDeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.i("Notification", "DeleteReceiver triggered")
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            NotificationUtils(context).cancelNotification(editItem)
            editItem.Deleted = true
            Functions().saveSItemToDB(editItem.toSItem())
            Log.i("localDB", "delted item ${editItem.Index}")
            pendResult.finish()
        }
    }
}