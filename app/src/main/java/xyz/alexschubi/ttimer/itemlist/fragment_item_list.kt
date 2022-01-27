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


class fragment_item_list : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter
    var displayItemsList: MutableList<sItem>? = null
    var unsortedItems: MutableList<sItem>? = null

    companion object {
        @JvmStatic
        fun newInstance(): fragment_item_list = fragment_item_list()
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
        unsortedItems = getUnsortItems()
        displayItemsList = sortItems(unsortedItems!!)
        //set adapter for recyclerview with listeners
        recyclerViewItems2.layoutManager = LinearLayoutManager(context)
        adapter = RecyclerViewAdapter(
            displayItemsList!!,
            { item: sItem, pos: IntArray -> displayAddItem(item, pos, view) }
        )
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
        b_back.setOnClickListener {
            val positions = b_back.getCenterPosition()
            parentFragmentManager.open {
                add(R.id.container, fragment_settings.newInstance(positions, positions))
                addToBackStack(null)
            }
        }


        sp_sortMode2.setSelection(suppPrefs.getInt("sortMode", 0))
        sp_sortMode2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var sMode: String = parent?.getItemAtPosition(position) as String
                var sModeInt: Int = 0
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
                suppPrefs.edit().putInt("sortMode", sModeInt).apply()
                Log.i("sortMode", "changed to $sModeInt")
                Log.d("suppPrefs", "sortMode is: "+ suppPrefs.getInt("sortMode", 0).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                null
            }
        }

    }
    /*private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        lateinit var currentTime: LocalDateTime //TODO rewrite time refresh with corutine
        override fun onTick(millisUntilFinished: Long){
            Functions().refreshTime()
            currentTime = LocalDateTime.now()
            getArrayList.forEachIndexed { index, item ->
                if (item.Date!=null && item.Date!!.isAfter(currentTime)){
                    view?.recyclerViewItems?.adapter?.notifyItemChanged(index)
                }
            }
            view?.recyclerViewItems?.adapter?.notifyDataSetChanged()
        }//TODO use coroutine
        override fun onFinish() {
            Toast.makeText(mContext, "AFK?", Toast.LENGTH_SHORT).show()
            exitProcess(-1)
        }
    }*/

    fun sortItems(items: MutableList<sItem>): MutableList<sItem> {
        var getItemsList = items
        getItemsList.toList().forEach { sItem -> if (sItem.Deleted) getItemsList.remove(sItem) }
        return getItemsList.toMutableList()
    }
    private fun getUnsortItems(): MutableList<sItem>{ return localDB.itemsDAO().getAll()}

    private fun displayAddItem(item: sItem?, exitPosition: IntArray, view: View){
        var editItem = item
        var exitPos = exitPosition
        var startPos: IntArray = intArrayOf(0,0)

        if (editItem == null){
            val getItems = sortItems(getUnsortItems())
            val newIndex = getItems.size+1
            editItem = sItem (newIndex,
                "------------",
                null,
                null,
                "purple",
                false,
                false)

            getItems.add(editItem)
            adapter.setItems(getItems)
        }
        //val viewHolder = editItem.Index.let { recyclerViewItems2.findViewHolderForItemId(it.toLong()) }
        startPos = view.findLocationOfCenterOnTheScreen()

        parentFragmentManager.open {//TODO get position of item
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