package com.example.ttimer

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view.view.*
import java.time.format.DateTimeFormatter

class RVadapter(private val rVArrayList: ArrayList<Item>) : RecyclerView.Adapter<RVadapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rVArrayList[position]

        holder.textView_index.text = currentItem.Index.toString()
        holder.textView_title.text = currentItem.Text
        holder.textView_date.text = currentItem.Date.format(DateTimeFormatter.ofPattern("dd.MM.uu HH:mm"))


    }

    override fun getItemCount() = rVArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        //2
        private var view: View = itemView
        private var item: Item? = null

        //3
        init {
            itemView.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            //5
            private val KEY = "ITEM"
        }
        val textView_title: TextView = itemView.tv_item_text
        val textView_date: TextView = itemView.tv_item_datetime
        val textView_index: TextView = itemView.tv_Item_index
    }
}