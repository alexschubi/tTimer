package xyz.alexschubi.ttimer.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.alexschubi.ttimer.data.kNote


class EditItem {
    @Composable
    fun EditItemDialog(
        show: MutableState<Boolean>,
        note: MutableState<kNote>
    //TODO add text/dates/etc maybe as whole mutablestateof kItem
    ){
        var returNnote =  kNote()
        if (show.value) {
            AlertDialog(
                onDismissRequest = {
                    show.value = false
                },
                title = {
                    Text(text = "Title for Alert")
                },
                text = {
                    returNnote = editItemDialogBody(note = note)
                },
                confirmButton = { Button(onClick = {
                    note.value = returNnote
                    show.value = false
                }){ Text("confirm") } },
                dismissButton = { Button(onClick = { show.value = false }){ Text("dismiss") } }
            )
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun editItemDialogBody(note: MutableState<kNote>): kNote {
        val returnNote = note.value
        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue("example", TextRange(0, 7)))
        }


        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn{
                item { Text(text = "id=" + note.value.uid) }
                item { Text(text = "source=" + note.value.source) }
                item { TextField(
                    value = mtext,
                    onValueChange = {mtext = it}
                ) }
                item { Text(text = "edited=" + note.value.lastEdited) }
                item { Text(text = "category=" + note.value.category) }
                item { Text(text = "tags=" + note.value.tags.toString()) }
                item { Text(text = "notifications" + note.value.notifications.toString()) }
            }

        }
        returnNote.text = mtext.text
        return returnNote
    }
}