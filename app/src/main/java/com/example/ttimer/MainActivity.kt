package com.example.ttimer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.TimePicker

class MainActivity : AppCompatActivity()
{


    //VARIABLES
    var delmode: Boolean = false
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""
    var test: String = ""
    //val array = Array(row) { IntArray(column) }
    val ArrayData: Array

//    val sharedPref: SharedPreferences = getSharedPreferences("SavedData1", 0)



    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTONS-link
        b_add.setOnClickListener(){clickAdd()}
        b_del.setOnClickListener(){clickDelete()}
    }

    private fun clickAdd(){
        Toast.makeText(this, "b_add clicked", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_add)
        b_add_final.setOnClickListener(){clickAddSave()}
    }
    private fun clickDelete(){
        if (delmode) {
            delmode = false
            b_del.background.setTint(getColor(R.color.color_header))
            main()
            Toast.makeText(this, "delmode false",Toast.LENGTH_SHORT).show()}
        else{
            delmode = true
            b_del.background.setTint(getColor(R.color.color_items_chosen))
            Toast.makeText(this, "delmode true",Toast.LENGTH_SHORT).show()}
        }
    private fun clickAddSave(){
        addText = tb_add_text.text.toString()
        addDate = calendarView.date.toString()
        addTime = tb_add_time.text.toString()
        setContentView(R.layout.activity_main)
        tv_date_item_1.text = addDate
        this.recreate()

        Toast.makeText(this, "b_add_final clicked / Date$addDate", Toast.LENGTH_LONG).show()
    }
    fun main() {


        val cinema = arrayOf(
            arrayOf(11, 12, 13, 14),
            arrayOf(21,22,23,24),
            arrayOf(31,32,33,34),
            arrayOf(41,42,43,44)
        )
        cinema[1][0] = 1

        for (array in cinema) {
            for (value in array) {

                    test += value
            }
            test += "- \n"
        }
        textView.text = test

    }
}