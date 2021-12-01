package xyz.alexschubi.ttimer.itemlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.alexschubi.ttimer.R.*
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import xyz.alexschubi.ttimer.data.sItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class RecyclerViewAdapter(private val rVArrayList: MutableList<sItem>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    var mItems = rVArrayList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout.recycler_view_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems)
    }

    override fun getItemCount() = mItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind (nItems: MutableList<sItem>){
            val currentItem = nItems[bindingAdapterPosition]
            if (currentItem.TimeStamp == null) {
                itemView.tv_item_span.visibility = View.GONE
                itemView.tv_item_datetime.visibility = View.GONE
            } else {
                itemView.tv_item_span.text = currentItem.Span
                itemView.tv_item_datetime.text = LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(currentItem.TimeStamp!!), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))
            }
            itemView.tv_item_index.text = currentItem.Span
            itemView.tv_item_text.text = currentItem.Text

            when (currentItem.Color) {
                "blue"-> itemView.setBackgroundResource(drawable.rounded_corner_blue)
                "green" -> itemView.setBackgroundResource(drawable.rounded_corner_green)
                "yellow" -> itemView.setBackgroundResource(drawable.rounded_corner_yellow)
                "orange" -> itemView.setBackgroundResource(drawable.rounded_corner_orange)
                "red" -> itemView.setBackgroundResource(drawable.rounded_corner_red)
                "purple" -> itemView.setBackgroundResource(drawable.rounded_corner_purple)
            }
            itemView.elevation = 30F
            itemView.translationZ = 30F
            itemView.id = currentItem.Index
        }
    }
    fun setItems(items: MutableList<sItem>) {
        this.mItems = items
    }
}

