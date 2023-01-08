package xyz.alexschubi.ttimer

import androidx.compose.runtime.Composable
import xyz.alexschubi.ttimer.tabs.*

typealias ComposableFun = @Composable () -> Unit
sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    object Home : TabItem(R.drawable.ic_baseline_home_24 , "Home", { HomeScreen() })
    object Discover : TabItem(R.drawable.ic_baseline_search_24, "Search", { DiscoverScreen() })
    object Settings : TabItem(R.drawable.ic_baseline_settings_24, "Setting", { SettingScreen() })
}