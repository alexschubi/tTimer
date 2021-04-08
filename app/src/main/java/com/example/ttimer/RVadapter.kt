package com.example.ttimer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view.view.*
import java.time.format.DateTimeFormatter


class RVadapter(private val rVArrayList: ArrayList<Item>) : RecyclerView.Adapter<RVadapter.ViewHolder>(){
    //https://www.raywenderlich.com/1560485-android-recyclerview-tutorial-with-kotlin

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rVArrayList.asReversed()[position]

        holder.itemView.tv_item_index.text = currentItem.Index.toString()
        holder.itemView.tv_item_text.text = currentItem.Text
        holder.itemView.tv_item_span.text = currentItem.Span
        holder.itemView.tv_item_datetime.text = currentItem.Date.format(DateTimeFormatter.ofPattern("dd.MM.uu HH:mm"))
        holder.itemView.id = currentItem.Index
    }

    override fun getItemCount() = rVArrayList.size

    fun deleteItem(position: Int) {
        rVArrayList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init { itemView.setOnClickListener(this) }
        override fun onClick(itemView: View) {
            /*val prefIndex = itemView.id
            if(delmode) {
                //TODO select deleting and keep items
                itemView.setBackgroundResource(R.color.button_select)
                mainPrefs.edit().remove("Item $prefIndex").apply()
                Toast.makeText( itemView.context, "Item $prefIndex deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText( itemView.context, "Item Pref $prefIndex clicked", Toast.LENGTH_SHORT).show()
            }*/
        }
    }
}
class SwipeToDelete(var adapter: RVadapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        var position = viewHolder.adapterPosition
        val item = viewHolder.itemView.id
        adapter.deleteItem(position)
        mainPrefs.edit().remove("Item $item").apply()
        Toast.makeText(viewHolder.itemView.context, "Item $item deleted", Toast.LENGTH_SHORT).show()
        Log.d("RecyclerView.swiped","adapterpos $position = ID $item")
        Log.d("SharedPreferences", "deleted Item $item")
    }

}

