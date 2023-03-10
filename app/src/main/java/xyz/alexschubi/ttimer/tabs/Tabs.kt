package xyz.alexschubi.ttimer.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradient
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import xyz.alexschubi.ttimer.TabItem

/*
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState, count: Int) {
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        backgroundColor = lerp(MaterialTheme.colorScheme.secondary, tabs[pagerState.currentPage].tint,0.2f),
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                //Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = TabRowDefaults.IndicatorHeight * 2F,
                color = tabs[pagerState.currentPage].tint
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = {
                    Icon(painter = painterResource(id = tab.icon), contentDescription = "", tint = tab.tint)
                    //TODO use the icon as a paint and apply radial gradient to it
                },
                text = { Text(text = tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}*/

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent(tabs: List<TabItem>, pagerState: PagerState, count: Int) {
    //Indicator
    val scope = rememberCoroutineScope()
    //val backColor = lerp(tabs[pagerState.currentPage].tint, MaterialTheme.colorScheme.secondary ,0.5f)
    Row( Modifier.height(40.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center ){
        repeat(count) {
            var color = tabs[it].tint
            var backColor = MaterialTheme.colorScheme.background
            if(pagerState.currentPage==it) {
                backColor = color
                color = MaterialTheme.colorScheme.background
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(backColor)
                .clip(RectangleShape)
                .clickable { scope.launch { pagerState.animateScrollToPage(it) } }
            ){
                Icon(Icons.Filled.List, "", Modifier.fillMaxSize(), color)
            }
        }
    }
    Box(modifier = Modifier
        .background(tabs[pagerState.currentPage].tint)
        .height(4.dp)
        .fillMaxWidth()
    )

    //Content
    HorizontalPager(state = pagerState, count = count) { page ->
        tabs[page].screen()
    }
}