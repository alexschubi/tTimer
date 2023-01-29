package xyz.alexschubi.ttimer.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pointlessapps.rt_editor.model.RichTextValue
import com.pointlessapps.rt_editor.model.Style
import com.pointlessapps.rt_editor.ui.RichTextEditor
import com.pointlessapps.rt_editor.ui.defaultRichTextFieldStyle
import kotlin.random.Random

class TextMarkupImported1 {

    //-----------------------------------------------------
    //from https://github.com/pChochura/richtext-compose
    //-----------------------------------------------------

    @Composable
    fun TextHalilozercan(){
        var value by remember { mutableStateOf(RichTextValue.get()) }

        Row() {

            Column() {
                RichTextEditor(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    value = value,
                    onValueChange = { value = it },
                    textFieldStyle = defaultRichTextFieldStyle().copy(
                        textColor = Color.Black,
                        placeholderColor = Color.LightGray,
                        placeholder = "My rich text editor in action"
                    )
                )
            }
            Column(modifier = Modifier
                .fillMaxWidth(0.3f)) {

                EditorAction(
                    icon = Icons.Default.FormatBold,
                    active = value.currentStyles.contains(Style.Bold)
                ) {
                    value = value.insertStyle(Style.Bold)
                }
                EditorAction(
                    icon = Icons.Default.FormatUnderlined,
                    active = value.currentStyles.contains(Style.Underline)
                ) {
                    value = value.insertStyle(Style.Underline)
                }
                EditorAction(
                    icon = Icons.Default.FormatItalic,
                    active = value.currentStyles.contains(Style.Italic)
                ) {
                    value = value.insertStyle(Style.Italic)
                }
                EditorAction(
                    icon = Icons.Default.FormatStrikethrough,
                    active = value.currentStyles.contains(Style.Strikethrough)
                ) {
                    value = value.insertStyle(Style.Strikethrough)
                }
                EditorAction(
                    icon = Icons.Default.AlignHorizontalLeft,
                    active = value.currentStyles.contains(Style.AlignLeft)
                ) {
                    value = value.insertStyle(Style.AlignLeft)
                }
                EditorAction(
                    icon = Icons.Default.AlignHorizontalCenter,
                    active = value.currentStyles.contains(Style.AlignCenter)
                ) {
                    value = value.insertStyle(Style.AlignCenter)
                }
                EditorAction(
                    icon = Icons.Default.AlignHorizontalRight,
                    active = value.currentStyles.contains(Style.AlignRight)
                ) {
                    value = value.insertStyle(Style.AlignRight)
                }
                EditorAction(
                    icon = Icons.Default.FormatSize,
                    active = value.currentStyles
                        .filterIsInstance<Style.TextSize>()
                        .isNotEmpty()
                ) {
                    // Remove all styles in selected region that changes the text size
                    value = value.clearStyles(Style.TextSize())
                    // Here you would show a dialog of some sorts and allow user to pick
                    // a specific text size. I'm gonna use a random one between 50% and 200%
                    value = value.insertStyle(
                        Style.TextSize(
                            (Random.nextFloat() *
                                    (Style.TextSize.MAX_VALUE - Style.TextSize.MIN_VALUE) +
                                    Style.TextSize.MIN_VALUE).toFloat()
                        )
                    )
                }
                EditorAction(
                    icon = Icons.Default.FormatClear,
                    active = true
                ) {
                    value = value.insertStyle(Style.ClearFormat)
                }
                EditorAction(
                    icon = Icons.Default.Undo,
                    active = value.isUndoAvailable
                ) {
                    value = value.undo()
                }
                EditorAction(
                    icon = Icons.Default.Redo,
                    active = value.isRedoAvailable
                ) {
                    value = value.redo()
                }

            }


        }




    }

    @Composable
    private fun EditorAction(
        icon: ImageVector,
        active: Boolean,
        onClick: () -> Unit,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                tint = if (active) Color.Green else Color.Yellow,
                contentDescription = null
            )
        }
    }

}