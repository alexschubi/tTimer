package xyz.alexschubi.ttimer.livedata

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import xyz.alexschubi.ttimer.Functions

class SwipeItem (var adapter: LiveDataRecyclerViewAdapter,
                 val fragment: LiveDataRecyclerViewFragment
                 ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val itemIndex = viewHolder.itemView.id.toLong()
        val deleteItem = adapter.mItems!!.findLast { it.Index == itemIndex }
        adapter.removeItem(deleteItem!!)
        Functions().deleteItemFromDB(itemIndex)
        Snackbar.make(fragment.requireView(), "Item removed", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                adapter.addItem(deleteItem)
                Functions().saveSItemToDB(deleteItem)
            }.show()
    }
}