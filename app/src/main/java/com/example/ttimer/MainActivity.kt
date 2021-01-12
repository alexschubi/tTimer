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
                tv_test_out.text = ""
                tv_test_out2.text = ""
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

    public fun addItem(view: View) {
        //setContentView(R.layout.activity_main)
        var tinyDB: TinyDB = TinyDB(applicationContext)
       // var index = 0//global / save in prefs TODO
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
            putTime(timePicker.hour),
            putTime(timePicker.minute)
        )
        arrayListSave.add(addItem)//unused?

        //Save in tinyDB
        // https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo
        //converted to Kotlin and works somehow
        tinyDB.putListString("Item $index", addItemString)
        tinyDB.putInt("Length", index)

        //TODO clearing needed
        //get from tinyDB --TEST
        //getFromTinyDB()
        var length = tinyDB.getInt("Length")
        var testStringSave: String= ""
        var gettestSting = ""
        while (length > 0){
            var getItem = tinyDB.getListString("Item $length")
            //Test2
            testStringSave += getItem.toString() + "\n"
            tv_test_out2.text = testStringSave
            //Test1
            gettestSting += getItem[0] + ". [" + getItem[1] + "] \n " + getItem[2] + "." + getItem[3] + "." + getItem[4] + "  " + getItem[5] + ":" + getItem[6] + "\n\n"
            tv_test_out.text = gettestSting

            length += -1
        }

        //CLOSE add
        hideKeyboard()
        this.layer1.visibility = View.VISIBLE
        this.layer2.visibility = View.INVISIBLE
        Toast.makeText(this, "b_add_final clicked", Toast.LENGTH_SHORT).show()
    }

    private fun putTime(time: Int): String {
        var stringMinute = ""
        if (time<10){
            stringMinute = "0" + time.toString()
        }else{
            stringMinute = time.toString()
        }
        return stringMinute
    }

    /*fun getFromTinyDB (){
        var length = tinyDB.getInt("Length")
        var testStringSave: String= ""
        var gettestSting = ""
        while (length > 0){
            var getItem = tinyDB.getListString("Item $length")

            //Test2
            testStringSave += getItem.toString() + "\n"
            tv_test_out2.text = testStringSave

            //Test1
            gettestSting += getItem[1] + ". [" + getItem[2] + "] \n " + getItem[3] + "." + getItem[4] + "." + getItem[5] + "  " + getItem[6] + ":" + getItem[7] + "\n\n"
            tv_test_out.text = gettestSting

            length += -1
        }
    }*/
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