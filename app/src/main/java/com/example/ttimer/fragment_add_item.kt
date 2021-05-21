package com.example.ttimer

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_add_item.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_add_item : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var addDateTime: LocalDateTime? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("FragmentManger", "Fragment created")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (addDateTime == null) { cl_date_time.visibility = View.GONE }
        view.b_add_final.setOnClickListener { addItem() }
        view.b_add_time.setOnClickListener { addDateTime() }
    }

    private fun addItem() {
        var index = suppPrefs.getInt("ItemAmount", 0)
        Log.d("Preferences", suppPrefs.getInt("ItemAmount", 0).toString() + "Items registered")
        index++
//TODO replace with dialogs
        val addItemString: ArrayList<String>
        if(addDateTime == null) {
            addItemString = arrayListOf<String>(
                index.toString(),
                activity?.tb_add_text?.text.toString(),
                "",//Day
                "",//Month
                "",//Year
                "",//Hour
                "",//Minute
                false.toString(), //Notified
                false.toString() //Deleted
            )
        } else {
            addItemString = arrayListOf<String>(
                index.toString(),
                activity?.tb_add_text?.text.toString(),
                addDateTime!!.dayOfMonth.toString(),
                addDateTime!!.monthValue.toString(),
                addDateTime!!.year.toString(),
                addDateTime!!.hour.toString(),
                addDateTime!!.minute.toString(),
                false.toString(),
                false.toString()
            )
        }

        Functions().putListString("Item $index", addItemString)
        suppPrefs.edit().putInt ("ItemAmount",suppPrefs.getInt("ItemAmount", 0) + 1 ).apply()
        Log.d("Preferences", "added Item: " + addItemString.toString())

        Log.d("Notification", "Item $index has Notification at " + addDateTime!!.format(
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")))
        if(addDateTime!!.isAfter(LocalDateTime.now())) {
            makeNotification(addItemString)
        } else {
            Log.d("Notification", "No Notification wanted or in Past")
        }
        //CLOSE addView
        Functions().getDB()
        this.view?.let { context?.hideKeyboard(it) }
        Log.d("FragmentManger", "create fragment_item_list...")
        NavHostFragment.findNavController(this).navigate(R.id.action_AddItem_to_ItemList)
    }

    private fun makeNotification(currentItemString: ArrayList<String>) {
        val zonedItemDateTime = Functions().getTime(currentItemString)!!.atZone(ZoneId.systemDefault())
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mContext, MainActivity.AlarmReceiver::class.java).putStringArrayListExtra("currentItemString", currentItemString)
        val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            zonedItemDateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
        Log.d("AlarmManager", "doAlarm Item: ${currentItemString[0]} in " +
                "${(zonedItemDateTime.toInstant().minusMillis
                    (ZonedDateTime.now().toInstant().toEpochMilli())).toEpochMilli()} milliSeconds")
    }
    /*open class AlarmReceiver : BroadcastReceiver() {
        //TODO multiple alarms dont stack
        //TODO Broadcast receiver have to work in background
        override fun onReceive(context: Context, intent: Intent) {
            val pendResult = this.goAsync()
            val currentItemString: ArrayList<String> = intent.getStringArrayListExtra("currentItemString") as ArrayList<String>
            Log.d("AlarmManager", "Item ${currentItemString?.get(0)} Timer Reached")
            val notificationUtils = NotificationUtils(context)
            val notification = notificationUtils.getNotificationBuilder(currentItemString).build()
            notificationUtils.getManager().notify((currentItemString[0].toInt()), notification)
            try { PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel()}finally {
                Log.d("PendingIntent", "already canceled")
            }
            pendResult.finish()
        }
    }*/
    private fun addDateTime() {
        val actualDateTime = LocalDateTime.now()
        var tMinute: Int = actualDateTime.minute
        var tHour: Int = actualDateTime.hour
        var tDay: Int = actualDateTime.dayOfMonth
        var tMonth: Int = actualDateTime.monthValue
        var tYear: Int = actualDateTime.year

        val timePickerDialog = TimePickerDialog(this.context,
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert,
            { view, hourOfDay, minute ->
                Log.d("TimePicker", "got Time $hourOfDay:$minute")
                tMinute = minute
                tHour = hourOfDay

                addDateTime = LocalDateTime.of(tYear, tMonth, tDay, tHour, tMinute)
                Log.d("addDateTime", "LocalDateTime $tHour:$tMinute $tDay.$tMonth.$tYear set")
                tv_addDate.text = tDay.toString() + "." + tMonth.toString() + "." + tYear.toString()
                tv_addTime.text = Functions().putTime(tHour) + ":" + Functions().putTime(tMinute)
                cl_date_time.visibility = View.VISIBLE
            },
            8,
            0,
            true)

        val datePickerDialog = DatePickerDialog(this.requireContext(),
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert,
            { view, year, month, dayOfMonth ->
                Log.d("DatePicker","got Date $dayOfMonth.$month.$year")
                tDay = dayOfMonth
                tMonth = month + 1
                tYear = year

                timePickerDialog.show()
            },
            2021, //TODO use actual Date and Time
            1,
            1)
        datePickerDialog.show()
    }

    private fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_add_item.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_add_item().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}