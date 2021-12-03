package xyz.alexschubi.ttimer

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import xyz.alexschubi.ttimer.data.ItemsDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
lateinit var localDB: ItemsDatabase

class MainActivity : AppCompatActivity()
{
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE dd.MM.yyyy HH:mm")
    //TODO undo button after delete
    //TODO Bug when cxhanging theme sortmode is shown in settings
    //TODO bug when adding a new item sometimes not adding for real

    override fun onCreate(savedInstanceState: Bundle?) {

        mContext = this
        mainActivity = this
        mapplication = this.application
        localDB = ItemsDatabase.getDatabase(this)!!
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
        //Functions().getDB()
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
            mContext = context
            Log.d("NotificationReceiver", "triggered")
            mainPrefs = context.getSharedPreferences("mainPrefs", MODE_PRIVATE)
            suppPrefs = context.getSharedPreferences("suppPrefs", MODE_PRIVATE)

            val editItemArray = intent.extras!!.getStringArrayList("ItemArray")!!
            Log.d("NotificationReceiver", "got Item " + editItemArray)
            var editItem = Functions().ItemFromArray(editItemArray)

            val notification = NotificationUtils().getNotificationBuilder(editItemArray).build()
            NotificationUtils().getManager().notify(editItem.Index, notification)
            pendResult.finish()
        }
    }
    open class NotificationSnoozeReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            mContext = context
            Log.i("NotificationSnoozeReceiver", "trigged")
            mainPrefs = context.getSharedPreferences("mainPrefs", MODE_PRIVATE)
            suppPrefs = context.getSharedPreferences("suppPrefs", MODE_PRIVATE)
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
            mainPrefs = context.getSharedPreferences("mainPrefs", MODE_PRIVATE)
            suppPrefs = context.getSharedPreferences("suppPrefs", MODE_PRIVATE)
            val editItemArray = intent.extras!!.getStringArrayList("currentItem")!!
            var editItem = Functions().ItemFromArray(editItemArray)
            editItem.Deleted = true
            Functions().saveItem(editItem)
            NotificationUtils().cancelNotification(editItem)
            pendResult.finish()
        }

    }

    override fun onBackPressed() { //TODO fix animation call wen back pressed
        with(supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
            if ((this as? ExitWithAnimation)?.isToBeExitedWithAnimation() == true) {
                if (this.posX == null || this.posY == null) {
                    super.onBackPressed()
                } else {
                    this.view?.exitCircularReveal(this.posX!!, this.posY!!) {
                        supportFragmentManager.popBackStack()
                        Log.d("Navigation", "Transformed to last Fragment with animation")
                        super.onBackPressed()
                    } ?: super.onBackPressed()
                }
            } else {
                super.onBackPressed()
            }
        }
        suppActionBar.customView.b_settings.visibility = View.VISIBLE
        suppActionBar.customView.b_back.visibility = View.GONE
    }
}