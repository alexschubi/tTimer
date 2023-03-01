package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import xyz.alexschubi.ttimer.data.NotificationStatus
import xyz.alexschubi.ttimer.data.kNotification
import java.time.ZonedDateTime

open class Notification {
    open val notification = mutableStateOf(kNotification())
    open val showDialog = mutableStateOf(false)

    //Dialog
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun NotificationDialog(){
        if(showDialog.value){
            AlertDialog(
                modifier = Modifier
                    .fillMaxSize(),
                shape  = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {  },
                text = { Text(text = "Add Notification") },
                confirmButton = {
                    Button(onClick = {
                        notification.value.timestamp = ZonedDateTime.now().toInstant().toEpochMilli()
                        notification.value.status = NotificationStatus.pending
                        showDialog.value = false
                    }){ Text("submit") } },
                dismissButton = { Button(onClick = {
                    showDialog.value = false
                }){ Text("cancel") } }
            )
        }
    }

}