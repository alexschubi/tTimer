package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import xyz.alexschubi.ttimer.data.jsonItems
import xyz.alexschubi.ttimer.data.jsonSettings
import xyz.alexschubi.ttimer.data.kNote


open class EditItem {

    open val sShowDialog = mutableStateOf(false)
    open val sNoteDialog = mutableStateOf(kNote())


    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun EditItemDialog(
        show: MutableState<Boolean>,
        note: MutableState<kNote>
    //TODO add text/dates/etc maybe as whole mutablestateof kItem
    ): Boolean {
        //val focusManager = LocalFocusManager.current

        var returnNote =  kNote()
        if (show.value) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxSize(),
                shape  = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { show.value = false },
                text = { returnNote = editItemDialogBody(note = note) },
                confirmButton = { Button(onClick = {
                    note.value = returnNote
                    show.value = false
                    jsonItems().saveToJson(note.value)
                    jsonSettings().registerNewItem()
                    //TODO use empty item when new note
                //TODO the data needs to get refreshed in the list
                }){ Text("submit") } },
                dismissButton = { Button(onClick = { show.value = false }){ Text("cancel") } }
            )
        }
        return false
    }
    @Composable
    fun editItemDialogBody(note: MutableState<kNote>): kNote {
        val returnNote = note.value

        Surface(
            modifier = Modifier
                .fillMaxSize()
                //.clickable { focusManager.clearFocus() }
        ) {
            LazyColumn(Modifier
               // .clickable { focusManager.clearFocus() }
            ){
                item {
                    Text(text = "id = " + note.value.uid)
                    Text(text = "source = " + note.value.source)
                    Text(text = "edited = " + note.value.lastEdited)
                }
                item {
                    //custom markup
                    returnNote.text = TextMarkup(note.value.text).CombinedTextField()
                }
                item { Text(text = "category = " + note.value.category) }
                item { Text(text = "tags = " + note.value.tags.toString()) }
                item { Text(text = "notifications" + note.value.notifications.toString()) }
            }

        }
        //returnNote.text = mtext.text
        return returnNote
    }
}