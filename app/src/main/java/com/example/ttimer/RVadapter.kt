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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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
        rVArrayList.asReversed()
        val currentItem = rVArrayList.asReversed()[position]

        holder.itemView.tv_item_index.text = currentItem.Index.toString()
        holder.itemView.tv_item_text.text = currentItem.Text
        holder.itemView.tv_item_span.text = currentItem.Span
        holder.itemView.tv_item_datetime.text = currentItem.Date.format(DateTimeFormatter.ofPattern("dd.MM.uu HH:mm"))


    }

    override fun getItemCount() = rVArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView
        private var item: Item? = null

        init { itemView.setOnClickListener(this) }

        override fun onClick(itemView: View) {//TODO get delmode-Bool and MainPrefs from MainActivity
            MainActivity().clickItem(layoutPosition + 1, itemView)

        }

        companion object {
            //5????????????????????
            private val ITEM_KEY = "ITEM"
        }

    }
}