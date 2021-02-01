package com.example.ttimer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.recycler_view.view.*
import java.time.format.DateTimeFormatter
import kotlin.coroutines.coroutineContext

class RVadapter(private val rVArrayList: ArrayList<Item>) : RecyclerView.Adapter<RVadapter.ViewHolder>() {
    //https://www.raywenderlich.com/1560485-android-recyclerview-tutorial-with-kotlin

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rVArrayList.asReversed()[position]

        holder.itemView.tv_item_index.text = currentItem.Index.toString()
        holder.itemView.tv_item_text.text = currentItem.Text
        holder.itemView.tv_item_span.text = currentItem.Span
        holder.itemView.tv_item_datetime.text = currentItem.Date.format(DateTimeFormatter.ofPattern("dd.MM.uu HH:mm"))
        holder.itemView.id = currentItem.Index


    }

    override fun getItemCount() = rVArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init { itemView.setOnClickListener(this) }
        //private val delmode: Boolean = true
        private var view: View = itemView
        private var item: Item? = null

        override fun onClick(itemView: View) {//TODO getDB after delteing
            val prefIndex = itemView.id
            if(delmode) {
                mainPrefs.edit().remove("Item $prefIndex").apply()
                Toast.makeText( itemView.context, "Item $prefIndex deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText( itemView.context, "Item Pref $prefIndex clicked", Toast.LENGTH_SHORT).show()
                //MainActivity().editItem(index)
                /*MainActivity().layer2.visibility = View.VISIBLE
                itemView.layer1.visibility = View.INVISIBLE
                addmode = true
                itemView.tb_add_text.setText(getArrayList[index].Text)

                itemView.datePicker.updateDate(getArrayList[index].Date.year, getArrayList[index].Date.monthValue, getArrayList[index].Date.dayOfMonth)
                itemView.timePicker.hour = getArrayList[index].Date.hour
                itemView.timePicker.minute = getArrayList[index].Date.minute*/
            }
        }

        companion object {
            //5????????????????????
            private val ITEM_KEY = "ITEM"
        }
    }
}