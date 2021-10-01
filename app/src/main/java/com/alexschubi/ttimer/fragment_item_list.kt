package com.alexschubi.ttimer

import android.app.Activity
import android.content.Intent.getIntent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.ActivityNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
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
        //val sortMode = suppPrefs.getInt("sortMode", 0)
        suppActionBar.customView.tv_sortMode.visibility = View.VISIBLE
        //suppActionBar.customView.tv_sortMode?.text = sortMode.toString()
        displyItemList = Functions().sortList(getArrayList, suppPrefs.getInt("sortMode", 0))

        linearLayoutManager = LinearLayoutManager(mContext)
        view.recyclerViewItems.layoutManager = linearLayoutManager
        var adapter = RvAdapter( displyItemList, object: RvAdapter.ContentListener{
            override fun onItemClicked(item: Item) {
                super.onItemClicked(item)
                //TODO sort by color/date
                NavHostFragment.findNavController(this@fragment_item_list).navigate(fragment_item_listDirections.actionItemListToAddItem(item))
            }
        })
        view.recyclerViewItems.adapter = adapter
        ItemTouchHelper(SwipeToDelete(adapter, displyItemList)).attachToRecyclerView(this.recyclerViewItems)
        ItemTouchHelper(SwipeToEdit(adapter, displyItemList)).attachToRecyclerView(this.recyclerViewItems)
        timer.start()

        view.b_add.setOnClickListener {
            //ViewAnimationUtils.createCircularReveal(fragment_add_item().view, b_add.x.toInt(), b_add.y.toInt(), 20F,50F)
            NavHostFragment.findNavController(this).navigate(R.id.action_ItemList_to_AddItem)
        }
        fab_test_togglecrcl.setExpanded(false)
        fab_test_togglecrcl.setOnClickListener{
            fab_test_togglecrcl.setExpanded(!fab_test_togglecrcl.isExpanded)
        }
        swipe_refresh_layout.setOnRefreshListener {
            Functions().getDB()
            Log.d("ItemList", "getDB()")
            suppPrefs.edit().putInt("sortMode", suppPrefs.getInt("sortMode", 0) +1).apply()
            displyItemList = Functions().sortList(getArrayList, suppPrefs.getInt("sortMode", 0))
            recyclerViewItems.adapter?.notifyDataSetChanged()
            mainActivity.recreate()
            swipe_refresh_layout.isRefreshing = false
        }
        sp_sortMode.onItemSelectedListener =
        //b_settings.setOnClickListener(){ parentFragment?.findNavController()?.navigate(R.id.action_ItemList_to_fragment_settings) }
    }

    private val timer = object: CountDownTimer(1 * 60 * 60 * 1000, 1 * 10 * 1000){ //hour*min*sec*millisec
        override fun onTick(millisUntilFinished: Long){
            Functions().refreshTime()
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

 class SpinnerActivity: Activity(), AdapterView.OnItemSelectedListener{
     override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
         var sMode: String = parent?.getItemAtPosition(position) as String
         var sModeInt: Int
         when (sMode){
             "sort by None ↑" -> sModeInt = 0
             "sort by None ↑" -> sModeInt = 0
             "sort by Color ↑" -> sModeInt = 0//TODO LAST
             "sort by None ↑" -> sModeInt = 0
             "sort by None ↑" -> sModeInt = 0
             "sort by None ↑" -> sModeInt = 0
             "sort by None ↑" -> sModeInt = 0
             "sort by None ↑" -> sModeInt = 0


         }
         suppPrefs.edit().putInt("sortMode", sMode).apply()
     }

     override fun onNothingSelected(parent: AdapterView<*>?) {
         null
     }

 }
}