package xyz.alexschubi.ttimer.itemlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import kotlinx.android.synthetic.main.recycler_view_item_v2.view.*
import xyz.alexschubi.ttimer.R.*
import xyz.alexschubi.ttimer.R
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.mapplication
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class RecyclerViewAdapter(rVArrayList: MutableList<sItem>, val listener: (sItem) -> Unit) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    var mItems = rVArrayList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout.recycler_view_item_v2, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems, listener)
    }

    override fun getItemCount() = mItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind (nItems: MutableList<sItem>, listener: (sItem) -> Unit){
            val currentItem = nItems[bindingAdapterPosition]
            if (currentItem.TimeStamp == null) {
                itemView.tv_item_span.visibility = View.GONE
                itemView.tv_item_datetime.visibility = View.GONE
            } else {
                itemView.tv_item_span.text = currentItem.Span
                itemView.tv_item_datetime.text = LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(currentItem.TimeStamp!!), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm:ss"))
            }
            itemView.tv_item_index.text = currentItem.Span
            itemView.tv_item_text.text = currentItem.Text

            var textColor: Int = 0
            var backgroundColor: Int = 0
            when (currentItem.Color) {
                "blue" -> {
                    textColor = ContextCompat.getColor(mapplication, R.color.tt_text_light)
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_blue)
                }
                "green" -> {
                    textColor = ContextCompat.getColor(mapplication, R.color.tt_text_dark)
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_green)
                }
                "yellow" -> {
                    textColor = ContextCompat.getColor(mapplication, R.color.tt_text_dark)
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_yellow)
                }
                "orange" -> {
                    textColor = ContextCompat.getColor(mapplication, R.color.tt_text_dark)
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_orange)
                }
                "red" -> {
                    textColor = ContextCompat.getColor(mapplication, R.color.tt_text_dark)
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_red)
                }
                "purple" -> {
                    textColor = ContextCompat.getColor(mapplication, R.color.tt_text_light)
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_purple)
                }
            }
            itemView.tv_item_datetime.setTextColor(textColor)
            itemView.tv_item_text.setTextColor(textColor)
            itemView.tv_item_span.setTextColor(textColor)
            val revealCardView = itemView as CircularRevealCardView
            revealCardView.setCardBackgroundColor(backgroundColor)
            itemView.id = currentItem.Index

            itemView.setOnClickListener {listener(currentItem)}
        }
    }
    fun setItems(items: MutableList<sItem>) {
        this.mItems = items
    }
}

