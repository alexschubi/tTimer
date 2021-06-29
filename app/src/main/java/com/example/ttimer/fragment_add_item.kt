package com.example.ttimer

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.Inflater

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class fragment_add_item: Fragment() {
    // TODO: Rename and change types of parameters
    //private var param1: String? = null
    private val args: fragment_add_itemArgs by navArgs<fragment_add_itemArgs>()
    private var binding: View? = null
    var addDateTime: LocalDateTime? = null
    var editItem: Item? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
        Log.d("FragmentManger", "Fragment created")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_add_item, container, false)
        return binding.apply {
            val item = args.itemArgument
            editItem = item
            binding?.tb_add_text?.setText(item?.Text)
            addDateTime = editItem?.Date
           /* if (item?.Date != null) {
                addDateTime = editItem?.Date
                tv_addDate?.text = item.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
                tv_addTime?.text = item.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
                cl_date_time?.visibility = View.VISIBLE
               // binding?.tv_addDate?.text = item.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
               // binding?.tv_addTime?.text = item.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
               // binding?.cl_date_time?.visibility = View.VISIBLE
            }*/
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (addDateTime == null) {
            cl_date_time.visibility = View.GONE
        }
        else {
            b_add_time.text = "Edit Notification"
            tv_addDate.text = addDateTime!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"))
            tv_addTime.text = addDateTime!!.format(DateTimeFormatter.ofPattern("HH:mm"))
            cl_date_time.visibility = View.VISIBLE
        }
        view.b_add_final.setOnClickListener { addItem() }
        view.b_add_time.setOnClickListener { addDateTime() }
    }

    private fun addItem() {
        var index: Int
        if (editItem == null){
            index = suppPrefs.getInt("ItemAmount", 0)
            Log.d("Preferences", suppPrefs.getInt("ItemAmount", 0).toString() + "Items registered")
            index++
        } else {
            index = editItem!!.Index
        }
        //TODO fix Problem when submitting a new item with no text and notification
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
        if(editItem == null){suppPrefs.edit().putInt ("ItemAmount",suppPrefs.getInt("ItemAmount", 0) + 1 ).apply()}
        Log.d("Preferences", "added Item: " + addItemString.toString())
        if(addDateTime!=null && addDateTime!!.isAfter(LocalDateTime.now())) {
            makeNotification(addItemString)
            Log.d("Notification", "Item $index has Notification at " + addDateTime!!.format(
                DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")))
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
        //TODO multiple alarms don't stack
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
                b_add_time.text = "Edit Notification"
                tv_addDate.text = addDateTime!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"))
                tv_addTime.text = addDateTime!!.format(DateTimeFormatter.ofPattern("HH:mm"))
                cl_date_time.visibility = View.VISIBLE
            },
            tHour,
            tMinute,
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
            tYear,
            tMonth - 1,
            tDay)
        datePickerDialog.show()
        b_add_time.text = "Edit Notification"
    }

    private fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*companion object {
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
        fun newInstance(param1: Item) =
            fragment_add_item(item = null).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }*/
}