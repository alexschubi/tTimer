package xyz.alexschubi.ttimer.itemlist

import androidx.lifecycle.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import xyz.alexschubi.ttimer.Functions
import xyz.alexschubi.ttimer.data.sItem
import xyz.alexschubi.ttimer.localDB

class LiveDataViewModel: ViewModel() {
   // private var mItemsList: MutableLiveData<List<sItem>>? = null
    val liveData = MutableLiveData<MutableList<sItem>>()
    var list = MutableLiveData<MutableList<sItem>>()
    var newList = mutableListOf<sItem>()

    init {
        MainScope().launch {
            liveData.value = Functions().sortMutableList(localDB.itemsDAO().getActiveItems(), localDB.preferencesDAO().getLast().SortMode)
        }
    }

    fun add(sItem: sItem){
        newList.add(sItem)
        list.value = newList
    }

    fun remove(sItem: sItem){
        newList.remove(sItem)
        list.value = newList
    }

}