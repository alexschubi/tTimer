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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_add_item3.*
import kotlinx.android.synthetic.main.fragment_add_item3.view.*
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.itemlist.fragment_item_list
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class AddItemFragment3() : Fragment(), ExitWithAnimation {

    //newInstance Variables
    private var getSItem: sItem? = null
    private var mfragmentItemList: fragment_item_list? = null
    override var posX: Int? = null
    override var posY: Int? = null
    private var startPosX: Int = 0
    private var startPosY: Int = 0
    override fun isToBeExitedWithAnimation(): Boolean = true
    private var exitWithSave = false
    //local Variables
    private var isNewItem = true
    private var newItem = sItem(-1, "", null, null, "purple", false, false)
    private var sDateTime = ZonedDateTime.now()

    lateinit var timePickerDialog: MaterialTimePicker
    lateinit var datePickerDialog: MaterialDatePicker<Long>
    var currentItem: sItem = newItem
    var hasNotification = false


    companion object {
        @JvmStatic
        fun newInstance(
            startPos: IntArray? = null,
            exitPos: IntArray? = null,
            getItem: sItem? = null,
            fragmentItemList: fragment_item_list? = null): AddItemFragment3
        = AddItemFragment3().apply {
            if (exitPos != null && exitPos.size == 2) {
                posX = exitPos[0]
                posY = exitPos[1]
            }
            if (startPos != null && startPos.size == 2) {
                startPosX = startPos[0]
                startPosY = startPos[1]
            }
            if (getItem != null){
                currentItem = getItem
                isNewItem = false
            }
            if(fragmentItemList != null){
                mfragmentItemList = fragmentItemList
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add_item3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//rotation crash savedstate so disabled rotation in mainfest
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(startPosX, startPosY)
        if (mfragmentItemList != null){
            mfragmentItemList!!.view?.appbar?.setExpanded(true)
        }else {
            Log.e("AddItemFragment", "no fragmentItemList passed")
        }

        //i dont know why this is here, cuz it cant br acessed
        view.b_back.setOnClickListener {
            b_back.setOnClickListener(null)
            b_add_final.setOnClickListener(null)
            exitWithSave = false
            timer.cancel()
            this.view?.let { inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0) }
            this.view?.exitCircularReveal(posX!!, posY!!){
                parentFragmentManager.popBackStack()
            }
        }

        if (!isNewItem) {
            b_add_final.text = "Save"
        } else {
            Functions().saveSItemToDB(currentItem)
            currentItem = localDB.itemsDAO().getLast()
            Functions().deleteItem(currentItem.Index)
        }

        var tempDateTime = LocalDateTime.now()
        if (currentItem.TimeStamp == null) {
            tv_show_time.visibility = View.GONE
            table_datetime.visibility =  View.GONE
            b_del_time.visibility = View.GONE
        } else {
            tempDateTime = currentItem.date()!!.toLocalDateTime()
            refreshDateTime()
            b_del_time.visibility = View.VISIBLE
            tv_show_time.visibility = View.VISIBLE
            table_datetime.visibility =  View.VISIBLE
        }
        tb_add_text.setText(currentItem.Text)

        timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(tempDateTime.hour)
            .setMinute(tempDateTime.minute)
            .setTheme(R.style.tMaterialTimePicker)
            .build()
        datePickerDialog = MaterialDatePicker.Builder.datePicker()
            .setSelection(null )
            .setTheme(R.style.tMaterialDatePicker)
            .build()
        timePickerDialog.addOnPositiveButtonClickListener {
            sDateTime = sDateTime.withHour(timePickerDialog.hour)
            sDateTime = sDateTime.withMinute(timePickerDialog.minute)
            currentItem.TimeStamp = sDateTime.toMilli()
            Log.d("TimePickerDialog", sDateTime.format(DateTimeFormatter.ofPattern("EE dd.MM.yyyy")))
            refreshDateTime()
        }
        datePickerDialog.addOnPositiveButtonClickListener {
            val tempDate = it.toZonedDateTime()
            sDateTime = sDateTime.withDayOfMonth(tempDate.dayOfMonth)
            sDateTime = sDateTime.withMonth(tempDate.monthValue)
            sDateTime = sDateTime.withYear(tempDate.year)
            currentItem.TimeStamp = sDateTime.toMilli()
            Log.d("TimePickerDialog", sDateTime.format(DateTimeFormatter.ofPattern("EE dd.MM.yyyy")))
            refreshDateTime()
        }

        tablerow_date.setOnClickListener {
            //initDateTimePicker()
            datePickerDialog.show(parentFragmentManager, "DatePicker")
        }
        tablerow_time.setOnClickListener {
            //initDateTimePicker()
            timePickerDialog.show(parentFragmentManager, "TimePicker")
        }
        tablerow_span.setOnClickListener {
            displayDateTimePicker()
        }
        b_del_time.setOnClickListener { delDateTime() }
        b_add_notification.setOnClickListener {
            addDateTime(ZonedDateTime.now())
            refreshDateTime()
        }
        b_add_final.setOnClickListener {
            b_back.setOnClickListener(null)
            b_add_final.setOnClickListener(null)
            addItem()
        }
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
        when (currentItem.Color) {
            "blue"-> rg_color.check(rb_blue.id)
            "green" -> rg_color.check(rb_green.id)
            "yellow" -> rg_color.check(rb_yellow.id)
            "orange" -> rg_color.check(rb_orange.id)
            "red" -> rg_color.check(rb_red.id)
            "purple" -> rg_color.check(rb_purple.id)
        }

        timer.start()
        tb_add_text.isFocusableInTouchMode = true
        tb_add_text.requestFocus()
        inputMethodManager.showSoftInput(tb_add_text, 0)
    }
//END of OnCreate()
    private fun addItem() {
        val colorButton: RadioButton = view?.findViewById<RadioButton>(rg_color.checkedRadioButtonId)!!
        when (this.view?.findViewById<RadioButton>(colorButton.id)?.id) {
            rb_purple.id -> currentItem.Color = "purple"
            rb_red.id -> currentItem.Color = "red"
            rb_orange.id -> currentItem.Color = "orange"
            rb_yellow.id -> currentItem.Color = "yellow"
            rb_green.id -> currentItem.Color = "green"
            rb_blue.id -> currentItem.Color = "blue"
        }
        Log.d("radio Button", " color set to ${currentItem.Color}")
        currentItem.Text = tb_add_text.text.toString()
        // sItem.Text = sItem.Index.toString() + " - " + tb_add_text.text.toString()
        Functions().saveSItemToDB(currentItem)

        //cancel and make notification
        NotificationUtils(mapplication).cancelNotification(currentItem.toItem())
        if(currentItem.TimeStamp !=null && currentItem.date()!!.isAfter(ZonedDateTime.now())) {
            NotificationUtils(mapplication).makeNotification(currentItem.toItem())
            Log.d("Notification", "Item ${currentItem.Index} has Notification at " + currentItem.date()!!.format(
                dateFormatter))
        } else {
            Log.d("Notification", "No Notification wanted or in Past")
        }

        //CLOSE addView
        if (mfragmentItemList != null){
            if(isNewItem){
                mfragmentItemList!!.addItem(currentItem)
            } else {
                mfragmentItemList!!.editItem(getSItem!!, currentItem)
            }
        } else {
            Log.e("AddItemFragment", "no fragmentItemList passed")
        }
        b_add_final.isClickable = false


        exitWithSave = true
        timer.cancel()
        this.view?.let { inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0) }
        this.view?.exitCircularReveal(posX!!, posY!!){
            parentFragmentManager.popBackStackImmediate()
        }
    }


    private fun displayDateTimePicker() {
        var actualDateTime = LocalDateTime.now()
        if (currentItem.TimeStamp != null) {actualDateTime = currentItem.date()!!.toLocalDateTime()}
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
                table_datetime.visibility = View.VISIBLE
                timer.start()

                currentItem.Span = Functions().getSpanString(newItemDate.toLocalDateTime())
                currentItem.TimeStamp = newItemDate.toMilli()
                Log.d("addDateTime", "LocalDateTime ${currentItem.date()!!.format(dateFormatter)} set")
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
    }

    fun initDateTimePicker(){
        var tempDateTime = LocalDateTime.now()
        if (currentItem.TimeStamp != null) {
            tempDateTime = currentItem.date()!!.toLocalDateTime()
        }
        timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(tempDateTime.hour)
            .setMinute(tempDateTime.minute)
            .build()
        datePickerDialog = MaterialDatePicker.Builder.datePicker()
            .setSelection(null )
            .build()
    }

    private fun addDateTime(dateTime: ZonedDateTime) {
        tv_show_time.text = dateTime.format(dateFormatter) + " in " + Functions().getSpanString(dateTime.toLocalDateTime()!!)
        tv_show_time.visibility = View.VISIBLE
        b_del_time.visibility = View.VISIBLE
        table_datetime.visibility = View.VISIBLE
        b_add_notification.visibility = View.GONE
        Log.d("addItem-Date-Text",dateTime.format(dateFormatter) + " in " + Functions().getSpanString(dateTime!!.toLocalDateTime()!!))
        currentItem.Span = Functions().getSpanString(dateTime.toLocalDateTime())
        currentItem.TimeStamp = dateTime.toMilli()
        Log.d("addDateTime", "LocalDateTime ${dateTime.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))} set")
    }
    private fun delDateTime(){
        currentItem.TimeStamp = null
        currentItem.Span = null
        tv_show_time.visibility = View.GONE
        b_del_time.visibility = View.GONE
        table_datetime.visibility = View.GONE
        b_add_notification.visibility = View.VISIBLE
    }

    private fun refreshDateTime(){
        tv_show_date.text = currentItem.date()!!.format(DateTimeFormatter.ofPattern("EE dd.MM.yyyy"))
        tv_show_time.text = currentItem.date()!!.format(DateTimeFormatter.ofPattern("HH:mm"))
        tv_show_span.text = "in " + Functions().getSpanString(currentItem.date()!!.toLocalDateTime()!!)
    }

    private val timer = object: CountDownTimer((1 * 60 * 60 * 1000).toLong(), (1 * 10 * 1000).toLong()){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            if (currentItem.TimeStamp != null ) {
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
        if (exitWithSave){
            Functions().saveSItemToDB(currentItem)
        }
        Log.d("AddItemFragment", "exit Fragment")
    }

}
