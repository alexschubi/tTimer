package com.example.ttimer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker

class MainActivity : AppCompatActivity()
{


    //VARIABLES
    var delmode: Boolean = false
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTONS-link
        b_add.setOnClickListener(){clickAdd()}
        b_del.setOnClickListener(){clickDelete()}
        /*b_add.setOnClickListener() {
            Toast.makeText(this, "b_add clicked", Toast.LENGTH_SHORT).show()
            setContentView(R.layout.activity_add)

            b_add_final.setOnClickListener() {
                addText = tb_add_text.text.toString()
                addDate = tb_add_date.text.toString()
                addTime = tb_add_time.text.toString()
                setContentView(R.layout.activity_main)
                onCreate()
                Toast.makeText(this, "b_add_final clicked", Toast.LENGTH_SHORT).show()
            }
        }
        b_del.setOnClickListener() {
            Toast.makeText(this, "b_del clicked", Toast.LENGTH_SHORT).show()
            if (delmode)
            {
                b_del.background.setTint(getColor(R.color.color_header))
                this.delmode = false
                Toast.makeText(this, "delmode disabled", Toast.LENGTH_SHORT).show()
            } else
            {
                b_del.background.setTint(getColor(R.color.color_items_chosen))
                this.delmode = true
                Toast.makeText(this, "delmode enabled", Toast.LENGTH_SHORT).show()
            }
        }
        linearLayout_h_Item_1.setOnClickListener() {
            Toast.makeText(this, "Item $linearLayout_h_Item_1 clicked", Toast.LENGTH_SHORT).show()
        }*/


    }

    fun clickAdd(){
        Toast.makeText(this, "b_add clicked", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_add)
        b_add_final.setOnClickListener(){clickAddSave()}
    }
    fun clickDelete(){
        if (delmode) {
            delmode = false
            b_del.background.setTint(getColor(R.color.color_header))
            Toast.makeText(this, "delmode false",Toast.LENGTH_SHORT).show()}
        else{
            delmode = true
            b_del.background.setTint(getColor(R.color.color_items_chosen))
            Toast.makeText(this, "delmode true",Toast.LENGTH_SHORT).show()}
        }
    fun clickAddSave(){
        addText = tb_add_text.text.toString()
        addDate = tb_add_date.text.toString()
        addTime = tb_add_time.text.toString()
        setContentView(R.layout.activity_main)
        Toast.makeText(this, "b_add_final clicked", Toast.LENGTH_SHORT).show()
    }
}