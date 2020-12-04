package com.example.ttimer

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    //VARIABLES
    var delmode = false
    //START
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        b_add.setOnClickListener(){
            Toast.makeText(this,"b_add clicked", Toast.LENGTH_SHORT).show()
        }
        b_del.setOnClickListener(){
            Toast.makeText(this,"b_del clicked", Toast.LENGTH_SHORT).show()
            if (delmode)
            {
                b_del.background.setTint(getColor(R.color.color_items))
                this.delmode = false
                Toast.makeText(this,"delmode disabled",Toast.LENGTH_SHORT).show()
            }else
            {
                b_del.background.setTint(getColor(R.color.color_items_chosen))
                this.delmode = true
                Toast.makeText(this,"delmode enabled",Toast.LENGTH_SHORT).show()
            }
        }
        linearLayout_h_Item_1.setOnClickListener(){
            Toast.makeText(this, "Item $linearLayout_h_Item_1 clicked", Toast.LENGTH_SHORT).show()
        }
    }

}