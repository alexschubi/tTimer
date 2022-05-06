package xyz.alexschubi.ttimer.itemlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import xyz.alexschubi.ttimer.*
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.settings.FragmentSettings


class fragment_item_list : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter
    var displayItemsList: MutableList<sItem>? = null

    companion object {
        private var openSItem: sItem? = null
        @JvmStatic
        fun newInstance(openWithItem: sItem? = null): fragment_item_list
                = fragment_item_list().apply {
            if(openWithItem != null){
                openSItem = openWithItem
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(mainActivity)
        //get and sort items for recyclerview
        displayItemsList = Functions().sortMutableList(localDB.itemsDAO().getActiveItems(), localDB.preferencesDAO().getLast().SortMode)
        //set adapter for recyclerview with listeners
        recyclerViewItems2.layoutManager = LinearLayoutManager(context)
        adapter = RecyclerViewAdapter(
            displayItemsList!!
        ) { item: sItem, pos: IntArray -> displayAddItem(item, pos, view) }
        adapter.setItems(Functions().sortMutableList(localDB.itemsDAO().getActiveItems(), localDB.preferencesDAO().getLast().SortMode))
        recyclerViewItems2.adapter = adapter
        Log.d("localDB", "got displayList $displayItemsList")
        //set swipe Listener
        ItemTouchHelper(SwipeItemLeft(adapter,this@fragment_item_list)).attachToRecyclerView(this.recyclerViewItems2)

        view.b_add_reveal.setOnClickListener {
            val positions = intArrayOf(it.left + it.width/2, it.top + it.height/2)
            parentFragmentManager.open {//TODO exit positions from recycler view or insert with animation
                add(R.id.container, AddItemFragment.newInstance(positions, positions,null, this@fragment_item_list))
                addToBackStack(null)
            }
        }
        b_settings.setOnClickListener {
            val positions = b_settings.getCenterPosition()
            parentFragmentManager.open {
                add(R.id.container, FragmentSettings.newInstance(positions, positions))
                addToBackStack(null)
            }
        }


        sp_sortMode2.setSelection(localDB.preferencesDAO().getLast().SortMode)
        sp_sortMode2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var sMode: String = parent?.getItemAtPosition(position) as String
                var sModeInt = 0
                Log.d("Spinner", "change sortMode")
                when (sMode){
                    "sort by None ↑" -> sModeInt = 0
                    "sort by None ↓" -> sModeInt = 1
                    "sort by Color ↑" -> sModeInt = 2
                    "sort by Color ↓" -> sModeInt = 3
                    "sort by Date ↑" -> sModeInt = 4
                    "sort by Date ↓" -> sModeInt = 5
                    "sort by Date>Color ↑" -> sModeInt = 6
                    "sort by Date>Color ↓" -> sModeInt = 7
                    "sort by Color>Date ↑" -> sModeInt = 8
                    "sort by Color>Date ↓" -> sModeInt = 9
                    else -> Log.d("sortMode", "wrong sortModeKey String")
                }
                localDB.preferencesDAO().update(localDB.preferencesDAO().getLast().apply { SortMode = sModeInt })
                adapter.setItems(Functions().sortMutableList(localDB.itemsDAO().getActiveItems(), sModeInt))
                Log.i("sortMode", "changed to $sModeInt")
                Log.d("suppPrefs", "sortMode is: "+ localDB.preferencesDAO().getLast().SortMode.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                null
            }
        }

        if (openSItem != null) {
            val positions = intArrayOf(b_add_reveal.left + b_add_reveal.width/2, b_add_reveal.top + b_add_reveal.height/2)
            parentFragmentManager.open {
                add(R.id.container, AddItemFragment.newInstance(positions, positions,
                    openSItem, this@fragment_item_list))
                addToBackStack(null)
            }
        }

    }

    private fun displayAddItem(item: sItem?, exitPosition: IntArray, view: View){
        var editItem = item
        var exitPos = exitPosition
        var startPos: IntArray = intArrayOf(0,0)

        if (editItem == null){
            val getItems = Functions().sortMutableList(localDB.itemsDAO().getActiveItems(), localDB.preferencesDAO().getLast().SortMode)
            val newIndex = getItems.size+1
            editItem = sItem (newIndex.toLong(),
                "------------",
                null,
                null,
                "purple",
                false,
                false)

            getItems.add(editItem)
            adapter.setItems(getItems)
        }
        exitPos[1] += view.appbar.height
        startPos = exitPos
        parentFragmentManager.open {
            add(R.id.container, AddItemFragment.newInstance(startPos, exitPos, item, this@fragment_item_list))
            addToBackStack(null)
        }
        Log.d("CircularReveal",
            "opened reveal item ${editItem.Index} from ${startPos[1]},${startPos[1]}, and closing at ${exitPos[0]},${exitPos[1]}")
    }

    fun addItem(item: sItem): IntArray? {
        adapter.addItem(item)
        return linearLayoutManager.findViewByPosition(adapter.mItems.lastIndexOf(item))
            ?.getCenterPosition()
    }
    fun editItem(oldItem: sItem, newItem: sItem): IntArray? {
        adapter.editItem(oldItem, newItem)
        return linearLayoutManager.findViewByPosition(adapter.mItems.lastIndexOf(newItem))
            ?.getCenterPosition()
    }
    fun removeItem(item: sItem){
        adapter.removeItem(item)
    }
}