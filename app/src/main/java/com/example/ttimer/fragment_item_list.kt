package com.example.ttimer

import android.net.sip.SipSession
import android.os.Bundle
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [fragment_item_list.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_item_list : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(mContext)
        view.recyclerViewItems.layoutManager = linearLayoutManager
        var adapter = RvAdapter( MainActivity(), getArrayList, object: RvAdapter.ContentListener{
            override fun onItemClicked(item: Item) {
                super.onItemClicked(item)

                Toast.makeText(mContext, "editing Item $item", Toast.LENGTH_SHORT).show()
            }
        })
        view.recyclerViewItems.adapter = adapter
        ItemTouchHelper(SwipeToDelete(adapter)).attachToRecyclerView(this.recyclerViewItems)
        ItemTouchHelper(SwipeToEdit(adapter, getArrayList)).attachToRecyclerView(this.recyclerViewItems)

        view.b_add.setOnClickListener {
            Log.d("FragmentManger", "create fragment_add_item...")
            NavHostFragment.findNavController(this).navigate(R.id.action_ItemList_to_AddItem)
        }
        swipe_refresh_layout.setOnRefreshListener {
            Functions().getDB()
            recyclerViewItems.adapter?.notifyDataSetChanged()
            swipe_refresh_layout.isRefreshing = false
        }
    }
    fun editItem(){}

    //override fun onItemClicked(item: Item) {}
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_item_list.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_item_list().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}