package xyz.alexschubi.ttimer.livedata

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import kotlinx.android.synthetic.main.live_data_recycler_view_item.view.tv_item_datetime
import kotlinx.android.synthetic.main.live_data_recycler_view_item.view.tv_item_span
import kotlinx.android.synthetic.main.live_data_recycler_view_item.view.tv_item_text
import xyz.alexschubi.ttimer.*
import xyz.alexschubi.ttimer.data.sItem

class LiveDataRecyclerViewAdapter(
    val context: Context,
    var data: MutableLiveData<MutableList<sItem>>,
    val onItemClicked: (item: sItem, view: View) -> Unit
    ): RecyclerView.Adapter<LiveDataRecyclerViewAdapter.ViewHolder>(){

    var mItems = mutableListOf<sItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.live_data_recycler_view_item, parent, false)
        return ViewHolder(itemView, onItemClicked, mItems)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(mItems[position])
    }

    override fun getItemCount() = mItems.size


    class ViewHolder(
        itemView: View,
        val onItemClicked: (item: sItem, view: View) -> Unit,
        val mItems: MutableList<sItem>
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{

        fun bindItem(item: sItem){
            itemView.setOnClickListener(this)

            itemView.tv_item_text.text = item.Text
            if (item.TimeStamp == null){
                itemView.tv_item_datetime.visibility = View.GONE
                itemView.tv_item_span.visibility = View.GONE
            } else {
                itemView.tv_item_datetime.visibility = View.VISIBLE
                itemView.tv_item_span.visibility = View.VISIBLE
                itemView.tv_item_datetime.text = item.date()!!.format(dateFormatter)
                itemView.tv_item_span.text = item.Span
            }

            var textColorDark = false
            var backgroundColor: Int = 0
            when (item.Color) {
                "blue" -> { backgroundColor = ContextCompat.getColor(mapplication, R.color.item_blue) }
                "green" -> { textColorDark = true
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_green) }
                "yellow" -> { textColorDark = true
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_yellow) }
                "orange" -> { textColorDark = true
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_orange) }
                "red" -> { textColorDark = true
                    backgroundColor = ContextCompat.getColor(mapplication, R.color.item_red) }
                "purple" -> { backgroundColor = ContextCompat.getColor(mapplication, R.color.item_purple) }
            }
            val textColor: Int = if (textColorDark){
                ContextCompat.getColor(mapplication, R.color.tt_text_dark)
            } else{
                ContextCompat.getColor(mapplication, R.color.tt_text_light)
            }
            itemView.tv_item_datetime.setTextColor(textColor)
            itemView.tv_item_text.setTextColor(textColor)
            itemView.tv_item_span.setTextColor(textColor)
            val revealCardView = itemView as CircularRevealCardView
            revealCardView.setCardBackgroundColor(backgroundColor)
            itemView.id = item.Index.toInt()
        }

        override fun onClick(clickView: View?) {
            val item = mItems.findLast { it.Index == clickView!!.id.toLong() }
            onItemClicked(item!!, clickView!!)
        }

    }


    fun setItems(items: MutableList<sItem>) {
        mItems = items
        data.value = items
        notifyDataSetChanged()
        Log.d("RecyclerView", "set Items")
    }
    fun addItem(newItem: sItem) {
        mItems!!.add(newItem)//this use useless somehow because notifieDatachanged only apllied on data.value
        val pos = mItems!!.indexOf(mItems!!.findLast{it.Index == newItem.Index}!!)
        //data.value!!.add(newItem)
        notifyItemInserted(pos)
        Log.d("RecyclerView", "add Item ${newItem.Index} at Position $pos")
    }
    fun editItem(oldItem: sItem, newItem: sItem) {
        val pos = mItems!!.indexOf(mItems!!.findLast{it.Index == oldItem.Index}!!)
        mItems!![pos] = newItem
        data.value!![pos] = newItem
        notifyItemChanged(pos)
        Log.d("RecyclerView", "replaced Item ${oldItem.Index} with Item ${newItem.Index}at Position $pos")
    }
    fun removeItem(oldItem: sItem){
        val pos = mItems!!.indexOf(mItems!!.findLast{it.Index == oldItem.Index}!!)
        mItems!!.removeAt(pos)
        data.value!!.remove(oldItem)
        notifyItemRemoved(pos)
        Log.d("RecyclerView", "remove Item ${oldItem.Index} at Position $pos")
    }

}










