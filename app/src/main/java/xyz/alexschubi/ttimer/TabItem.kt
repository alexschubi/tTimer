package xyz.alexschubi.ttimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import xyz.alexschubi.ttimer.tabs.*
import xyz.alexschubi.ttimer.theme.*

sealed class TabItem(var icon: Int, var tint: Color, var title: String, var screen: @Composable () -> Unit) {
    object ItemPurple : TabItem(R.drawable.ic_baseline_table_rows_24, SectionPurple, "Home", { ItemsScreen() })
    object ItemsBlue : TabItem(R.drawable.ic_baseline_table_rows_24, SectionBlue, "Home", { ItemsScreen() })
    object ItemsGreen : TabItem(R.drawable.ic_baseline_table_rows_24, SectionGreen , "Home", { ItemsScreen() })
    object ItemsYellow : TabItem(R.drawable.ic_baseline_table_rows_24, SectionYellow , "Home", { ItemsScreen() })
    object ItemsOrange : TabItem(R.drawable.ic_baseline_table_rows_24, SectionOrange , "Home", { ItemsScreen() })
    object ItemsRed : TabItem(R.drawable.ic_baseline_table_rows_24, SectionRed , "Home", { ItemsScreen() })
    object Settings : TabItem(R.drawable.ic_baseline_settings_24, Color.Gray, "Setting", { SettingScreen() })
}