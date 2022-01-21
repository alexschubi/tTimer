package xyz.alexschubi.ttimer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_add_item2.*
import kotlinx.android.synthetic.main.fragment_item_list.*
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.itemlist.fragment_item_list
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class AddItemFragment(val getItem: sItem?, val fragmentItemList: fragment_item_list) : Fragment(), ExitWithAnimation {

    //private var editItem: Item = Item(-1,"", null, null,false, false, "")
    private var sItem = sItem(-1, "", null, null, "purple", false, false)
    override var posX: Int? = null
    override var posY: Int? = null
    var startPosX: Int = 0
    var startPosY: Int = 0
    override fun isToBeExitedWithAnimation(): Boolean = true

    companion object {
        @JvmStatic
        fun newInstance(startPos: IntArray? = null, exitPos: IntArray? = null, getItem: sItem?, fragmentItemList: fragment_item_list): AddItemFragment = AddItemFragment(getItem, fragmentItemList).apply {
            if (exitPos != null && exitPos.size == 2) {
                posX = exitPos[0]
                posY = exitPos[1]
            }
            if (startPos != null && startPos.size == 2) {
                startPosX = startPos[0]
                startPosY = startPos[1]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add_item2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(startPosX, startPosY)
        if (getItem!=null) {
            sItem = getItem
            b_add_final.text = "Save"
        }
        timer.start()
        tv_show_time.setOnClickListener { addSpecificDateTime() }
        b_del_time.setOnClickListener { delDateTime() }
        b_add_final.setOnClickListener { addItem() }

        tb_add_text.setText(sItem.Text)
        rg_color.setOnCheckedChangeListener { radioGroup, i ->
            var strokeColor = 0
            when(i){
                rb_blue.id -> strokeColor = ContextCompat.getColor(mapplication, R.color.item_blue)
                rb_green.id ->  strokeColor= ContextCompat.getColor(mapplication, R.color.item_green)
                rb_red.id ->  strokeColor= ContextCompat.getColor(mapplication, R.color.item_red)
                rb_purple.id ->  strokeColor= ContextCompat.getColor(mapplication, R.color.item_purple)
                rb_yellow.id ->  strokeColor= ContextCompat.getColor(mapplication, R.color.item_yellow)
                rb_orange.id ->  strokeColor= ContextCompat.getColor(mapplication, R.color.item_orange)
            }
            text_Input_layout.apply {
                boxStrokeColor = strokeColor
                hintTextColor = ColorStateList.valueOf(strokeColor)
            }
        }
        rg_color.clearCheck()
        rg_color.check(rb_purple.id)
        when (sItem.Color) {
            "blue"-> rg_color.check(rb_blue.id)
            "green" -> rg_color.check(rb_green.id)
            "yellow" -> rg_color.check(rb_yellow.id)
            "orange" -> rg_color.check(rb_orange.id)
            "red" -> rg_color.check(rb_red.id)
            "purple" -> rg_color.check(rb_purple.id)
        }

        if (sItem.TimeStamp == null) {
            tv_show_time.visibility = View.GONE
            cl_modify_time.visibility =  View.GONE
            b_del_time.visibility = View.GONE
        } else {
            refreshDateTime()
            b_del_time.visibility = View.VISIBLE
            tv_show_time.visibility = View.VISIBLE
            cl_modify_time.visibility =  View.VISIBLE
        }
        //Minutes
        b_minus5minute.setOnClickListener {
            Log.d("DateTime","+7 Days")
            val newDateTime = sItem.date()
            if (newDateTime != null) {
                sItem.TimeStamp = newDateTime.minusMinutes(5).toMilli()
            }
            refreshDateTime()
        }
        b_plus5minute.setOnClickListener {
            Log.d("DateTime","+5 Minutes")
            val newDateTime = sItem.date()
            if (newDateTime != null) {
                sItem.TimeStamp = newDateTime.plusMinutes(5).toMilli()
            }
            refreshDateTime()
        }
        //Hours
        b_minus1hour.setOnClickListener {
            Log.d("DateTime","-1 Hour")
            val newDateTime = sItem.date()
            if (newDateTime != null) {
                sItem.TimeStamp = newDateTime.minusHours(1).toMilli()
            }
            refreshDateTime()
        }
        b_plus1hour.setOnClickListener {
            Log.d("DateTime","+1 Hour")
            val newDateTime = sItem.date()
            if (newDateTime != null) {
                sItem.TimeStamp = newDateTime.plusHours(1).toMilli()
            }
            refreshDateTime()
        }
        //Days
        b_minus1day.setOnClickListener {
            Log.d("DateTime","-1 Day")
            val newDateTime = sItem.date()
            if (newDateTime != null) {
                sItem.TimeStamp = newDateTime.minusDays(1).toMilli()
            }
            refreshDateTime()
        }
        b_plus1day.setOnClickListener {
            Log.d("DateTime","+1 Day")
            val newDateTime = sItem.date()
            if (newDateTime != null) {
                sItem.TimeStamp = newDateTime.plusDays(1).toMilli()
            }
            refreshDateTime()
        }
        //Quick Time
        b_add_notification.setOnClickListener {
            Log.d("DateTime","Now")
            val newDateTIme = ZonedDateTime.now()
            sItem.TimeStamp = newDateTIme.toMilli()
            addDateTime(newDateTIme.toLocalDateTime())
        }
        b_add_qtime_1d.setOnClickListener {
            Log.d("DateTime","Tomorrow")
            val newDateTime = ZonedDateTime.now().plusDays(1).withHour(8).withMinute(0)
            sItem.TimeStamp = newDateTime.toMilli()
            addDateTime(newDateTime.toLocalDateTime())
        }
        b_add_qtime_weekend.setOnClickListener {
            Log.d("DateTime","Weekend")
            val newDateTime = ZonedDateTime.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).withHour(17).withMinute(0)
            sItem.TimeStamp = newDateTime.toMilli()
            addDateTime(newDateTime.toLocalDateTime())
        }

        tb_add_text.isFocusableInTouchMode = true
        tb_add_text.requestFocus()
        inputMethodManager.showSoftInput(tb_add_text, 0)
    }

    private fun addItem() {
        val colorButton: RadioButton = view?.findViewById<RadioButton>(rg_color.checkedRadioButtonId)!!
        when (this.view?.findViewById<RadioButton>(colorButton.id)?.id) {
            rb_purple.id -> sItem.Color = "purple"
            rb_red.id -> sItem.Color = "red"
            rb_orange.id -> sItem.Color = "orange"
            rb_yellow.id -> sItem.Color = "yellow"
            rb_green.id -> sItem.Color = "green"
            rb_blue.id -> sItem.Color = "blue"
        }
        Log.d("radio Button", " color set to ${sItem.Color}")
        sItem.Text = tb_add_text.text.toString()
        Functions().saveSItemToDB(sItem)

        if (getItem != null) {
            NotificationUtils(mapplication).cancelNotification(sItem.toItem())
        }

        if(sItem.TimeStamp !=null && sItem.date()!!.isAfter(ZonedDateTime.now())) {
            NotificationUtils(mapplication).makeNotification(sItem.toItem())
            Log.d("Notification", "Item ${sItem.Index} has Notification at " + sItem.date()!!.format(
                dateFormatter))
        } else {
            Log.d("Notification", "No Notification wanted or in Past")
        }
        //CLOSE addView
        //Functions().getDB()
        timer.cancel()
        this.view?.let { inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0) }
        this.view?.exitCircularReveal(posX!!, posY!!){
            fragmentItemList.displayItemsList?.add(sItem)
            fragmentItemList.recyclerViewItems2.adapter!!.notifyItemInserted()
            parentFragmentManager.popBackStack()
        }
    }

    private fun addSpecificDateTime() {
        var actualDateTime = LocalDateTime.now()
        if (sItem.TimeStamp != null) {actualDateTime = sItem.date()!!.toLocalDateTime()}
        var newItemDate: ZonedDateTime
        var tMinute: Int = actualDateTime.minute
        var tHour: Int = actualDateTime.hour
        var tDay: Int = actualDateTime.dayOfMonth
        var tMonth: Int = actualDateTime.monthValue
        var tYear: Int = actualDateTime.year
        val timePickerDialog = TimePickerDialog(this.context, //TODO use MaterialTimeOicker https://material.io/components/time-pickers/android#using-time-pickers
            R.style.ThemeOverlay_MaterialComponents_TimePicker,
            { view, hourOfDay, minute ->
                Log.d("TimePicker", "got Time $hourOfDay:$minute")
                tMinute = minute
                tHour = hourOfDay
                newItemDate = ZonedDateTime.of(tYear, tMonth, tDay, tHour, tMinute, 0, 0, ZoneId.systemDefault())
                tv_show_time.text = newItemDate.format(dateFormatter) + " in " + Functions().getSpanString(newItemDate.toLocalDateTime())
                tv_show_time.visibility = View.VISIBLE
                b_del_time.visibility = View.VISIBLE
                cl_modify_time.visibility = View.VISIBLE

                sItem.Span = Functions().getSpanString(newItemDate.toLocalDateTime())
                sItem.TimeStamp = newItemDate.toMilli()
                Log.d("addDateTime", "LocalDateTime ${sItem.date()!!.format(dateFormatter)} set")
                //exit here
            },
            tHour,
            tMinute,
            true)
        val datePickerDialog = DatePickerDialog(this.requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialCalendar,
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

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)


    }
    private fun addDateTime(dateTime: ZonedDateTime) {
        tv_show_time.text = dateTime.format(dateFormatter) + " in " + Functions().getSpanString(dateTime)
        tv_show_time.visibility = View.VISIBLE
        b_del_time.visibility = View.VISIBLE
        cl_modify_time.visibility = View.VISIBLE
        b_add_notification.visibility = View.GONE
        Log.d("addItem-Date-Text",dateTime.format(dateFormatter) + " in " + Functions().getSpanString(dateTime))
        sItem.Span = Functions().getSpanString(dateTime.toLocalDateTime())
        sItem.TimeStamp = dateTime.toMilli()
        Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))} set")
    }
    private fun delDateTime(){
        editItem.Date = null
        editItem.Span = null
        tv_show_time.visibility = View.GONE
        b_del_time.visibility = View.GONE
        cl_modify_time.visibility = View.GONE
        b_add_notification.visibility = View.VISIBLE
    }

    private fun refreshDateTime(){
        tv_show_time.text = editItem.Date!!.format(dateFormatter) + " in " + Functions().getSpanString(editItem.Date!!)
    }

    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            if (editItem.Date != null ) {
                refreshDateTime()
            }
        }
        override fun onFinish() {
            Toast.makeText(mapplication, "AFK?", Toast.LENGTH_SHORT).show()
            this.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

}
