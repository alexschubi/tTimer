package xyz.alexschubi.ttimer

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.time.format.DateTimeFormatter

class RecyclerViewAdapter constructor(displayItems: MutableList<Item>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    var mItems = displayItems

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_v2, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems)
    }

    override fun getItemCount() = mItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(nItems: List<Item>) {
            val currentItem = nItems[bindingAdapterPosition]
            if (currentItem.Date == null) {
                itemView.tv_item_span.visibility = View.GONE
                itemView.tv_item_datetime.visibility = View.GONE
            } else {
                itemView.tv_item_span.text = currentItem.Span
                itemView.tv_item_datetime.text =
                    currentItem.Date!!.format(DateTimeFormatter.ofPattern("EE dd.MM.uuuu HH:mm"))
            }
            itemView.tv_item_index.text = currentItem.Span
            itemView.tv_item_text.text = currentItem.Text
            when (currentItem.Color) {
                "blue" -> itemView.setBackgroundResource(R.drawable.rounded_corner_blue)
                "green" -> itemView.setBackgroundResource(R.drawable.rounded_corner_green)
                "yellow" -> itemView.setBackgroundResource(R.drawable.rounded_corner_yellow)
                "orange" -> itemView.setBackgroundResource(R.drawable.rounded_corner_orange)
                "red" -> itemView.setBackgroundResource(R.drawable.rounded_corner_red)
                "purple" -> itemView.setBackgroundResource(R.drawable.rounded_corner_purple)
            }
            itemView.id = currentItem.Index
        }

    }
}
