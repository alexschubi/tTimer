package com.alexschubi.ttimer

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
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import kotlinx.android.synthetic.main.recycler_view.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class fragment_add_item: Fragment() {
    private val args: fragment_add_itemArgs by navArgs<fragment_add_itemArgs>()
    private var binding: View? = null
    private var editItem: Item = Item(-1,"", null, null,false, false, "")
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
        view.b_add_time.setOnClickListener { addCurrentDateTime() }
        cl_date_time.setOnClickListener { addDateTime() }
        view.b_del_time.setOnClickListener { delDateTime() }
        view.b_add_final.setOnClickListener { addItem() }

        tb_add_text.setText(editItem.Text)
        when (editItem.Color) {
            "blue"-> rg_color.check(rb_blue.id)
            "green" -> rg_color.check(rb_green.id)
            "yellow" -> rg_color.check(rb_yellow.id)
            "orange" -> rg_color.check(rb_orange.id)
            "red" -> rg_color.check(rb_red.id)
            "purple" -> rg_color.check(rb_purple.id)
        }

        if (editItem.Date == null) {
            b_add_time.visibility = View.VISIBLE
            cl_date_time.visibility = View.GONE
            tl_plusTime.visibility = View.GONE
            b_del_time.visibility = View.GONE
        } else {
            refreshDateTime()
            b_add_time.visibility = View.GONE
            b_del_time.visibility = View.VISIBLE
            cl_date_time.visibility = View.VISIBLE
            tl_plusTime.visibility = View.VISIBLE
        }
        //Minutes
        view.b_minus15minute.setOnClickListener {
            Log.d("DateTime","+7 Days")
            editItem.Date = editItem.Date!!.minusMinutes(15)
            refreshDateTime()
        }
        view.b_minus5minute.setOnClickListener {
            Log.d("DateTime","+7 Days")
            editItem.Date = editItem.Date!!.minusMinutes(5)
            refreshDateTime()
        }
        view.b_plus15minute.setOnClickListener {
            Log.d("DateTime","+15 Minutes")
            editItem.Date = editItem.Date!!.plusMinutes(15)
            refreshDateTime()
        }
        view.b_plus5minute.setOnClickListener {
            Log.d("DateTime","+5 Minutes")
            editItem.Date = editItem.Date!!.plusMinutes(5)
            refreshDateTime()
        }
        //Hours
        view.b_minus12hour.setOnClickListener {
            Log.d("DateTime","-12 Hours")
            editItem.Date = editItem.Date!!.minusHours(12)
            refreshDateTime()
        }
        view.b_minus1hour.setOnClickListener {
            Log.d("DateTime","-1 Hour")
            editItem.Date = editItem.Date!!.minusHours(1)
            refreshDateTime()
        }
        view.b_plus12hour.setOnClickListener {
            Log.d("DateTime","+12 Hour")
            editItem.Date = editItem.Date!!.plusHours(12)
            refreshDateTime()
        }
        view.b_plus1hour.setOnClickListener {
            Log.d("DateTime","+1 Hour")
            editItem.Date = editItem.Date!!.plusHours(1)
            refreshDateTime()
        }
        //Days
        view.b_minus7day.setOnClickListener {
            Log.d("DateTime","-7 Days")
            editItem.Date = editItem.Date!!.minusDays(7)
            refreshDateTime()
        }
        view.b_minus1day.setOnClickListener {
            Log.d("DateTime","-1 Day")
            editItem.Date = editItem.Date!!.minusDays(1)
            refreshDateTime()
        }
        view.b_plus7day.setOnClickListener {
            Log.d("DateTime","+7 Days")
            editItem.Date = editItem.Date!!.plusDays(7)
            refreshDateTime()
        }
        view.b_plus1day.setOnClickListener {
            Log.d("DateTime","+1 Day")
            editItem.Date = editItem.Date!!.plusDays(1)
            refreshDateTime()
        }

        suppActionBar.customView.b_settings.visibility = View.GONE
        suppActionBar.customView.b_back.visibility = View.VISIBLE
        suppActionBar.customView.b_back.setOnClickListener() {
            NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_AddItem_to_ItemList)
            suppActionBar.customView.b_settings.visibility = View.VISIBLE
            suppActionBar.customView.b_back.visibility = View.GONE
        }

        tb_add_text.isFocusableInTouchMode = true
        tb_add_text.requestFocus()
        inputMethodManager.showSoftInput(tb_add_text, 0)
    }

    //TODO colors for Items
    private fun addItem() {
        var index: Int
        var color = ""
        var colorButton: RadioButton = view?.findViewById<RadioButton>(rg_color.checkedRadioButtonId)!!
        when (this.view?.findViewById<RadioButton>(colorButton.id)?.id) {
            rb_purple.id -> color = "purple"
            rb_red.id -> color = "red"
            rb_orange.id -> color = "orange"
            rb_yellow.id -> color = "yellow"
            rb_green.id -> color = "green"
            rb_blue.id -> color = "blue"
        }
        Log.d("radio Button", " color set to $color")
        Log.d("radioButton", colorButton.id.toString())
        Log.i("addItem", "Color = " + view?.findViewById<RadioButton>(rg_color.checkedRadioButtonId)?.buttonTintList?.defaultColor.toString())

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
                false.toString(), //Deleted
                color
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
                false.toString(),
                color
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
        timer.cancel()
        this.view?.let { inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0) }
        NavHostFragment.findNavController(this).navigate(R.id.action_AddItem_to_ItemList)
        suppActionBar.customView.b_settings.visibility = View.VISIBLE
        suppActionBar.customView.b_back.visibility = View.GONE
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
        //TODO use material timepicker datepicker
        //TODO set colors
        val timePickerDialog = TimePickerDialog(this.context,
            R.style.Theme_AppCompat_Light_Dialog,
            { view, hourOfDay, minute ->
                Log.d("TimePicker", "got Time $hourOfDay:$minute")
                tMinute = minute
                tHour = hourOfDay
                newItemDate = LocalDateTime.of(tYear, tMonth, tDay, tHour, tMinute)
                tv_addDateTime.text = newItemDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
                tv_addTimeSpan.text = Functions().getSpanString(newItemDate)
                b_add_time.visibility = View.GONE
                b_del_time.visibility = View.VISIBLE
                cl_date_time.visibility = View.VISIBLE
                tl_plusTime.visibility = View.VISIBLE

                editItem.Span = Functions().getSpanString(newItemDate)
                editItem.Date = newItemDate
                Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))} set")
                //exit here
            },
            tHour,
            tMinute,
            true)
        val datePickerDialog = DatePickerDialog(this.requireContext(),
            R.style.Theme_AppCompat_Light_Dialog
            ,
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
    private fun addCurrentDateTime() {
        var actualDateTime = LocalDateTime.now()
        if (editItem.Date != null) {actualDateTime = editItem.Date}
        tv_addDateTime.text = actualDateTime.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
        tv_addTimeSpan.text = Functions().getSpanString(actualDateTime)
        b_add_time.visibility = View.GONE
        b_del_time.visibility = View.VISIBLE
        cl_date_time.visibility = View.VISIBLE
        tl_plusTime.visibility = View.VISIBLE
        editItem.Span = Functions().getSpanString(actualDateTime)
        editItem.Date = actualDateTime
        Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))} set")
    }
    private fun delDateTime(){
        editItem.Date = null
        editItem.Span = null
        b_add_time.visibility = View.VISIBLE
        cl_date_time.visibility = View.GONE
        tl_plusTime.visibility = View.GONE
        b_del_time.visibility = View.GONE
    }

    private fun refreshDateTime(){
        tv_addTimeSpan.text = Functions().getSpanString(editItem.Date!!)
        tv_addDateTime.text = editItem.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
    }

    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            if (editItem.Date != null ) {
                refreshDateTime()
            }
        }
        override fun onFinish() {
            Toast.makeText(mContext, "AFK?", Toast.LENGTH_SHORT).show()
            this.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        timer.start()
    }
}