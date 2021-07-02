package com.example.ttimer

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class fragment_add_item: Fragment() {
    private val args: fragment_add_itemArgs by navArgs<fragment_add_itemArgs>()
    private var binding: View? = null
    private var editItem: Item = Item(-1,"", null, null,false, false)
    private var getItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_add_item, container, false)
        return binding.apply {
            getItem = args.itemArgument
            binding?.tb_add_text?.setText(editItem.Text)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_addTimeSpan.text = ""
        if (getItem!=null) {
            editItem = getItem!!
            b_add_final.text = "Save"
        }
        timer.start()

        if (editItem.Date == null) {
            cl_date_time.visibility = View.GONE
        } else {
            b_add_time.text = "Edit Notification"
            tv_addDateTime.text = editItem.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
            if(editItem.Date!! > LocalDateTime.now()) { tv_addTimeSpan.text = editItem.Span
            }
            cl_date_time.visibility = View.VISIBLE
        }
        view.b_add_final.setOnClickListener { addItem() }
        view.b_add_time.setOnClickListener { addDateTime() }
    }

    private fun addItem() {
        var index: Int
        if (editItem.Index == -1){
            index = suppPrefs.getInt("ItemAmount", 0)
            Log.d("Preferences", suppPrefs.getInt("ItemAmount", 0).toString() + "Items registered")
            index++
        } else {
            index = editItem.Index
        }
        Log.d("AddItem","editing Item $index")
        val addItemString: ArrayList<String>
        if(editItem.Date == null) {
            addItemString = arrayListOf<String>(
                index.toString(),//Index
                activity?.tb_add_text?.text.toString(),//Text
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
                editItem.Date!!.dayOfMonth.toString(),
                editItem.Date!!.monthValue.toString(),
                editItem.Date!!.year.toString(),
                editItem.Date!!.hour.toString(),
                editItem.Date!!.minute.toString(),
                false.toString(),
                false.toString()
            )
        }
        Functions().putListString("Item $index", addItemString)
        if(editItem.Index == -1) {suppPrefs.edit().putInt ("ItemAmount",suppPrefs.getInt("ItemAmount", 0) + 1 ).apply()}
        Log.d("Preferences", "added Item: " + addItemString.toString())
        if(editItem.Date !=null && editItem.Date!!.isAfter(LocalDateTime.now())) {
            makeNotification(addItemString)
            Log.d("Notification", "Item $index has Notification at " + editItem.Date!!.format(
                DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")))
        } else {
            Log.d("Notification", "No Notification wanted or in Past")
        }
        //CLOSE addView
        Functions().getDB()
        this.view?.let { context?.hideKeyboard(it) }
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

    private fun addDateTime() {
        var actualDateTime = LocalDateTime.now()
        if (editItem.Date != null) {actualDateTime = editItem.Date}
        var newItemDate: LocalDateTime
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
                newItemDate = LocalDateTime.of(tYear, tMonth, tDay, tHour, tMinute)
                b_add_time.text = "Edit Notification"
                tv_addDateTime.text = newItemDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
                tv_addTimeSpan.text = Functions().getSpanString(newItemDate)
                cl_date_time.visibility = View.VISIBLE

                editItem.Span = Functions().getSpanString(newItemDate)
                editItem.Date = newItemDate
                Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))} set")
                //exit here
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
    }
    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            if (editItem.Date != null ) {tv_addTimeSpan.text = Functions().getSpanString(editItem.Date!!)}
        }
        override fun onFinish() {
            Toast.makeText(mContext, "AFK?", Toast.LENGTH_SHORT).show()
            exitProcess(-1)
        }
    }

    private fun MainActivity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }
}