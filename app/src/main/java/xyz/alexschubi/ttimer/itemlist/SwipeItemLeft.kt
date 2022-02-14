package xyz.alexschubi.ttimer.itemlist

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import xyz.alexschubi.ttimer.*

class SwipeItemLeft(var adapter: RecyclerViewAdapter, private var fragmentItemList: fragment_item_list) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //val position = viewHolder.bindingAdapterPosition
        val itemIndex = viewHolder.itemView.id.toLong()
        fragmentItemList.removeItem(adapter.mItems.find { it.Index == itemIndex }!!)
        Functions().deleteItem(itemIndex)
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
        background.color = ContextCompat.getColor(mainActivity, R.color.tt_back_light)
        background.setBounds(viewHolder.itemView.right+dX.toInt(),
            viewHolder.itemView.top,
            viewHolder.itemView.right,
            viewHolder.itemView.bottom)
        //background.draw(c)
        val bitmp = BitmapFactory.decodeResource(mainActivity.resources,android.R.drawable.ic_menu_delete)
        val sbitmp = bitmp.scale(viewHolder.itemView.height, viewHolder.itemView.height, true)
        //c.drawBitmap(sbitmp , (viewHolder.itemView.right - viewHolder.itemView.height).toFloat(), viewHolder.itemView.top.toFloat(), null)
    }
}