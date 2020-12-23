package com.example.ttimer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{


    //VARIABLES
    var delmode: Boolean = false
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""
    var test: String = ""
    //val array = Array(row) { IntArray(column) }
    var arrayitem = arrayOf(
        arrayOf("TextTest1", "DateTest1", "TimeTest1"))





    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTONS-link
        b_add.setOnClickListener(){clickAdd()}
        b_del.setOnClickListener(){clickDelete()}

        //TestTextViewOutput
        for (array in arrayitem) {
            for (value in array) {

                test += value
            }
            test += "- \n"
        }
        textView.text = test
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
            test()
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
        arrayitem += (arrayOf(addText, addDate, addTime))
        val arrayItemString = arrayitem.toString()


        setContentView(R.layout.activity_main)
        tv_date_item_1.text = addDate


        ///////RELOAD
        this.recreate()

        Toast.makeText(this, "b_add_final clicked / Date$addDate", Toast.LENGTH_LONG).show()
    }
    fun test() {

        arrayitem[0][1] = "99"
        arrayitem += (arrayOf("51","52","53","52"))
        for (array in arrayitem) {
            for (value in array) {

                    test += value
            }
            test += "- \n"
        }
        textView.text = test

    }
}