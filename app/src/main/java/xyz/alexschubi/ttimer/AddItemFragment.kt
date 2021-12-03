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
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.itemlist.fragment_item_list
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class AddItemFragment(public val getItem: sItem?) : Fragment(), ExitWithAnimation {


    private var editItem: Item = Item(-1,"", null, null,false, false, "")

    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add_item2, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(exit: IntArray? = null) =
            AddItemFragment(null).apply {
                posX = 0
                posY = 0
                if (exit != null && exit.size ==2){
                    posX = exit[0]
                    posY = exit[1]
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal()

        tv_addTimeSpan.text = ""
        suppActionBar.customView.sp_sortMode.visibility = View.GONE
        if (getItem!=null) {
            editItem = getItem.toItem()
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
            this.view?.exitCircularReveal(this.posX!!, this.posY!!){}
            suppActionBar.customView.b_settings.visibility = View.VISIBLE
            suppActionBar.customView.b_back.visibility = View.GONE
        }//TODO only action bar at items-list-fragment

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
        //this.view?.exitCircularReveal(posX!!, posY!!){ parentFragmentManager.popBackStackImmediate("list", 0)}
        this.view?.exitCircularReveal(posX!!, posY!!){ parentFragmentManager.close { replace(R.id.nav_host_fragment, fragment_item_list()) }}

        //NavHostFragment.findNavController(this).navigate(R.id.action_AddItem_to_ItemList)
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

}
