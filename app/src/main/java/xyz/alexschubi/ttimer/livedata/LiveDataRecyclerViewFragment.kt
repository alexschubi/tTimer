package xyz.alexschubi.ttimer.livedata

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.live_data_recycler_view_fragment.*
import kotlinx.android.synthetic.main.live_data_recycler_view_fragment.view.*
import xyz.alexschubi.ttimer.*
import xyz.alexschubi.ttimer.data.ItemShort
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.settings.FragmentSettings
import java.time.ZonedDateTime

class LiveDataRecyclerViewFragment : Fragment() {

    var data = MutableLiveData<MutableList<sItem>>()

    companion object {
        private var openSItem: sItem? = null
        @JvmStatic
        fun newInstance(item: ItemShort?): LiveDataRecyclerViewFragment
                = LiveDataRecyclerViewFragment().apply {
            if(item != null){
                openSItem = sItem(item.Index, item.Text, item.TimeStamp, "", item.Color, item.Notified, item.Deleted)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.live_data_recycler_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Animtaor
        val itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
        liveRecyclerView.itemAnimator = itemAnimator

        //set Adapter with observer
        liveRecyclerView.layoutManager = LinearLayoutManager(this.context)
        val adapter = LiveDataRecyclerViewAdapter(this.requireContext(), data){
            item, itemView-> displayAddItem(item, itemView)
        }
        liveRecyclerView.adapter = adapter
        val model = ViewModelProvider(this)[LiveDataRecyclerViewViewModel::class.java]
        model.liveData.observe(viewLifecycleOwner) { liveData ->
            data.value = liveData
            liveData!!.forEach {
                if (it.TimeStamp != null){
                    if (it.date()!!.isAfter(ZonedDateTime.now())){
                        this.liveRecyclerView.adapter!!.notifyItemChanged(liveData.indexOf(it))
                        Log.v("LiveData-Fragment", "recyclerview notified Item ${it.Text} changed")
                    }
                }
            }
        }


        //set SwipeListener
        ItemTouchHelper(SwipeItem(adapter, this@LiveDataRecyclerViewFragment))
            .attachToRecyclerView(liveRecyclerView)
        //set FAB listener
        view.b_add_reveal.setOnClickListener {
            displayAddItem(null, b_add_reveal)
        }

        //set settings-button listener
        b_settings.setOnClickListener {
            val positions = b_settings.getCenterPosition()
            parentFragmentManager.open {
                add(R.id.container, FragmentSettings.newInstance(positions, positions))
                addToBackStack(null)
            }
        }

        //set sortMode and litener
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
                    "sort by Date, Color ↑" -> sModeInt = 6
                    "sort by Date, Color ↓" -> sModeInt = 7
                    "sort by Color, Date ↑" -> sModeInt = 8
                    "sort by Color, Date ↓" -> sModeInt = 9
                    "sort by 0>Date, Color ↑" -> sModeInt = 10
                    "sort by 0>Date, Color ↓" -> sModeInt = 11
                    "sort by Date>0, Color ↑" -> sModeInt = 12
                    "sort by Date>0, Color ↓" -> sModeInt = 13
                    "sort by Color, Date ↑>0" -> sModeInt = 14
                    "sort by Color, Date ↓>0" -> sModeInt = 15
                    "sort by Color, 0>Date ↑" -> sModeInt = 16
                    "sort by Color, 0>Date ↓" -> sModeInt = 17
                    "sort by 0>Date ↑" -> sModeInt = 18
                    "sort by 0>Date ↓" -> sModeInt = 19
                    "sort by Date ↑>0" -> sModeInt = 20
                    "sort by Date ↓>0" -> sModeInt = 21
                    else -> Log.d("sortMode", "wrong sortModeKey String") }
                localDB.preferencesDAO().update(localDB.preferencesDAO().getLast().apply { SortMode = sModeInt })
                adapter.setItems(Functions().getSortedItemsWithSpan())
                Log.i("sortMode", "changed to $sModeInt")
                Log.d("suppPrefs", "sortMode is: "+ localDB.preferencesDAO().getLast().SortMode.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                null
            }
        }

        //open AddItem when started from notification
        if (openSItem != null) {
            displayAddItem(openSItem, b_add_reveal)
        }
    }

    fun displayAddItem(item: sItem?, view: View?) {



        var startPos = intArrayOf(0,0)
        var exitPos = intArrayOf(0,0)

        if (view != null){
            startPos = intArrayOf(view.left + view.width/2, view.top + view.height/2)
        }
        parentFragmentManager.open {
            add(R.id.container, AddItemFragment3.newInstance(startPos, exitPos,
                item, this@LiveDataRecyclerViewFragment))
            addToBackStack(null)
        }
        appbar.setExpanded(false)

    }

}