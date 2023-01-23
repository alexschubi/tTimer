package xyz.alexschubi.ttimer.tabs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


class EditItem {

    //var show: Boolean = false
    @Composable
    fun EditItemDialog(show: MutableState<Boolean>){

        //val openDialog = remember { mutableStateOf(show) }

        if (show.value) {
            AlertDialog(
                onDismissRequest = {
                    show.value = false
                },
                title = {
                    Text(text = "Title for Alert")
                },
                text = {
                    Text(
                        "Some text describing the alert's purpose."
                    )
                },
                confirmButton = { Button(onClick = { show.value = false }){} },
                dismissButton = { Button(onClick = { show.value = false }){} }
            )
        }
    }
}