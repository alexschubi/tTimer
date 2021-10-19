package com.alexschubi.ttimer

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

lateinit var mainPrefs: SharedPreferences
lateinit var suppPrefs: SharedPreferences
lateinit var mContext: Context //TODO dont use
lateinit var mapplication: Application
lateinit var mainActivity: MainActivity
lateinit var suppFragManager: FragmentManager
lateinit var suppActionBar: ActionBar
lateinit var inputMethodManager: InputMethodManager
lateinit var firebaseAnalytics: FirebaseAnalytics
lateinit var firebaseCrashlytics: FirebaseCrashlytics

var getArrayList = ArrayList<Item>() //TODO dont use

class MainActivity : AppCompatActivity()
{
    //Arrays Adapter
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE dd.MM.yyyy HH:mm")

    //START
    override fun onCreate(savedInstanceState: Bundle?) {

        mContext = this
        mainActivity = this
        mapplication = this.application
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        suppActionBar = supportActionBar!!
        suppActionBar.setCustomView(R.layout.main_toolbar)
        suppActionBar.setDisplayShowCustomEnabled(true)
        suppActionBar.customView.b_settings.visibility = View.VISIBLE
        suppActionBar.customView.b_back.visibility = View.GONE
        suppActionBar.customView.sp_sortMode.visibility = View.VISIBLE

        suppFragManager = supportFragmentManager
        //TODO use navigation-back instead of direct transitions

        mainPrefs = getPreferences(MODE_PRIVATE)
        suppPrefs = getPreferences(MODE_PRIVATE)
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(mContext)
        firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        Functions().applyFirebase()
        Functions().getDB()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Functions().applyTheme()

        b_settings.setOnClickListener(){
            NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_ItemList_to_fragment_settings)
            suppActionBar.customView.b_settings.visibility = View.GONE
            suppActionBar.customView.b_back.visibility = View.VISIBLE
            suppActionBar.customView.sp_sortMode.visibility = View.GONE
        }
    }

    open class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            Log.d("NotificationReceiver", "triggered")
            mainPrefs = context.getSharedPreferences("mainPrefs", MODE_PRIVATE)
            suppPrefs = context.getSharedPreferences("suppPrefs", MODE_PRIVATE)
            val snoozeTime = 600000 //1Min=60*1000

            val editItemArray = intent.extras!!.getStringArrayList("ItemArray")!!
            //Log.d("NotificationReceiver", "get saved Item $editItemIndex")
            //val editItemArray: ArrayList<String> = Functions().getListString("Item $editItemIndex")
            Log.d("NotificationReceiver", "convert $editItemArray to Item")
            var editItem = Item(
                editItemArray[0].toInt(),
                editItemArray[1],
                Functions().getTimeOfArray(editItemArray),
                null,
                editItemArray[7].toBoolean(),
                editItemArray[8].toBoolean(),
                editItemArray[9]
            )
            Log.d("NotificationReceiver", "got Item " + editItem.toString())

            /*try { PendingIntent.getBroadcast(context, editItem.Index, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel()}finally {
                Log.d("PendingIntent", "already canceled")
            }*/
            val notificationUtils = NotificationUtils()
            val notification = notificationUtils.getNotificationBuilder(editItemArray).build()
            notificationUtils.getManager().notify(editItem.Index, notification)

            pendResult.finish()
        }
    }
    open class NotificationSnoozeReceiver: BroadcastReceiver(){//TODO not showing ass button
        override fun onReceive(context: Context, intent: Intent) {
        val pendResult = this.goAsync()
            Log.i("NotificationSnoozeReceiver", "trigged")
            pendResult.finish()

        /*
            mainPrefs = context.getSharedPreferences("mainPrefs", MODE_PRIVATE)
            suppPrefs = context.getSharedPreferences("suppPrefs", MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("ItemArray")!!
            Log.d("NotificationSnoozeReceiver", "convert $editItemArray to Item")
            var editItem = Item(
                editItemArray[0].toInt(),
                editItemArray[1],
                Functions().getTimeOfArray(editItemArray),
                null,
                editItemArray[7].toBoolean(),
                editItemArray[8].toBoolean(),
                editItemArray[9]
            )
            Log.d("NotificationSnoozeReceiver", "got Item " + editItem.toString())
            editItem.Date = LocalDateTime.now().plusMinutes(10)
            Functions().saveItem(editItem)

            val notificationUtils = NotificationUtils(context)
            if(editItem.Date !=null && editItem.Date!!.isAfter(LocalDateTime.now())) {
                NotificationUtils(mContext).makeNotification(editItem)
                Log.d("Notification", "Item ${editItem.Index} has Notification at " + editItem.Date!!.format(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")))
            }
            Log.i("AlarmMangaer", "notify Item ${editItem.Index} in " + editItem.Date!!.format(
                DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")))
        */
        }
    }
    open class NotificationDismissReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("NotificationDismissReceiver", "triggered")
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        suppActionBar.customView.b_settings.visibility = View.VISIBLE
        suppActionBar.customView.b_back.visibility = View.GONE
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.b_back -> {
                NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_fragment_settings_to_ItemList)
                suppActionBar.customView.b_settings.visibility = View.VISIBLE
                suppActionBar.customView.b_back.visibility = View.GONE
                return true
            }
        }
        return super.onContextItemSelected(item)
    }*/
}