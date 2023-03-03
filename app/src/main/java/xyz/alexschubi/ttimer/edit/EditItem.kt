package xyz.alexschubi.ttimer.edit

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import xyz.alexschubi.ttimer.data.*


open class EditItem {
    open val sShowDialog = mutableStateOf(false)
    open val sNoteDialog = mutableStateOf(kNote())

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun EditItemDialog(
        showDialog: MutableState<Boolean>,
        note: MutableState<kNote>
    ): Boolean {
        //val focusManager = LocalFocusManager.current
        var returnNote =  kNote()
        if (showDialog.value) {
            AlertDialog(
                modifier = Modifier.fillMaxSize(),
                shape  = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { showDialog.value = false },
                text = { returnNote = editItemDialogBody(note = note) },
                confirmButton = { Button(onClick = {
                    note.value = returnNote
                    showDialog.value = false
                    jsonItems().saveToJson(note.value)
                    jsonSettings().registerNewItem()
                    //TODO use empty item when new note
                //TODO the data needs to get refreshed in the list
                }){ Text("submit") } },
                dismissButton = { Button(onClick = {
                    showDialog.value = false
                }){ Text("cancel") } }
            )
        }
        return false
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun editItemDialogBody(note: MutableState<kNote>): kNote {
        val returnNote = note.value
        //Dialog implememnts
        val notificationEdit: MutableState<kNotification> = remember { mutableStateOf(kNotification()) }
        val notificationShow = remember { mutableStateOf(false) }
        NotificationDialog(notification = notificationEdit, show = notificationShow)

        //Body
        Surface(
            modifier = Modifier
                .fillMaxSize()
                //.clickable { focusManager.clearFocus() }
        ) {
            //Plain Layout
            LazyColumn(Modifier
               // .clickable { focusManager.clearFocus() }
            ){
                item {
                    Text(text = "id = " + note.value.uid)
                    Text(text = "source = " + note.value.source)
                    Text(text = "edited = " + note.value.lastEdited)
                }
                //TextField
                item {
                    //custom markup
                    returnNote.text = TextMarkup(note.value.text).CombinedTextField()
                }
                //Category
                item { 
                    Text(text = "category = " + note.value.category)
                    SimpleDialog().Dialog(title = "Category", body = Text("text")) {}
                }
                item { Text(text = "tags = " + note.value.tags.toString()) }
                //Notification
                item {
                    val notifications = note.value.notifications
                    Text(text = "notifications = " + notifications.toString())
                    notifications?.forEach { notification ->
                        AssistChip(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier
                                .padding(2.dp)
                                .height(18.dp),
                            label = { Text(text = notification.timestamp.toString(), fontSize = 12.sp)},
                            onClick = {
                                notificationShow.value = true
                                notificationEdit.value = notification
                            }
                        )
                    }
                    AssistChip(
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier
                            .padding(2.dp)
                            .height(18.dp),
                        label = { Text(text = "New", fontSize = 12.sp)},
                        onClick = {
                            notificationShow.value = true
                            notificationEdit.value = kNotification(/*TODO uid*/)
                        }
                    )

                }
            }

        }
        //RETURN
        return returnNote
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun NotificationDialog(notification: MutableState<kNotification>, show: MutableState<Boolean>){
        //val sNotification = notification
        //val showDialog = remember{show}
        if(show.value){
            AlertDialog(
                modifier = Modifier.fillMaxSize(),
                shape  = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {  },
                text = {
                    //TODO LAST
                    Text(text = "uid = " + notification.value.uid)
                    Text(text = "status = " + notification.value.status)
                    Text(text = "timestamp = " + notification.value.timestamp)


                },
                confirmButton = {
                    Button(onClick = {
                        show.value = false
                        //TODO
                    }){ Text("submit") }
                },
                dismissButton = { Button(onClick = { show.value = false }){ Text("cancel") } }
            )
        }
    }



}