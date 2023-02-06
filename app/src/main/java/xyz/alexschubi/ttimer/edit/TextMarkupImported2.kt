package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import com.dmytroshuba.dailytags.core.simple.SimpleMarkupParser
import com.dmytroshuba.dailytags.core.simple.render
import com.dmytroshuba.dailytags.markdown.rules.MarkdownRules


class TextMarkupImported2 {


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MarkupTextEdit(text: String) {
        val source = """
             # adizugasi
             ## dsfsd
             1. sadkhfvas
             2. dasdaskdasdas
             - sadasdasasd
             - asdasdasd
             - adasdasda
             - asdasda
             asdhjdasj www.google.com ad hjh fdsdf https://youtube.com/ sdjfgb
                 """.trimIndent()
        val rules = MarkdownRules.toList()
        val parser = SimpleMarkupParser()
        fun getParsedString(string: String):AnnotatedString{
            return parser
                .parse(string, rules)
                .render()
                .toAnnotatedString()
        }

        var mtext by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(getParsedString(source), TextRange.Zero))
        }
 //TODO LATEST use anothe method to reformat while typing
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                mtext = TextFieldValue(getParsedString(it.text), TextRange.Zero)
                            },
            value = mtext,
            label = { Text(text = "Text") },
        )
    }
}