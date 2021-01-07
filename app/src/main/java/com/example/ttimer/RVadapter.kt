package com.example.ttimer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view.view.*

class RVadapter(private val Liste: ArrayList<Item>) : RecyclerView.Adapter<RVadapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view,
        parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = Liste[position]

        holder.textView_title.text = currentItem.Text
        holder.textView_date.text = currentItem.Date.toString()
        holder.textView_time.text = currentItem.Time.toString()

    }

    override fun getItemCount() = Liste.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView_title: TextView = itemView.tv_title_item_1
        val textView_date: TextView = itemView.tv_date_item_1
        val textView_time: TextView = itemView.tv_time_item_1
    }
}