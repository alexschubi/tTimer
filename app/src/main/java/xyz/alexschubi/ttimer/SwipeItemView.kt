package xyz.alexschubi.ttimer

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeItemView(var adapter: RecyclerViewAdapter, var displayItemList: List<Item>) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = viewHolder.absoluteAdapterPosition
        //adapter.listener.onItemClicked(displayItemList.get(item))

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
        super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive)
        val background = ColorDrawable()
        background.color = ContextCompat.getColor(mContext, R.color.tt_delete)
        background.setBounds(
            (viewHolder.itemView.left+dX).toInt(),
            viewHolder.itemView.top,
            viewHolder.itemView.right,
            viewHolder.itemView.bottom
        )
        background.draw(c)
        val bitmp = BitmapFactory.decodeResource(mContext.resources,android.R.drawable.ic_menu_edit)
        val sbitmp = bitmp.scale(30, 30, true)
        c.drawBitmap(sbitmp , viewHolder.itemView.left.toFloat(), viewHolder.itemView.top.toFloat(), null)
    }
}