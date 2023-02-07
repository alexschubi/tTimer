package xyz.alexschubi.ttimer.edit

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TextMarkup(text: String) {

    val text = "dagshjd #hjasdashsjd\n- asdhjdasj \n- www.google.com ad hjh fdsdf\n#jkgfh\n https://youtube.com/ sdjfgb sdf \n \u2022 sd dsfsd ffg "
    val tText = mutableStateOf(text)

    @Composable
    fun TextField() {

        val tText = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text, TextRange.Zero)) }
        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text, TextRange.Zero)) }
        var isTextEditing by rememberSaveable { mutableStateOf(false) }
        val uriHandler = LocalUriHandler.current
        val focusManager = LocalFocusManager.current


        if (isTextEditing){
            EditTextField(textField = tText)
        } else {
            ViewTextField(textField = tText)
        }
        Button(onClick = {isTextEditing = !isTextEditing}) { Text(text = "toggle EDIT + ${isTextEditing}") }

    }

    private fun textMarkup(text: AnnotatedString): TransformedText{
        return TransformedText(
            buildAnnotatedString {
                //for the FUTURE: dont change the appended text
                val ntext = text
                    .replace("\n-\\s".toRegex(), "\n\u2022 ")//list1
                    .replace("\n\u2022\\s\\s".toRegex(), "\n \u25E6 ")//list2
                append(ntext)
                //URL markup TODO add link
                ntext.split("\\s+".toRegex()).filter { word ->
                    Patterns.WEB_URL.matcher(word).matches()
                }.forEach {
                    //if word is an url
                    val startIndex = ntext.indexOf(it)
                    val endIndex = startIndex + it.length
                    addStyle(
                        style = SpanStyle(
                            //color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        ),
                        start = startIndex, end = endIndex
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = it,
                        start = startIndex,
                        end = endIndex
                    )
                }

                //heading
                ntext.split("\n+".toRegex()).filter { line ->
                    line.matches("^#.+$".toRegex())
                }.forEach {
                    val startIndex = text.indexOf(it)
                    val endIndex = startIndex + it.length
                    addStyle(
                        style = SpanStyle(
                            fontSize = 20.sp
                        ),
                        start = startIndex, end = endIndex
                    )
                    addStyle(
                        style = SpanStyle(
                            color = Color(0x8000000A)
                        ),
                        start = startIndex,
                        end = startIndex + 1
                    )
                }
            },
            OffsetMapping.Identity
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditTextField(textField: MutableState<TextFieldValue>){
        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(textField.value) }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {mtext = it},
            value = mtext,
            label = { Text(text = "Text") },
            visualTransformation = {textMarkup(it)}
        )
    }
    @Composable
    fun ViewTextField(textField: MutableState<TextFieldValue>){
        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(textField.value) }
        val uriHandler = LocalUriHandler.current
        ClickableText(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = textMarkup(mtext.annotatedString).text,
            onClick = {
                textMarkup(mtext.annotatedString).text
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            }
        )
    }
}
