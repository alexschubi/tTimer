package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

class SimpleDialog {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Dialog(        title: String,
        body: Unit,
        doIfSubmit: () -> Unit
    ){
        val showDialog = remember {mutableStateOf(false)}
        Button(onClick = { showDialog.value = true }) { Text(title) }
        if(showDialog.value){
            AlertDialog(
                modifier = Modifier.fillMaxSize(),
                shape  = MaterialTheme.shapes.large.copy(CornerSize(8.dp)),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {  },
                text = { body },
                confirmButton = {
                    Button(onClick = {
                        showDialog.value = false
                        doIfSubmit
                    }){ Text("submit") }
                                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }){ Text("cancel") } }
            )
        }
    }
}