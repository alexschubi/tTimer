package xyz.alexschubi.ttimer.screen1

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.alexschubi.ttimer.MainActivityViewModel

@Composable
fun Screen1(viewModel: MainActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState = viewModel.uiState.collectAsState()
    Button(onClick = {uiState.value.click2++}, modifier = Modifier.padding(8.dp)) {
        Text("I've been clicked ${uiState.value.click2} times")
    }
}