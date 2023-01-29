package xyz.alexschubi.ttimer

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import xyz.alexschubi.ttimer.data.kNote
import xyz.alexschubi.ttimer.edit.EditItem
import xyz.alexschubi.ttimer.tabs.Tabs
import xyz.alexschubi.ttimer.tabs.TabsContent
import xyz.alexschubi.ttimer.theme.TTimerTheme

lateinit var mcontext: Context
lateinit var mEditItem: EditItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide() //TODO better way?
        mcontext = applicationContext
        mEditItem = EditItem()
        setContent {
            TTimerTheme{
                Surface {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MainScreen() {
    val tabs = listOf(TabItem.ItemPurple, TabItem.ItemsBlue, TabItem.ItemsGreen,TabItem.ItemsYellow, TabItem.ItemsOrange, TabItem.ItemsRed, TabItem.Settings)
    val pagerState = rememberPagerState(pageCount = tabs.size)
    //dialog values
    val showDialog = remember { mEditItem.sShowDialog }
    val noteDialog = remember { mEditItem.sNoteDialog }
    //Layout
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Tabs(tabs = tabs, pagerState = pagerState)
            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }
    EditItem().EditItemDialog(show = showDialog, note = noteDialog)
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier
                .padding(all = 16.dp)
                .align(Alignment.BottomEnd),
            shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
            onClick = {
                mEditItem.sNoteDialog.value = kNote()
                mEditItem.sShowDialog.value = true
                      },
        ) { Icon(Icons.Filled.Add, "NEW") }
    }

}
