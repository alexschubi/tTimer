package com.example.ttimer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class MainActivity : AppCompatActivity()
{


    //VARIABLES
   // val intent = Intent(this, SecondActivity::class.java)
    var delmode: Boolean = false
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""
    private val exList = ArrayList<Item>()
    private val adapter = RVadapter(exList)
    private var index: Int = 0
    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//-------------ADDING
        b_add.setOnClickListener() {
            Toast.makeText(this, "b_add clicked", Toast.LENGTH_SHORT).show()
            //setContentView(R.layout.activity_add)
            R.layout.activity_add.bringToFront()
        }
        //DELMODE
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



//CREATE LISTE TODO
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }


    //-------------------ADDITEM
    //TODO fragment host erstellen
    fun addItem(view: View) {
        setContentView(R.layout.activity_main)

        addText = tb_add_text.text.toString()
        addDate = calendarView.date.toString()
        addTime = tb_add_time.text.toString()
        index++

        val newItem = Item(addText, addDate, addTime)
        exList.add(index, newItem)
        adapter.notifyItemInserted(index)

        Toast.makeText(this, "b_add_final clicked", Toast.LENGTH_SHORT).show()
    }
    fun deleteItem (view: View){

    }
//TEST
    private fun generateDummyList(size: Int): ArrayList<Item> {
        val list = ArrayList<Item>()
        for (i in 0 until size) {
            val String = when (i % 3) {
                0 -> "blblblblbl"
                1 -> "tototototootot"
                else -> "rtrtrtrtrttr"
            }
            val item = Item(String, "Item $i", "Line 2")
            list += item
        }
        return list
    }
}