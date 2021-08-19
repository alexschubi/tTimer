package com.alexschubi.ttimer

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.scale
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.recycler_view.view.*
import java.time.format.DateTimeFormatter


class RvAdapter constructor(private val activity: MainActivity, private val rVArrayList: List<Item>, val listener: ContentListener) : RecyclerView.Adapter<RvAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rVArrayList, listener)
    }

    override fun getItemCount() = rVArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind (rVArrayList: List<Item>, listener: ContentListener){
            val currentItem = rVArrayList[absoluteAdapterPosition]
            if (currentItem.Date == null) {
                itemView.tv_item_span.visibility = View.GONE
                itemView.tv_item_datetime.visibility = View.GONE
            } else {
                itemView.tv_item_span.text = currentItem.Span
                itemView.tv_item_datetime.text = currentItem.Date!!.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm"))
            }
            itemView.tv_item_index.text = currentItem.Index.toString()
            itemView.tv_item_text.text = currentItem.Text
            when (currentItem.Color) {
                "blue"-> itemView.setBackgroundResource(R.color.item_blue)
                "green" -> itemView.setBackgroundResource(R.color.item_green)
                "yellow" -> itemView.setBackgroundResource(R.color.item_yellow)
                "orange" -> itemView.setBackgroundResource(R.color.item_orange)
                "red" -> itemView.setBackgroundResource(R.color.item_red)
                "purple" -> itemView.setBackgroundResource(R.color.item_purple)
            }
            itemView.id = currentItem.Index
        }//TODO simplify Items
    }

    interface ContentListener {
        fun onItemClicked(item: Item) {}
    }
}
class SwipeToDelete(var adapter: RvAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        val item = viewHolder.itemView.id
        Log.d("RecyclerView.swiped","adapterpos $position = item.id $item")
        val editItem = Functions().getListString("Item $item")
        editItem[8] = true.toString()
        Functions().putListString("Item $item", editItem)
        Log.d("Preferences", "changed Item $item to deleted")
        Functions().getDB()
        adapter.notifyItemRemoved(position)
        Toast.makeText(viewHolder.itemView.context, "Item $item deleted", Toast.LENGTH_SHORT).show()
        Log.d("SharedPreferences", "deleted Item $item")
        Log.d("MainPrefs.size", mainPrefs.all.size.toString() + "items")
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val background = ColorDrawable()
        background.color = Color.RED
        background.setBounds(viewHolder.itemView.right+dX.toInt(),
            viewHolder.itemView.top,
            viewHolder.itemView.right,
            viewHolder.itemView.bottom)
        background.draw(c)
        val bitmp = BitmapFactory.decodeResource(mContext.resources,android.R.drawable.ic_menu_delete)
        val sbitmp = bitmp.scale(viewHolder.itemView.height, viewHolder.itemView.height, true)
        c.drawBitmap(sbitmp , (viewHolder.itemView.right - viewHolder.itemView.height).toFloat(), viewHolder.itemView.top.toFloat(), null)
    }
}
//TODO visualization
class SwipeToEdit(var adapter: RvAdapter, var rVArrayList: ArrayList<Item>) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = viewHolder.absoluteAdapterPosition
        adapter.listener.onItemClicked(rVArrayList.asReversed().get(item))
        Log.d("","")
        Log.d("FragmentManger", "create fragment_add_item...")
        //NavHostFragment.findNavController().navigate(R.id.action_ItemList_to_AddItem)
        //Toast.makeText(viewHolder.itemView.context, "Item $item editing", Toast.LENGTH_SHORT).show()
    }
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val background = ColorDrawable()
        background.color = Color.GREEN
        background.setBounds(
            viewHolder.itemView.left,
            viewHolder.itemView.top,
            (viewHolder.itemView.left+dX).toInt(),
            viewHolder.itemView.bottom
        )
        background.draw(c)
        val bitmp = BitmapFactory.decodeResource(mContext.resources,android.R.drawable.ic_menu_edit)
        val sbitmp = bitmp.scale(viewHolder.itemView.height, viewHolder.itemView.height, true)
        c.drawBitmap(sbitmp , viewHolder.itemView.left.toFloat(), viewHolder.itemView.top.toFloat(), null)
    }
}

