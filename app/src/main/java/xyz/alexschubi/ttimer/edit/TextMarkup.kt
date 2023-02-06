package xyz.alexschubi.ttimer.edit

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import java.util.regex.Pattern

class TextMarkup {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TextEdit(ntext: String){
        val text = "dagshjd # hjasdashsjd\n- asdhjdasj \n- www.google.com ad hjh fdsdf https://youtube.com/ sdjfgb sdf \u2022 sd dsfsd ffg "
        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(text, TextRange.Zero))
        }

        val uriHandler = LocalUriHandler.current
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {mtext = it},
            value = mtext,
            label = { Text(text = "Text") },
            visualTransformation = {textMarkup(it)},
        )
    }

    private fun textMarkup(text: AnnotatedString): TransformedText{
        return TransformedText(
            buildAnnotatedString {
                append(text)


                //URL markup TODO add link
                text.split("\\s+".toRegex()).filter { word ->
                    Patterns.WEB_URL.matcher(word).matches()
                }.forEach {
                    //if word is an url
                    val startIndex = text.indexOf(it)
                    val endIndex = startIndex + it.length
                    addStyle(
                        style = SpanStyle(
                            color = Color.Blue,
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

                val uListRegex = Regex("\\n - ")
                if(uListRegex.find(text) != null){
                    Log.d("TextMarkup", "found unsorted List item")
                } else {
                    Log.d("TextMarkup", "found no unsorted List item")
                }
                text.replace(uListRegex, "\n * ")
                //list markup TODO
                //text.split("\n".toRegex()).filter { line ->
                //    Pattern.matches("^-\\s.+", line)
                //}.forEach {
                //    //val startIndex = text.indexOf(it)
                //    //val endIndex = startIndex + it.length
                //    it.replaceFirst("- ", " \u2022 ")
                //}


            },
            OffsetMapping.Identity
        )
    }


}
