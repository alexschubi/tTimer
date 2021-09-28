package com.alexschubi.ttimer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class fragment_add_item: Fragment() { //TODO light Theme
    private val args: fragment_add_itemArgs by navArgs<fragment_add_itemArgs>()
    private var binding: View? = null
    private var editItem: Item = Item(-1,"", null, null,false, false, "")
    private var getItem: Item? = null
    private var originItem: Item? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_add_item, container, false)
        return binding.apply {
            getItem = args.itemArgument
            originItem = args.itemArgument
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
        cl_date_time.setOnClickListener { addSpecificDateTime() }
        b_del_time.setOnClickListener { delDateTime() }
        b_add_final.setOnClickListener { addItem() }

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
            cl_date_time.visibility = View.GONE
            ll_day.visibility =  View.GONE
            ll_hour.visibility = View.GONE
            ll_minute.visibility = View.GONE
            b_del_time.visibility = View.GONE
        } else {
            refreshDateTime()
            b_del_time.visibility = View.VISIBLE
            cl_date_time.visibility = View.VISIBLE
            ll_day.visibility =  View.VISIBLE
            ll_hour.visibility = View.VISIBLE
            ll_minute.visibility = View.VISIBLE
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
        //Quick Time
        view.b_add_qtime_now.setOnClickListener {
            Log.d("DateTime","Now")
            editItem.Date = LocalDateTime.now()
            addDateTime(editItem.Date!!)
        }
        view.b_add_qtime_1d.setOnClickListener {
            Log.d("DateTime","Tomorrow")
            editItem.Date = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0)
            addDateTime(editItem.Date!!)
        }
        view.b_add_qtime_weekend.setOnClickListener {
            Log.d("DateTime","Weekend")
            editItem.Date = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).withHour(14).withMinute(0)
            addDateTime(editItem.Date!!)
        }
        view.b_add_qtime_monthend.setOnClickListener {
            Log.d("DateTime","Monthend")
            var mtime = LocalDateTime.now()
            if(mtime.dayOfMonth > 27){
                mtime = mtime.plusDays(10)
            }
            editItem.Date = mtime.withDayOfMonth(27).withHour(8).withMinute(0)
            addDateTime(editItem.Date!!)
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

    private fun addItem() {
        var colorButton: RadioButton = view?.findViewById<RadioButton>(rg_color.checkedRadioButtonId)!!
        when (this.view?.findViewById<RadioButton>(colorButton.id)?.id) {
            rb_purple.id -> editItem.Color = "purple"
            rb_red.id -> editItem.Color = "red"
            rb_orange.id -> editItem.Color = "orange"
            rb_yellow.id -> editItem.Color = "yellow"
            rb_green.id -> editItem.Color = "green"
            rb_blue.id -> editItem.Color = "blue"
        }
        Log.d("radio Button", " color set to ${editItem.Color}")
        editItem.Text = tb_add_text.text.toString()
        Functions().saveItem(editItem)

        if (originItem != null) {
            NotificationUtils().cancelNotification(originItem!!.Index)
        }

        if(editItem.Date !=null && editItem.Date!!.isAfter(LocalDateTime.now())) {
            NotificationUtils().makeNotification(editItem)
            Log.d("Notification", "Item ${editItem.Index} has Notification at " + editItem.Date!!.format(
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

    private fun addSpecificDateTime() {
        var actualDateTime = LocalDateTime.now()
        if (editItem.Date != null) {actualDateTime = editItem.Date}
        var newItemDate: LocalDateTime
        var tMinute: Int = actualDateTime.minute
        var tHour: Int = actualDateTime.hour
        var tDay: Int = actualDateTime.dayOfMonth
        var tMonth: Int = actualDateTime.monthValue
        var tYear: Int = actualDateTime.year
        val timePickerDialog = TimePickerDialog(this.context,
            R.style.ThemeOverlay_MaterialComponents_MaterialCalendar,
            { view, hourOfDay, minute ->
                Log.d("TimePicker", "got Time $hourOfDay:$minute")
                tMinute = minute
                tHour = hourOfDay
                newItemDate = LocalDateTime.of(tYear, tMonth, tDay, tHour, tMinute)
                tv_addDateTime.text = newItemDate.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))
                tv_addTimeSpan.text = Functions().getSpanString(newItemDate)
                b_del_time.visibility = View.VISIBLE
                cl_date_time.visibility = View.VISIBLE
                ll_day.visibility =  View.VISIBLE
                ll_hour.visibility = View.VISIBLE
                ll_minute.visibility = View.VISIBLE

                editItem.Span = Functions().getSpanString(newItemDate)
                editItem.Date = newItemDate
                Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))} set")
                //exit here
            },
            tHour,
            tMinute,
            true)
        val datePickerDialog = DatePickerDialog(this.requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialCalendar
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
    private fun addDateTime(dateTime: LocalDateTime) {
        tv_addDateTime.text = dateTime.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))
        tv_addTimeSpan.text = Functions().getSpanString(dateTime)
        b_del_time.visibility = View.VISIBLE
        cl_date_time.visibility = View.VISIBLE
        ll_day.visibility =  View.VISIBLE
        ll_hour.visibility = View.VISIBLE
        ll_minute.visibility = View.VISIBLE
        editItem.Span = Functions().getSpanString(dateTime)
        editItem.Date = dateTime
        Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))} set")
    }
    private fun delDateTime(){
        editItem.Date = null
        editItem.Span = null
        cl_date_time.visibility = View.GONE
        ll_day.visibility =  View.GONE
        ll_hour.visibility = View.GONE
        ll_minute.visibility = View.GONE
        b_del_time.visibility = View.GONE
    }

    private fun refreshDateTime(){
        tv_addTimeSpan.text = Functions().getSpanString(editItem.Date!!)
        tv_addDateTime.text = editItem.Date!!.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))
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