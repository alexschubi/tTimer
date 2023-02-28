package xyz.alexschubi.ttimer.edit

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TextMarkup(val text: String) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CombinedTextField(): String {
        //sText = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(sText.value.text, TextRange.Zero)) }
        var isTextEditing by rememberSaveable { mutableStateOf(false) }
        //var sText by remember{ mutableStateOf(TextFieldValue(text)) }
        var sText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text = text, selection = TextRange.Zero)) }

        //COMPOSABLE TEXT FIELDS
        @Composable
        fun ViewTextField(){
            val uriHandler = LocalUriHandler.current
            Surface(
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.medium,
                onClick = { /*isTextEditing = true*/ }
            ) {

                ClickableText(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .fillMaxWidth(),
                    text = textMarkup(sText).text,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, lineHeight = 24.sp),
                    onClick = {
                        /*isTextEditing = true*/
                        textMarkup(sText).text
                            .getStringAnnotations("URL", it, it)
                            .firstOrNull()?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    }
                )
            }
        }

        @Composable
        fun EditTextField(){
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {sText = it},
                value = sText,
                label = { Text(text = "Text") },
                visualTransformation = {textMarkup(sText) },
            )
        }

        //TOGGLE FUN
        if (isTextEditing){
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {sText = it},
                value = sText,
                label = { Text(text = "Text") },
                visualTransformation = {textMarkup(sText) },
            )
        } else {
            val uriHandler = LocalUriHandler.current
            Surface(
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.medium,
                onClick = { /*isTextEditing = true*/ }
            ) {

                ClickableText(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .fillMaxWidth(),
                    text = textMarkup(sText).text,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, lineHeight = 24.sp),
                    onClick = {
                        /*isTextEditing = true*/
                        textMarkup(sText).text
                            .getStringAnnotations("URL", it, it)
                            .firstOrNull()?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    }
                )
            }
        }
        fun toggleEdit(){ isTextEditing = !isTextEditing }
        Button(onClick = { toggleEdit()}) { Text(text = "Toggle") }

        return sText.text
    }

    fun getMarkupText(): TransformedText {
        return textMarkup(TextFieldValue(text = text, selection = TextRange.Zero))
    }

    private fun textMarkup(textField: TextFieldValue): TransformedText{
        return TransformedText(
            buildAnnotatedString {
                //for the FUTURE: dont change the appended text
                val ntext = textField.annotatedString
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
                    val startIndex = ntext.indexOf(it)
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



}
