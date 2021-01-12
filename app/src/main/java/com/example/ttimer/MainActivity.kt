package com.example.ttimer

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity()
{
    //VARIABLES
   // val intent = Intent(this, SecondActivity::class.java)
    var delmode: Boolean = false
    var addText: String = ""
    var addDate: String = ""
    var addTime: String = ""
    private val exList = ArrayList<ItemTest>()
    private val adapter = RVadapter(exList)
    private var index: Int = 0
    var testText: String = ""
    //SAVINGS
    val arrayListSave = ArrayList<Item>()

    //START
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.layer2.visibility = View.INVISIBLE
        this.layer1.visibility = View.VISIBLE
        timePicker.setIs24HourView(true)

//-------------ADDING
        b_add.setOnClickListener() {
            Toast.makeText(this, "b_add clicked", Toast.LENGTH_SHORT).show()
            this.layer1.visibility = View.INVISIBLE
            this.layer2.visibility = View.VISIBLE

            //setContentView(R.layout.activity_add)
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

// TODO recycler View
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }
    //TODO SAVE IN TinyDB


    //-------------------ADDITEM
    //TODO fragment host erstellen
    fun openAdd(view: View){
        Toast.makeText(this, "Button-ADD clicked", Toast.LENGTH_SHORT)
    }

    fun addItem(view: View) {
        //setContentView(R.layout.activity_main)
        var index = 1//not superglobal the same / save in prefs TODO
        addText = tb_add_text.text.toString()
        //addDate = calendarView.date.toString()
        addDate = datePicker.dayOfMonth.toString() + "." + (datePicker.month+1) + "." + datePicker.year
        addTime = timePicker.hour.toString() + ":" + timePicker.minute.toString()
        index += 1
        val addItem: Item = Item(
            index,
            addText,
            datePicker.dayOfMonth,
            (datePicker.month + 1),
            datePicker.year,
            timePicker.hour,
            timePicker.minute
        )
        val addItemString = arrayListOf<String>(
            index.toString(),
            addText.toString(),
            datePicker.dayOfMonth.toString(),
            (datePicker.month + 1).toString(),
            datePicker.year.toString(),
            timePicker.hour.toString(),
            timePicker.minute.toString()
        )
        arrayListSave.add(addItem)//maybe delete?


        //TEST-output
        var addindex: String = index.toString()
        testText += "$addindex $addText $addDate $addTime \n"
        tv_test_out.text = testText

        //Save in tinyDB
        // https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo
        //converted to Kotlin and works somehow
        var tinyDB: TinyDB = TinyDB(applicationContext)
        tinyDB.putListString("Item $index", addItemString)
        tinyDB.putInt("Length", index)

        //TODO clearing needed
        //get from tinyDB
        var length = tinyDB.getInt("Length")
        var testStringSave: String= ""
        while (length > 0){
            var getItem = tinyDB.getListString("Item $length")
            testStringSave += getItem.toString() + "\n"
            tv_test_out2.text = testStringSave
            length += -1
        }

        //CLOSE add
        hideKeyboard()
        this.layer1.visibility = View.VISIBLE
        this.layer2.visibility = View.INVISIBLE
        Toast.makeText(this, "b_add_final clicked", Toast.LENGTH_SHORT).show()
    }
    fun deleteItem(view: View){

    }

    //HIDE KEYBOARD
    fun MainActivity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
 //END
}