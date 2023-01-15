package xyz.alexschubi.ttimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import xyz.alexschubi.ttimer.tabs.Tabs
import xyz.alexschubi.ttimer.tabs.TabsContent
import xyz.alexschubi.ttimer.theme.TTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide() //TODO better way?
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
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Tabs(tabs = tabs, pagerState = pagerState)
            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }
}
