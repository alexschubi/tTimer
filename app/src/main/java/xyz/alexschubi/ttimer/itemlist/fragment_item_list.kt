package xyz.alexschubi.ttimer.itemlist

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import xyz.alexschubi.ttimer.*
import java.time.LocalDateTime
import kotlin.system.exitProcess

private lateinit var displyItemList: MutableList<Item>

class fragment_item_list : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        suppActionBar.setHomeButtonEnabled(true)
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suppActionBar.customView.sp_sortMode.visibility = View.VISIBLE

        displyItemList = Functions().sortList(getArrayList, suppPrefs.getInt("sortMode", 0))
        linearLayoutManager = LinearLayoutManager(mContext)
        view.recyclerViewItems.layoutManager = linearLayoutManager
        var mlistener = object: RvAdapter.ContentListener {
            override fun onItemClicked(item: Item) {
                super.onItemClicked(item)
                NavHostFragment.findNavController(this@fragment_item_list).navigate(fragment_item_listDirections.actionItemListToAddItem(item))
            }
        }
        var adapter = RvAdapter( displyItemList, mlistener)
        view.recyclerViewItems.adapter = adapter
        ItemTouchHelper(SwipeToDelete(adapter, displyItemList)).attachToRecyclerView(this.recyclerViewItems)
        ItemTouchHelper(SwipeToEdit(adapter, displyItemList)).attachToRecyclerView(this.recyclerViewItems)

        var getItemsList = Functions().sortMutableList(localDB.itemsDAO().getAll(), suppPrefs.getInt("sortMode", 0))
        getItemsList.toList().forEach { sItem -> if (sItem.Deleted) getItemsList.remove(sItem) }
        var displayItemList2 = getItemsList.toMutableList()
        recyclerViewItems2.layoutManager = LinearLayoutManager(context)
        var adapter2 = RecyclerViewAdapter(displayItemList2)//TODO rewrite notifications to this DB
        recyclerViewItems2.adapter = adapter2
        Log.d("localDB", "got $displayItemList2")
        ItemTouchHelper(SwipeItemLeft(adapter2,displayItemList2)).attachToRecyclerView(this.recyclerViewItems2)

        //timer.start()
        view.b_add.setOnClickListener {
            //ViewAnimationUtils.createCircularReveal(fragment_add_item().view, b_add.x.toInt(), b_add.y.toInt(), 20F,50F)
            NavHostFragment.findNavController(this).navigate(R.id.action_ItemList_to_AddItem)
        }
        swipe_refresh_layout.setOnRefreshListener {
            Functions().getDB()
            Log.d("ItemList", "getDB() and reload Fragment")
            displyItemList = Functions().sortList(getArrayList, suppPrefs.getInt("sortMode", 0))
            mainActivity.recreate()
            swipe_refresh_layout.isRefreshing = false
        }

        suppActionBar.customView.sp_sortMode.setSelection(suppPrefs.getInt("sortMode", 0))
        suppActionBar.customView.sp_sortMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

                adapter.setItems(Functions().sortList(getArrayList, suppPrefs.getInt("sortMode", 0)))
                adapter.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                null
            }
        }

    }

    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }
}