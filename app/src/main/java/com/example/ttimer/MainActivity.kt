package com.example.ttimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        b_add.setOnClickListener(){
            Toast.makeText(this,"b_add clicked", Toast.LENGTH_SHORT).show()
        }
        b_del.setOnClickListener(){
            Toast.makeText(this,"b_del clicked", Toast.LENGTH_SHORT).show()
        }
        linearLayout_h_Item_1.setOnClickListener(){
            Toast.makeText(this, "Item $linearLayout_h_Item_1 clicked", Toast.LENGTH_SHORT).show()
        }
    }

}