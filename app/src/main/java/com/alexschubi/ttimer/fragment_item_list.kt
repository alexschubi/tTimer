package com.alexschubi.ttimer

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlin.system.exitProcess

class fragment_item_list : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        linearLayoutManager = LinearLayoutManager(mContext)
        view.recyclerViewItems.layoutManager = linearLayoutManager
        val castArrayList: List<Item> = getArrayList.asReversed() as List<Item>
        var adapter = RvAdapter( MainActivity(), castArrayList, object: RvAdapter.ContentListener{
            override fun onItemClicked(item: Item) {
                super.onItemClicked(item)
                //Toast.makeText(mContext, "editing Item ${item.Index}", Toast.LENGTH_SHORT).show()
                NavHostFragment.findNavController(this@fragment_item_list).navigate(fragment_item_listDirections.actionItemListToAddItem(item))
            }
        })
        view.recyclerViewItems.adapter = adapter
        ItemTouchHelper(SwipeToDelete(adapter)).attachToRecyclerView(this.recyclerViewItems)
        ItemTouchHelper(SwipeToEdit(adapter, getArrayList)).attachToRecyclerView(this.recyclerViewItems)
        timer.start()

        view.b_add.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_ItemList_to_AddItem)
        }
        swipe_refresh_layout.setOnRefreshListener {

            Functions().getDB()
            Log.d("ItemList", "getDB()")
            recyclerViewItems.adapter?.notifyDataSetChanged()
            swipe_refresh_layout.isRefreshing = false
        }
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
}