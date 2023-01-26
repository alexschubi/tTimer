package xyz.alexschubi.ttimer.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.alexschubi.ttimer.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(category: Category) {
    //Variables for the dialog
    val showDialog = remember { mutableStateOf(false) }
    val editNote = remember { mutableStateOf( kNote() ) }
    EditItem().EditItemDialog(showDialog, editNote)

    //For Testing TODO remove
    val mTest = test()
    var initItemsList = remember { mutableStateOf(mTest.testdata) }.value //TODO remove test data

    val selectedList: List<kNote> = initItemsList.filter { it.category == category }
    //TODO make it selectable in the preferences to choose between separated and compact view

    val itemsList = remember { mutableStateOf( selectedList ) }
    //TODO recompose the list so its new when an item was edited
    //start of the layout
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed( itemsList.value ){ index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.secondaryContainer),
                onClick = {
                    editNote.value = item
                    showDialog.value = true
                }
            ){
                //row with Text
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item.text, modifier = Modifier.padding(8.dp, 4.dp))
                }
                //row with Notifications
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 0.dp, 2.dp, 2.dp)) {
                    //add TAGS
                    item.notifications?.forEach { notification ->
                        AssistChip(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier
                                .padding(2.dp)
                                .height(18.dp),
                            label = { Text(text = notification.timestamp.toString(), fontSize = 12.sp)},
                            onClick = { /*TODO*/ }
                        )
                    }
                }
                //row with Tags
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 0.dp, 2.dp, 2.dp)) {
                    //add TAGS
                    item.tags?.forEach { tag ->
                        AssistChip(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier
                                .padding(2.dp)
                                .height(18.dp),
                            label = { Text(text = tag.toString(), fontSize = 12.sp)},
                            onClick = { /*TODO*/ }
                        )
                    }
                    Text(text = item.uid.toString())
                }
            }
        }
    }
}