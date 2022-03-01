package xyz.alexschubi.ttimer.itemlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import kotlinx.android.synthetic.main.recycler_view_item_v2.view.*
import xyz.alexschubi.ttimer.*
import xyz.alexschubi.ttimer.R.*
import xyz.alexschubi.ttimer.data.sItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class RecyclerViewAdapter(
    rVArrayList: MutableList<sItem>,
    val onItemClicked: (item: sItem, screenPos: IntArray) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    var mItems = rVArrayList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout.recycler_view_item_v2, parent, false)
        return ViewHolder(itemView, onItemClicked, mItems)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems)
    }

    override fun getItemCount() = mItems.size

    class ViewHolder(
        itemView: View,
        val onItemClicked: (item: sItem, screenPos: IntArray) -> Unit,
        val mItems: MutableList<sItem>
    )
        : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        fun bind (nItems: MutableList<sItem>){
            val currentItem = nItems[bindingAdapterPosition]
            if (currentItem.TimeStamp == null) {
                itemView.tv_item_span.visibility = View.GONE
                itemView.tv_item_datetime.visibility = View.GONE
            } else {
                itemView.tv_item_span.text = currentItem.Span
                itemView.tv_item_datetime.text = LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(currentItem.TimeStamp!!), ZoneId.systemDefault())
                    .format(dateFormatter)
            }
            itemView.tv_item_index.text = currentItem.Index.toString()
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
            itemView.id = currentItem.Index.toInt()

        }

        override fun onClick(view: View) {
            val dItem = mItems[bindingAdapterPosition]
            //val item = localDB.itemsDAO().get(view!!.id.toLong())
            val screenLocation = view.getCenterPosition()
            onItemClicked(dItem, screenLocation)
        }
    }
    fun setItems(items: MutableList<sItem>) {
        mItems = Functions().sortMutableList(items, localDB.preferencesDAO().getLast().SortMode)
        notifyDataSetChanged()
    }
    fun addItem(item: sItem){
        mItems.add(item)
        mItems = Functions().sortMutableList(mItems, localDB.preferencesDAO().getLast().SortMode)
        val index = mItems.lastIndexOf(item)
        notifyItemInserted(index)
        Log.d("RecyclerView", "addItem $item")
    }
    fun editItem(oldItem: sItem, newItem: sItem){
        val index = mItems.lastIndexOf(oldItem)
        mItems[index] = newItem
        notifyItemChanged(index)
        Log.d("RecyclerView", "editItem $newItem")
    }
    fun removeItem(item: sItem){
        val index = mItems.lastIndexOf(item)
        mItems.removeAt(index)
        notifyItemRemoved(index)
        Log.d("RecyclerView", "removeItem $item")
    }
}

