package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
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
    ){


        var returnNote =  kNote()
        if (show.value) {
            AlertDialog(
                modifier = Modifier.fillMaxSize(),
                shape  = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { show.value = false },
                //title = { Text(text = "Title for Alert") },
                text = { returnNote = editItemDialogBody(note = note) },
                confirmButton = { Button(onClick = {
                    note.value = returnNote
                    show.value = false
                }){ Text("submit") } },
                dismissButton = { Button(onClick = { show.value = false }){ Text("cancel") } }
            )
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun editItemDialogBody(note: MutableState<kNote>): kNote {
        val returnNote = note.value
        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(note.value.text, TextRange.Zero))
        }
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn{
                item {
                    Text(text = "id = " + note.value.uid)
                    Text(text = "source = " + note.value.source)
                    Text(text = "edited = " + note.value.lastEdited)
                }
                item {
                    //custom markup
                    //TODO use custom text-input that can style lines separate
                    //TextMarkup().CustomTextEdit(text = "")

                    // github repo
                    TextMarkupImported1().TextHalilozercan()
                    //TODO return string-value
                    //TODO restyle

                    //plain string
                    /*OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {mtext = it},
                        value = mtext,
                        label = { Text(text = "Text")},
                    )*/
                }
                item { Text(text = "category = " + note.value.category) }
                item { Text(text = "tags = " + note.value.tags.toString()) }
                item { Text(text = "notifications" + note.value.notifications.toString()) }
            }

        }
        returnNote.text = mtext.text
        return returnNote
    }
}