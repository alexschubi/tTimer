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

class RVadapter(private val rVArrayList: ArrayList<Item>, val delmode: Boolean) : RecyclerView.Adapter<RVadapter.ViewHolder>() {
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
        //private val delmode: Boolean = true
        private var view: View = itemView
        private var item: Item? = null

        init { itemView.setOnClickListener(this) }

        override fun onClick(itemView: View) {//TODO get delmode-Bool and MainPrefs from MainActivity
            if(delmode) {
                mainPrefs.edit().remove("Item ${layoutPosition + 1}").apply()
                mainPrefs.edit().putInt("index", mainPrefs.getInt("index", 0) - 1 ).apply()
                Toast.makeText( itemView.context, "Item ${layoutPosition + 1} deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(itemView.context, "Item ${layoutPosition + 1} clicked", Toast.LENGTH_SHORT).show()
            }
        }

        companion object {
            //5????????????????????
            private val ITEM_KEY = "ITEM"
        }
    }
}