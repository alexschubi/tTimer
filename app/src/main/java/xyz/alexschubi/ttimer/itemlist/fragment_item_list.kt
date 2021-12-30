package xyz.alexschubi.ttimer.itemlist

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlinx.android.synthetic.main.list_toolbar.view.*
import xyz.alexschubi.ttimer.*
import xyz.alexschubi.ttimer.data.sItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class fragment_item_list : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager

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
        suppActionBar.setCustomView(R.layout.list_toolbar)
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(mainActivity)
        //get and sort items for recyclerview
        var getItemsList = Functions().sortMutableList(localDB.itemsDAO().getAll(), suppPrefs.getInt("sortMode", 0))
        getItemsList.toList().forEach { sItem -> if (sItem.Deleted) getItemsList.remove(sItem) }
        var displayItemList2 = getItemsList.toMutableList()
        //set adapter for recyclerview with listeners
        recyclerViewItems2.layoutManager = LinearLayoutManager(context)
        var adapter2 = RecyclerViewAdapter(displayItemList2) { item: sItem ->
            displayAddItem(item)
        }

        recyclerViewItems2.adapter = adapter2
        Log.d("localDB", "got displayList $displayItemList2")
        ItemTouchHelper(SwipeItemLeft(adapter2,displayItemList2)).attachToRecyclerView(this.recyclerViewItems2)

        view.b_add_reveal.setOnClickListener {
            val positions = intArrayOf(it.left + it.width/2, it.top + it.height/2)
            parentFragmentManager.open {//TODO exit positions from recycler view or insert with animation
                add(R.id.container, AddItemFragment.newInstance(positions, positions,null))
                addToBackStack(null)
            }
        }
        swipe_refresh_layout.setOnRefreshListener {
            mainActivity.recreate()
            swipe_refresh_layout.isRefreshing = false
        }
        suppActionBar.customView.b_settings.setOnClickListener {
            val positions = it.findLocationOfCenterOnTheScreen()
            parentFragmentManager.open {
                add(R.id.container, fragment_settings.newInstance(positions))
                addToBackStack(null)
            }
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
    private fun displayAddItem(item: sItem?){
        val editItem = item
        if (editItem!=null){
            val positions = view?.findLocationOfCenterOnTheScreen()
            parentFragmentManager.open {//TODO get position of item
                add(R.id.container, AddItemFragment.newInstance(positions, intArrayOf(0,0), item))
                addToBackStack(null)
            }

        } else {
            val positions = view?.findLocationOfCenterOnTheScreen()
            parentFragmentManager.open {//TODO get position of item
                add(R.id.container, AddItemFragment.newInstance(positions, intArrayOf(0,0), null))
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //timer.cancel()
    }
}