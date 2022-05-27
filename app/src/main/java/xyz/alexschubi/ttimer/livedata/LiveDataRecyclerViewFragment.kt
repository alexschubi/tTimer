package xyz.alexschubi.ttimer.livedata

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import kotlinx.android.synthetic.main.live_data_recycler_view_fragment.*
import xyz.alexschubi.ttimer.R
import xyz.alexschubi.ttimer.data.sItem

class LiveDataRecyclerViewFragment : Fragment() {

    var data = MutableLiveData<MutableList<sItem>>()

    companion object {
        fun newInstance() = LiveDataRecyclerViewFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.live_data_recycler_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveRecyclerView.layoutManager = LinearLayoutManager(this.context)
        val model = ViewModelProvider(this)[LiveDataRecyclerViewViewModel::class.java]

        model.liveData.observe(viewLifecycleOwner) {
            data.value = it
            liveRecyclerView.adapter!!.notifyDataSetChanged()
            Log.d("LiveData-Fragment", "recyclerview notified datasetchanged")
        }
        liveRecyclerView.adapter = LiveDataRecyclerViewAdapter(this.requireContext(), data)

    }

}