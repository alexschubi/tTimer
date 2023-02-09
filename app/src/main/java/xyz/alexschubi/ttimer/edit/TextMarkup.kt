package xyz.alexschubi.ttimer.edit

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TextMarkup(text: String) {

    val ptext = "dagshjd #hjasdashsjd\n- asdhjdasj \n- www.google.com ad hjh fdsdf\n#jkgfh\n https://youtube.com/ sdjfgb sdf \n \u2022 sd dsfsd ffg "
    var sText = mutableStateOf(TextFieldValue(text, TextRange.Zero))
    var isTextEditing = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun textField(): String {

        //val tText = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(ptext, TextRange.Zero)) }
        sText = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(sText.value.text, TextRange.Zero)) }
        isTextEditing = rememberSaveable { mutableStateOf(false) }


        if (isTextEditing.value){
            EditTextField()
        } else {
            ViewTextField()
        }
        Button(onClick = {isTextEditing.value = !isTextEditing.value}) { Text(text = "toggle EDIT") }

        return sText.value.text
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
    fun EditTextField(){
        var firstFocus = 0
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused && firstFocus>1) {
                        isTextEditing.value = false
                    //TODO doesn't trigger change of textview
                    }
                    firstFocus++

                },
            onValueChange = {sText.value = it},
            value = sText.value,
            label = { Text(text = "Text") },
            visualTransformation = {textMarkup(it)},
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ViewTextField(){
        val uriHandler = LocalUriHandler.current
        Surface(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = MaterialTheme.shapes.medium,
            onClick = { isTextEditing.value = true }
        ) {

            ClickableText(
                modifier = Modifier
                    .padding(16.dp, 16.dp)
                    .fillMaxWidth(),
                text = textMarkup(sText.value.annotatedString).text,
                style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, lineHeight = 24.sp),
                onClick = {
                    isTextEditing.value = true
                    textMarkup(sText.value.annotatedString).text
                        .getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
        }
    }
}
