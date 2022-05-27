package xyz.alexschubi.ttimer.livedata

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.alexschubi.ttimer.Functions
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.date

class LiveDataRecyclerViewViewModel : ViewModel() {

    var liveData = MutableLiveData<MutableList<sItem>>()

    init {
        var itemsList = Functions().getSortedItems()
        viewModelScope.launch {
            liveData.value = itemsList
            while (true) {
                (itemsList).forEach {
                    if(it.TimeStamp != null){
                        it.Span = Functions().getSpanString(it.date()!!.toLocalDateTime())
                        Log.d("LiveData", "refreshed Item ${it.Index} to Span " +
                                itemsList!!.findLast{ tempItem ->
                                    it.Index == tempItem.Index}!!.Span)
                    }
                }
                liveData.value = itemsList
                //milliseconds * seconds * minutes
                delay(1000 * 10 * 1)
            }
        }
    }

}