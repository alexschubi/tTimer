package xyz.alexschubi.ttimer.livedata

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import kotlinx.android.synthetic.main.live_data_recycler_view_item.view.*
import xyz.alexschubi.ttimer.R
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.date
import xyz.alexschubi.ttimer.dateFormatter
import xyz.alexschubi.ttimer.toMilli
import java.time.ZonedDateTime

class LiveDataRecyclerViewAdapter(val context: Context, val data: LiveData<MutableList<sItem>>): Adapter<LiveDataRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.live_data_recycler_view_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data.value!![position])
    }

    override fun getItemCount() = data.value!!.size


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(model: sItem){
            itemView.tv_item_text.text = model.Text
            if (model.TimeStamp == null){
                itemView.tv_item_datetime.visibility = GONE
                itemView.tv_item_span.visibility = GONE
            } else {
                itemView.tv_item_datetime.text = model.date()!!.format(dateFormatter)
                itemView.tv_item_span.text = model.Span
            }

        }
    }
}










