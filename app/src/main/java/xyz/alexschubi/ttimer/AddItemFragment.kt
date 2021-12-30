package xyz.alexschubi.ttimer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.add_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_add_item2.*
import xyz.alexschubi.ttimer.data.sItem
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class AddItemFragment(val getItem: sItem?) : Fragment(), ExitWithAnimation {

    private var editItem: Item = Item(-1,"", null, null,false, false, "")
    override var posX: Int? = null
    override var posY: Int? = null
    var startPosX: Int = 0
    var startPosY: Int = 0
    override fun isToBeExitedWithAnimation(): Boolean = true

    companion object {
        @JvmStatic
        fun newInstance(startPos: IntArray? = null, exitPos: IntArray? = null, getItem: sItem?): AddItemFragment = AddItemFragment(getItem).apply {
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
        suppActionBar.setCustomView(R.layout.add_toolbar)
        if (getItem!=null) {
            editItem = getItem.toItem()
            b_add_final.text = "Save"
        }
        timer.start()
        tv_show_time.setOnClickListener { addSpecificDateTime() }
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
            editItem.Date = editItem.Date!!.minusMinutes(5)
            refreshDateTime()
        }
        b_plus5minute.setOnClickListener {
            Log.d("DateTime","+5 Minutes")
            editItem.Date = editItem.Date!!.plusMinutes(5)
            refreshDateTime()
        }
        //Hours
        b_minus1hour.setOnClickListener {
            Log.d("DateTime","-1 Hour")
            editItem.Date = editItem.Date!!.minusHours(1)
            refreshDateTime()
        }
        b_plus1hour.setOnClickListener {
            Log.d("DateTime","+1 Hour")
            editItem.Date = editItem.Date!!.plusHours(1)
            refreshDateTime()
        }
        //Days
        b_minus1day.setOnClickListener {
            Log.d("DateTime","-1 Day")
            editItem.Date = editItem.Date!!.minusDays(1)
            refreshDateTime()
        }
        b_plus1day.setOnClickListener {
            Log.d("DateTime","+1 Day")
            editItem.Date = editItem.Date!!.plusDays(1)
            refreshDateTime()
        }
        //Quick Time
        b_add_notification.setOnClickListener {
            Log.d("DateTime","Now")
            editItem.Date = LocalDateTime.now()
            addDateTime(editItem.Date!!)
        }
        b_add_qtime_1d.setOnClickListener {
            Log.d("DateTime","Tomorrow")
            editItem.Date = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0)
            addDateTime(editItem.Date!!)
        }
        b_add_qtime_weekend.setOnClickListener {
            Log.d("DateTime","Weekend")
            editItem.Date = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).withHour(14).withMinute(0)
            addDateTime(editItem.Date!!)
        }

        suppActionBar.customView.b_back.setOnClickListener() {
            Functions().hideKeyboard(this.requireView())
            this.view?.exitCircularReveal(this.posX!!, this.posY!!){
                suppActionBar.setCustomView(R.layout.list_toolbar)
                parentFragmentManager.popBackStack()
            }
        }

        tb_add_text.isFocusableInTouchMode = true
        tb_add_text.requestFocus()
        inputMethodManager.showSoftInput(tb_add_text, 0)
    }

    private fun addItem() {
        val colorButton: RadioButton = view?.findViewById<RadioButton>(rg_color.checkedRadioButtonId)!!
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

        if (getItem != null) {
            NotificationUtils().cancelNotification(editItem)
        }

        if(editItem.Date !=null && editItem.Date!!.isAfter(LocalDateTime.now())) {
            NotificationUtils().makeNotification(editItem)
            Log.d("Notification", "Item ${editItem.Index} has Notification at " + editItem.Date!!.format(
                DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")))
        } else {
            Log.d("Notification", "No Notification wanted or in Past")
        }
        //CLOSE addView
        //Functions().getDB()
        timer.cancel()
        this.view?.let { inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0) }
        this.view?.exitCircularReveal(posX!!, posY!!){
            parentFragmentManager.popBackStack()
            suppActionBar.setCustomView(R.layout.list_toolbar)
            val fragment = parentFragmentManager.findFragmentById(R.id.container)
            if (fragment != null) {
                if (fragment.equals(R.id.ItemList)){

                }

            }
        }
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
        val timePickerDialog = TimePickerDialog(this.context, //TODO use MaterialTimeOicker https://material.io/components/time-pickers/android#using-time-pickers
            R.style.tTimePicker,
            { view, hourOfDay, minute ->
                Log.d("TimePicker", "got Time $hourOfDay:$minute")
                tMinute = minute
                tHour = hourOfDay
                newItemDate = LocalDateTime.of(tYear, tMonth, tDay, tHour, tMinute)
                tv_show_time.text = newItemDate.format(dateFormatter) + " in " + Functions().getSpanString(newItemDate)
                tv_show_time.visibility = View.VISIBLE
                b_del_time.visibility = View.VISIBLE
                cl_modify_time.visibility = View.VISIBLE

                editItem.Span = Functions().getSpanString(newItemDate)
                editItem.Date = newItemDate
                Log.d("addDateTime", "LocalDateTime ${editItem.Date!!.format(dateFormatter)} set")
                //exit here
            },
            tHour,
            tMinute,
            true)
        val datePickerDialog = DatePickerDialog(this.requireContext(),
            R.style.tDatePicker
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
        tv_show_time.text = dateTime.format(dateFormatter) + " in " + Functions().getSpanString(dateTime)
        tv_show_time.visibility = View.VISIBLE
        b_del_time.visibility = View.VISIBLE
        cl_modify_time.visibility = View.VISIBLE
        b_add_notification.visibility = View.GONE
        Log.d("addItem-Date-Text",dateTime.format(dateFormatter) + " in " + Functions().getSpanString(dateTime))
        editItem.Span = Functions().getSpanString(dateTime)
        editItem.Date = dateTime
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
            Toast.makeText(mContext, "AFK?", Toast.LENGTH_SHORT).show()
            this.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

}
