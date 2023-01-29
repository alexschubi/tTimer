package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class TextMarkup {


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CustomTextEdit(text: String){

        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(text, TextRange.Zero))
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {mtext = it},
            value = mtext,
            label = { Text(text = "Text") },
        )



    }
}