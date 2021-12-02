package xyz.alexschubi.ttimer.itemlist

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.circularreveal.CircularRevealFrameLayout
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import xyz.alexschubi.ttimer.*
import xyz.alexschubi.ttimer.data.sItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt
import kotlin.system.exitProcess


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
        //TODO rewrite notifications to this DB
        recyclerViewItems2.adapter = adapter2
        Log.d("localDB", "got displayList $displayItemList2")
        ItemTouchHelper(SwipeItemLeft(adapter2,displayItemList2)).attachToRecyclerView(this.recyclerViewItems2)

        view.b_add.setOnClickListener {

            NavHostFragment.findNavController(this).navigate(R.id.action_ItemList_to_AddItem)
        }

        var isAddViewRevealed = false
        view.b_add_reveal.setOnClickListener {
            val dx: Double = (b_add_reveal.x/2).toDouble()
            val dy: Double = (b_add_reveal.y/2).toDouble()
            val minRadius = Math.hypot(dx,dy).toFloat() //TODO edit item

            val addFragment = fragment_add_item()
            val mx: Double = (addFragment.requireView().x/2).toDouble()
            val my: Double = (addFragment.requireView().y/2).toDouble()
            val maxRadius = Math.hypot(mx, my).toFloat()
            if (!isAddViewRevealed){
                val animation = ViewAnimationUtils.createCircularReveal(addFragment.view,
                    (b_add_reveal.x/2).toInt(),
                    (b_add_reveal.y/2).toInt(),
                    minRadius,
                    maxRadius
                )
                addFragment.requireView().visibility = View.VISIBLE
                animation.start()
                isAddViewRevealed = true
            }else{
                isAddViewRevealed = false
            }


        }
        swipe_refresh_layout.setOnRefreshListener {
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
            var modifyItem: Item
            if (editItem.TimeStamp!=null){
                modifyItem = Item(editItem.Index,
                    editItem.Text,
                    LocalDateTime.ofInstant(editItem.TimeStamp?.let { Instant.ofEpochMilli(it) },
                        ZoneId.systemDefault()),
                    editItem.Span,
                    editItem.Notified,
                    editItem.Deleted,
                    editItem.Color
                )
            } else {
                modifyItem = Item(editItem.Index,
                    editItem.Text,
                    null,
                    null,
                    editItem.Notified,
                    editItem.Deleted,
                    editItem.Color
                )
            }
            NavHostFragment.findNavController(this.requireParentFragment())
                .navigate(fragment_item_listDirections.actionItemListToAddItem(modifyItem))
        } else {
            NavHostFragment.findNavController(this.requireParentFragment())
                .navigate(fragment_item_listDirections.actionItemListToAddItem())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //timer.cancel()
    }
}