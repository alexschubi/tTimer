package xyz.alexschubi.ttimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel (app: Application): AndroidViewModel(app) {
    val uiStateSource = MutableStateFlow(MainActivityUIState())
    val uiState: StateFlow<MainActivityUIState> = uiStateSource.asStateFlow()
}